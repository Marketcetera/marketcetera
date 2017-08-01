package org.marketcetera.modules.fix;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.publisher.PublisherModuleFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickfix.Initiator;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Test {@link FixAcceptorModule} and {@link FixInitiatorModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/test.xml" })
public class FixModuleTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        createAndStartModulesIfNecessary();
        verifySessionsConnected();
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        reset();
        if(acceptorModuleUrn != null) {
            try {
                moduleManager.stop(acceptorModuleUrn);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Problem stopping acceptor module",
                                                 e);
            }
        }
        if(initiatorModuleUrn != null) {
            try {
                moduleManager.stop(initiatorModuleUrn);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Problem stopping initiator module",
                                                 e);
            }
        }
    }
    /**
     * Test starting and connecting the acceptors and initiators.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testStartAndConnect()
            throws Exception
    {
    }
    /**
     * Test that admin messages can be received in a dataflow
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testAdminInitiatorMessageDataFlow()
            throws Exception
    {
        // receive admin messages only with no blacklist/whitelist
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(true);
        fixDataRequest.setIncludeApp(false);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorDataRequest(fixDataRequest)));
        waitForMessages(5);
    }
    /**
     * Test that app messages can be received in a dataflow
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Ignore@Test
    public void testAppInitiatorMessageDataFlow()
            throws Exception
    {
        // receive app messages only with no blacklist/whitelist
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorDataRequest(fixDataRequest)));
        // we'll also need to set up a data flow for the acceptor to get the data flowing (we can use the same fix data request)
        dataFlows.add(moduleManager.createDataFlow(getAcceptorDataRequest(fixDataRequest)));
        Message appMessage = new Message("8=FIX.4.2,9=217,34=467,35=D,11="+UUID.randomUUID().toString()+",15=USD,21=3,22=1,38=8974,40=1,48=35671D857,54=2,55=FCX,58=LCVC CP,59=0,60=20150619-12:15:45,207=N");
        HeadwaterModule.getInstance(headwaterInstance).emit(appMessage);
        waitForMessages(5);
    }
    /**
     * Wait for at least the given number of messages to be received.
     *
     * @param inCount an <code>int</code> value
     * @throws Exception if an unexpected failure occurs
     */
    private void waitForMessages(final int inCount)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return receivedData.size() >= inCount;
            }}
        );
    }
    /**
     * Build a data request for the acceptor module with the given runner.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getAcceptorDataRequest(FixDataRequest inFixDataRequest)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(FixAcceptorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the acceptor module with the given runner.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getInitiatorDataRequest(FixDataRequest inFixDataRequest)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(FixAcceptorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Create the headwater module, if necessary.
     *
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createHeadwaterModule()
    {
        if(headwaterUrn == null) {
            headwaterInstance = "hw"+System.nanoTime();
            headwaterUrn = moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                                      headwaterInstance);
        }
        return headwaterUrn;
    }
    /**
     * Create the publisher module, if necessary.
     *
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createPublisherModule()
    {
        if(publisherUrn == null) {
            publisherUrn = moduleManager.createModule(PublisherModuleFactory.PROVIDER_URN,
                                                      new ISubscriber(){
                @Override
                public boolean isInteresting(Object inData)
                {
                    return true;
                }
                @Override
                public void publishTo(Object inData)
                {
                    SLF4JLoggerProxy.debug(FixModuleTest.this,
                                           "Received {}",
                                           inData);
                    synchronized(receivedData) {
                        receivedData.add(inData);
                        receivedData.notifyAll();
                    }
                }}
            );
        }
        return publisherUrn;
    }
    /**
     * Generate a fix session with the given attributes.
     *
     * @param inIndex an <code>int</code> value
     * @param inAcceptor a <code>boolean</code> value
     * @return a <code>FixSession</code> value
     */
    private FixSession generateFixSession(int inIndex,
                                          boolean inAcceptor)
    {
        String sender = inAcceptor?"TARGET"+inIndex:"MATP"+inIndex;
        String target = inAcceptor?"MATP"+inIndex:"TARGET"+inIndex;
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        FixSession fixSession = fixSessionFactory.create();
        fixSession.setAffinity(1);
        fixSession.setBrokerId("test-"+(inAcceptor?"acceptor":"initiator")+inIndex);
        fixSession.setHost(fixSettingsProvider.getAcceptorHost());
        fixSession.setIsAcceptor(inAcceptor);
        fixSession.setIsEnabled(true);
        fixSession.setName(fixSession.getBrokerId());
        fixSession.setPort(fixSettingsProvider.getAcceptorPort());
        SessionID sessionId = new SessionID(FIXVersion.FIX42.getVersion(),
                                            sender,
                                            target);
        sessions.add(sessionId);
        fixSession.setSessionId(sessionId.toString());
        fixSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                            "00:00:00");
        fixSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                            "00:00:00");
        if(!inAcceptor) {
            fixSession.getSessionSettings().put(Session.SETTING_HEARTBTINT,
                                                "1");
            fixSession.getSessionSettings().put(Initiator.SETTING_RECONNECT_INTERVAL,
                                                "1");
        }
        return fixSession;
    }
    /**
     * Verify that all test sessions are connected.
     *
     * @throws Exception if an unexpected failure occurs
     */
    private void verifySessionsConnected()
            throws Exception
    {
        for(final SessionID sessionId : sessions) {
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    Session session = Session.lookupSession(sessionId);
                    if(session == null) {
                        return false;
                    }
                    return session.isLoggedOn();
                }}
            );
        }
    }
    /**
     * Create and start the test initiator and acceptor modules, if necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void createAndStartModulesIfNecessary()
            throws Exception
    {
        if(acceptorModuleUrn == null) {
            SessionSettingsProvider acceptorSessionSettingsProvider = new SessionSettingsProvider() {
                @Override
                public SessionSettings create()
                {
                    return SessionSettingsGenerator.generateSessionSettings(Lists.newArrayList(generateFixSession(1,true),generateFixSession(2,true),generateFixSession(3,true)),
                                                                            fixSettingsProviderFactory);
                }
            };
            acceptorModuleUrn = moduleManager.createModule(FixAcceptorModuleFactory.PROVIDER_URN,
                                                           acceptorSessionSettingsProvider);
        }
        if(!moduleManager.getModuleInfo(acceptorModuleUrn).getState().isStarted()) {
            moduleManager.start(acceptorModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(acceptorModuleUrn).getState());
        }
        if(initiatorModuleUrn == null) {
            SessionSettingsProvider initiatorSessionSettingsProvider = new SessionSettingsProvider() {
                @Override
                public SessionSettings create()
                {
                    return SessionSettingsGenerator.generateSessionSettings(Lists.newArrayList(generateFixSession(1,false),generateFixSession(2,false),generateFixSession(3,false)),
                                                                            fixSettingsProviderFactory);
                }
            };
            initiatorModuleUrn = moduleManager.createModule(FixInitiatorModuleFactory.PROVIDER_URN,
                                                            initiatorSessionSettingsProvider);
            moduleManager.start(initiatorModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(initiatorModuleUrn).getState());
        }
        if(!moduleManager.getModuleInfo(initiatorModuleUrn).getState().isStarted()) {
            moduleManager.start(initiatorModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(initiatorModuleUrn).getState());
        }
    }
    /**
     * Reset the test objects.
     */
    private void reset()
    {
        synchronized(dataFlows) {
            for(DataFlowID dataFlow : dataFlows) {
                try {
                    moduleManager.cancel(dataFlow);
                } catch (Exception ignored) {}
            }
            dataFlows.clear();
        }
        synchronized(receivedData) {
            receivedData.clear();
        }
    }
    /**
     * publisher module URN
     */
    private ModuleURN publisherUrn;
    /**
     * headwater module URN
     */
    private ModuleURN headwaterUrn;
    /**
     * stores received data
     */
    private final Deque<Object> receivedData = Lists.newLinkedList();
    /**
     * data flows created during the test
     */
    private final Collection<DataFlowID> dataFlows = Lists.newArrayList();
    /**
     * provides a handle to the headwater module used to initiate the data flow
     */
    private String headwaterInstance;
    /**
     * stores generated session ids
     */
    private final Set<SessionID> sessions = Sets.newHashSet();
    /**
     * test acceptor module
     */
    private static ModuleURN acceptorModuleUrn;
    /**
     * test initiator module
     */
    private static ModuleURN initiatorModuleUrn;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * provides access to the module framework
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * provides fix settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
}

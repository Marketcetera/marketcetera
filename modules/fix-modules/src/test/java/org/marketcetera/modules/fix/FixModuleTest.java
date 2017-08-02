package org.marketcetera.modules.fix;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionTransType;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
        fixDataRequest.setIsStatusRequest(false);
        Deque<Object> receivedMessages = Lists.newLinkedList();
        // we only need a received data request since admin messages flow automatically
        dataFlows.add(moduleManager.createDataFlow(getInitiatorReceiveDataRequest(fixDataRequest,
                                                                                  receivedMessages)));
        waitForMessages(5,
                        receivedMessages);
    }
    /**
     * Test that app messages can be received in a dataflow
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testAppInitiatorMessageDataFlow()
            throws Exception
    {
        // receive app messages only with no blacklist/whitelist
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        fixDataRequest.setIsStatusRequest(false);
        // this data flow is more complicated because we need to be able to inject messages on both sides
        // this data flow is to send messages via the initiator
        String initiatorHeadwaterInstance = generateHeadwaterInstanceName();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorSendDataRequest(fixDataRequest,
                                                                               initiatorHeadwaterInstance)));
        HeadwaterModule initiatorSender = HeadwaterModule.getInstance(initiatorHeadwaterInstance);
        // this data flow is to send messages via the acceptor
        String acceptorHeadwaterInstance = generateHeadwaterInstanceName();
        dataFlows.add(moduleManager.createDataFlow(getAcceptorSendDataRequest(fixDataRequest,
                                                                              acceptorHeadwaterInstance)));
        HeadwaterModule acceptorSender = HeadwaterModule.getInstance(acceptorHeadwaterInstance);
        // this data flow is to receive messages from the initiator
        Deque<Object> initiatorMessages = Lists.newLinkedList();
        dataFlows.add(moduleManager.createDataFlow(getInitiatorReceiveDataRequest(fixDataRequest,
                                                                                  initiatorMessages)));
        // this data flow is to receive messages from the acceptor
        Deque<Object> acceptorMessages = Lists.newLinkedList();
        dataFlows.add(moduleManager.createDataFlow(getAcceptorReceiveDataRequest(fixDataRequest,
                                                                                 acceptorMessages)));
        FIXMessageFactory messageFactory = FIXVersion.FIX42.getMessageFactory();
        Message order = messageFactory.newLimitOrder(UUID.randomUUID().toString(),
                                                     Side.Buy.getFIXValue(),
                                                     new BigDecimal(1000),
                                                     new Equity("METC"),
                                                     new BigDecimal(100),
                                                     TimeInForce.GoodTillCancel.getFIXValue(),
                                                     null);
        messageFactory.addTransactionTimeIfNeeded(order);
        // mark this fine message with a session id
        FIXMessageUtil.setSessionId(order,
                                    acceptorSessions.get(1));
        acceptorSender.emit(order);
        waitForMessages(1,
                        initiatorMessages);
        // respond with an ER
        Message receivedOrder = (Message)initiatorMessages.getFirst();
        Message receivedOrderAck = FIXMessageUtil.createExecutionReport(receivedOrder,
                                                                        OrderStatus.New,
                                                                        ExecutionType.New,
                                                                        ExecutionTransType.New,
                                                                        "Ack");
        messageFactory.addTransactionTimeIfNeeded(receivedOrderAck);
        FIXMessageUtil.setSessionId(receivedOrderAck,
                                    initiatorSessions.get(1));
        initiatorSender.emit(receivedOrderAck);
        waitForMessages(1,
                        acceptorMessages);
    }
    /**
     * Generate a unique headwater instance name.
     *
     * @return a <code>String</code> value
     */
    private String generateHeadwaterInstanceName()
    {
        return "hw"+System.nanoTime();
    }
    /**
     * Wait for at least the given number of messages to be received.
     *
     * @param inCount an <code>int</code> value
     * @throws Exception if an unexpected failure occurs
     */
    private void waitForMessages(final int inCount,
                                 Deque<Object> inReceivedData)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return inReceivedData.size() >= inCount;
            }}
        );
    }
    /**
     * Build a send data request for the acceptor module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getAcceptorSendDataRequest(FixDataRequest inFixDataRequest,
                                                     String inHeadwaterInstance)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(FixAcceptorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a receive data request for the acceptor module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inReceivedData a <code>Deque&lt;Object&gt;</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getAcceptorReceiveDataRequest(FixDataRequest inFixDataRequest,
                                                        Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(FixAcceptorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a send data request for the initiator module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getInitiatorSendDataRequest(FixDataRequest inFixDataRequest,
                                                      String inHeadwaterInstance)
     {
         List<DataRequest> dataRequestBuilder = Lists.newArrayList();
         ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
         dataRequestBuilder.add(new DataRequest(headwaterUrn));
         dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                                inFixDataRequest));
         return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
     }
    /**
     * Build a receive data request for the initiator module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inReceivedData a <code>Deque&lt;Object&gt;</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getInitiatorReceiveDataRequest(FixDataRequest inFixDataRequest,
                                                         Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Create a headwater module.
     *
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createHeadwaterModule(String inHeadwaterInstance)
    {
        ModuleURN headwaterUrn = moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                                            inHeadwaterInstance);
        return headwaterUrn;
    }
    /**
     * Create a publisher module.
     *
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createPublisherModule(final Deque<Object> inDataContainer)
    {
        ModuleURN publisherUrn = moduleManager.createModule(PublisherModuleFactory.PROVIDER_URN,
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
                synchronized(inDataContainer) {
                    inDataContainer.add(inData);
                    inDataContainer.notifyAll();
                }
            }}
        );
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
            initiatorSessions.put(inIndex,
                                  sessionId);
        } else {
            acceptorSessions.put(inIndex,
                                 sessionId);
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
    }
    /**
     * 
     */
    private static final Map<Integer,SessionID> acceptorSessions = Maps.newHashMap();
    /**
     * 
     */
    private static final Map<Integer,SessionID> initiatorSessions = Maps.newHashMap();
    /**
     * data flows created during the test
     */
    private final Collection<DataFlowID> dataFlows = Lists.newArrayList();
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

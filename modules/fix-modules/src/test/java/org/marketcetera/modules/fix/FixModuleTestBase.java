package org.marketcetera.modules.fix;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.fix.FixMessageHandler;
import org.marketcetera.fix.IncomingMessagePublisher;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.publisher.PublisherModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides common behaviors for FIX module testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class FixModuleTestBase
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
        acceptorModuleUrn = FixAcceptorModuleFactory.INSTANCE_URN;
        initiatorModuleUrn = FixInitiatorModuleFactory.INSTANCE_URN;
        acceptorSessions.clear();
        initiatorSessions.clear();
        for(Broker broker : brokerService.getBrokers()) {
            if(broker.getFixSession().isAcceptor()) {
                acceptorSessions.add(new SessionID(broker.getFixSession().getSessionId()));
            } else {
                initiatorSessions.add(new SessionID(broker.getFixSession().getSessionId()));
            }
        }
        startModulesIfNecessary();
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
     * Generate a unique headwater instance name.
     *
     * @return a <code>String</code> value
     */
    protected String generateHeadwaterInstanceName()
    {
        return "hw"+System.nanoTime();
    }
    /**
     * Wait for at least the given number of messages to be received.
     *
     * @param inCount an <code>int</code> value
     * @throws Exception if an unexpected failure occurs
     */
    protected void waitForMessages(final int inCount,
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
    protected DataRequest[] getAcceptorSendDataRequest(FixDataRequest inFixDataRequest,
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
    protected DataRequest[] getAcceptorReceiveDataRequest(FixDataRequest inFixDataRequest,
                                                          final Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(FixAcceptorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        dataRequestBuilder.add(new DataRequest(FixMessageBroadcastModuleFactory.INSTANCE_URN));
        incomingMessagePublisher.addMessageListener(new FixMessageHandler() {
            @Override
            public void handleMessage(SessionID inSessionId,
                                      Message inMessage)
                                              throws Exception
            {
                inReceivedData.add(inMessage);
            }
        });
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a send data request for the initiator module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getInitiatorSendDataRequest(FixDataRequest inFixDataRequest,
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
    protected DataRequest[] getInitiatorReceiveDataRequest(FixDataRequest inFixDataRequest,
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
    protected ModuleURN createHeadwaterModule(String inHeadwaterInstance)
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
    protected ModuleURN createPublisherModule(final Deque<Object> inDataContainer)
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
                SLF4JLoggerProxy.debug(FixModuleTestBase.this,
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
     * Verify that all test sessions are connected.
     *
     * @throws Exception if an unexpected failure occurs
     */
    protected void verifySessionsConnected()
            throws Exception
    {
        for(final Broker broker : brokerService.getBrokers()) {
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    BrokerStatus brokerStatus = brokerService.getBrokerStatus(broker.getBrokerId());
                    if(brokerStatus == null) {
                        return false;
                    }
                    return brokerStatus.getLoggedOn();
                }}
            );
        }
    }
    /**
     * Start the initiator and acceptor modules, if necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void startModulesIfNecessary()
            throws Exception
    {
        if(!moduleManager.getModuleInfo(acceptorModuleUrn).getState().isStarted()) {
            moduleManager.start(acceptorModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(acceptorModuleUrn).getState());
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
    protected void reset()
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
     * acceptor sessions
     */
    protected static final Collection<SessionID> acceptorSessions = Sets.newHashSet();
    /**
     * initiator sessions
     */
    protected static final Collection<SessionID> initiatorSessions = Sets.newHashSet();
    /**
     * data flows created during the test
     */
    protected final Collection<DataFlowID> dataFlows = Lists.newArrayList();
    /**
     * test acceptor module
     */
    protected ModuleURN acceptorModuleUrn;
    /**
     * test initiator module
     */
    protected ModuleURN initiatorModuleUrn;
    /**
     * provides access to the module framework
     */
    @Autowired
    protected ModuleManager moduleManager;
    /**
     * provides access to broker services
     */
    @Autowired
    protected BrokerService brokerService;
    /**
     * test application context
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * provides access to cluster services
     */
    @Autowired
    protected ClusterService clusterService;
    /**
     * provides access to incoming messages
     */
    @Autowired
    private IncomingMessagePublisher incomingMessagePublisher;
}

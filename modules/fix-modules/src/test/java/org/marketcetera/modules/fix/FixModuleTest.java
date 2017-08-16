package org.marketcetera.modules.fix;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.publisher.ISubscriber;
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
import com.google.common.collect.Sets;

import quickfix.Message;
import quickfix.SessionID;

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
                                    acceptorSessions.iterator().next());
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
                                    initiatorSessions.iterator().next());
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
     * Verify that all test sessions are connected.
     *
     * @throws Exception if an unexpected failure occurs
     */
    private void verifySessionsConnected()
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
    private void startModulesIfNecessary()
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
     * acceptor sessions
     */
    private static final Collection<SessionID> acceptorSessions = Sets.newHashSet();
    /**
     * initiator sessions
     */
    private static final Collection<SessionID> initiatorSessions = Sets.newHashSet();
    /**
     * data flows created during the test
     */
    private final Collection<DataFlowID> dataFlows = Lists.newArrayList();
    /**
     * test acceptor module
     */
    private ModuleURN acceptorModuleUrn;
    /**
     * test initiator module
     */
    private ModuleURN initiatorModuleUrn;
    /**
     * provides access to the module framework
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}

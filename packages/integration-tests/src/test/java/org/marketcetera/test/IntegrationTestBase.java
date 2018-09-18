package org.marketcetera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.core.PlatformServices.divisionContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.MutableUserFactory;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.dao.PersistentPermissionDao;
import org.marketcetera.admin.rpc.AdminRpcUtilTest;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.PriceQtyTuple;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.fix.FixMessageHandler;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.IncomingMessagePublisher;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.fix.FixAcceptorModuleFactory;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.modules.fix.FixMessageBroadcastModuleFactory;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.publisher.PublisherModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.trade.Receiver;
import org.marketcetera.test.trade.Sender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Messages;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradeMessagePublisher;
import org.marketcetera.trade.modules.OrderConverterModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessageCachingModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessagePersistenceModuleFactory;
import org.marketcetera.trade.modules.TradeMessageBroadcastModuleFactory;
import org.marketcetera.trade.modules.TradeMessageConverterModuleFactory;
import org.marketcetera.trade.modules.TradeMessagePersistenceModuleFactory;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.grpc.StatusRuntimeException;
import junitparams.JUnitParamsRunner;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Provides common test behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest(classes=IntegrationTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public class IntegrationTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "{} beginning setup",
                              name.getMethodName());
        adminClient = generateAdminClient("test",
                                          "test");
        orderConverterModuleUrn = OrderConverterModuleFactory.INSTANCE_URN;
        tradeMessageConverterModuleUrn = TradeMessageConverterModuleFactory.INSTANCE_URN;
        tradeMessagePersistenceModuleUrn = TradeMessagePersistenceModuleFactory.INSTANCE_URN;
        transactionModuleUrn = TransactionModuleFactory.INSTANCE_URN;
        tradeMessages.clear();
        tradeService.addTradeMessageListener(tradeMessageListener);
        Broker target = null;
        for(Broker broker : brokerService.getBrokers()) {
            if(!broker.getFixSession().isAcceptor() && broker.getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        assertNotNull(target);
        selector.setSelectedBrokerId(target.getBrokerId());
        selector.setChooseBrokerException(null);
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
        verifyAllBrokersReady();
        SLF4JLoggerProxy.info(this,
                              "{} beginning",
                              name.getMethodName());
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "{} cleanup beginning",
                              name.getMethodName());
        reset();
        if(adminClient != null) {
            try {
                adminClient.stop();
            } catch (Exception ignored) {}
            adminClient = null;
        }
        try {
            tradeService.removeTradeMessageListener(tradeMessageListener);
            tradeMessages.clear();
        } finally {
            SLF4JLoggerProxy.info(this,
                                  "{} done",
                                  name.getMethodName());
        }
    }
    /**
     * Generate an {@link AdminClient} owned by user "trader".
     *
     * @return an <code>AdminClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected AdminClient generateTraderAdminClient()
            throws Exception
    {
        return generateAdminClient("trader",
                                   "trader");
    }
    /**
     * Verify the given exception is for lack of the given permission.
     *
     * @param inThrowable a <code>Throwable</code> value
     * @param inPermissionName a <code>String</code> value
     */
    protected void assertNotAuthorized(Throwable inThrowable,
                                       String inPermissionName)
    {
        assertTrue(inThrowable instanceof StatusRuntimeException);
        assertTrue(inThrowable.getMessage().contains("not authorized"));
        assertTrue(inThrowable.getMessage().contains(inPermissionName));
    }
    /**
     * Generate and create a user with no permissions.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    protected User generateUserNoPermissions(String inUsername,
                                             String inPassword)
    {
        User noPermissionUser = AdminRpcUtilTest.generateUser(inUsername,
                                                              PlatformServices.generateId());
        noPermissionUser = adminClient.createUser(noPermissionUser,
                                                  inPassword);
        return noPermissionUser;
    }
    /**
     * Generate an <code>AdminClient</code> with the given user/password.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return an <code>AdminClient</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected AdminClient generateAdminClient(String inUsername,
                                              String inPassword)
            throws Exception
    {
        AdminRpcClientParameters params = new AdminRpcClientParameters();
        params.setHostname(rpcHostname);
        params.setPort(rpcPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        AdminClient adminClient = adminClientFactory.create(params);
        adminClient.start();
        return adminClient;
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
        ,10);
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
                SLF4JLoggerProxy.debug(IntegrationTestBase.this,
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
     * Build a data request that includes the order converter and the FIX initiator.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getFullInitiatorDataSendRequest(FixDataRequest inFixDataRequest,
                                                            String inHeadwaterInstance)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessageCachingModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessagePersistenceModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request that includes the order converter and the FIX initiator.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inReceivedData a <code>Deque&lt;Object&gt;</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getFullInitiatorReceiveDataRequest(FixDataRequest inFixDataRequest,
                                                               final Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessageConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessagePersistenceModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessageBroadcastModuleFactory.INSTANCE_URN));
        for(TradeMessagePublisher tradeMessagePublisher : tradeMessagePublishers) {
            tradeMessagePublisher.addTradeMessageListener(new TradeMessageListener() {
                @Override
                public void receiveTradeMessage(TradeMessage inTradeMessage)
                {
                    inReceivedData.addLast(inTradeMessage);
                }
            });
        }
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the order converter module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getOrderConverterDataRequest(String inHeadwaterInstance,
                                                         Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the order converter module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getTradeMessageConverterDataRequest(String inHeadwaterInstance,
                                                                Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessageConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessagePersistenceModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Start the test modules, if necessary.
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
        if(!moduleManager.getModuleInfo(orderConverterModuleUrn).getState().isStarted()) {
            moduleManager.start(orderConverterModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(orderConverterModuleUrn).getState());
        }
        if(!moduleManager.getModuleInfo(tradeMessageConverterModuleUrn).getState().isStarted()) {
            moduleManager.start(tradeMessageConverterModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(tradeMessageConverterModuleUrn).getState());
        }
        if(!moduleManager.getModuleInfo(tradeMessagePersistenceModuleUrn).getState().isStarted()) {
            moduleManager.start(tradeMessagePersistenceModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(tradeMessagePersistenceModuleUrn).getState());
        }
        if(!moduleManager.getModuleInfo(transactionModuleUrn).getState().isStarted()) {
            moduleManager.start(transactionModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(transactionModuleUrn).getState());
        }
    }
    /**
     * Generate a test user.
     *
     * @return a <code>User</code> value
     */
    protected User generateUser()
    {
        User user = userFactory.create("test-user-"+System.nanoTime(),
                                  "password",
                                  "Description of the test user",
                                  true);
        user = userService.save(user);
        return user;
    }
    /**
     * Makes the given broker available.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void makeBrokerAvailable(BrokerID inBrokerId)
            throws Exception
    {
        Broker broker = brokerService.getBroker(inBrokerId);
        assertNotNull(broker);
        reportBrokerStatus(broker,
                           FixSessionStatus.CONNECTED,
                           true);
    }
    /**
     * Reports the broker status as indicated.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inFixSessionStatus a <code>FixSessionStatus</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void reportBrokerStatus(Broker inBroker,
                                    FixSessionStatus inFixSessionStatus,
                                    boolean inIsLoggedOn)
            throws Exception
    {
        brokerService.reportBrokerStatus(brokerService.generateBrokerStatus(inBroker.getFixSession(),
                                                                            clusterService.getInstanceData(),
                                                                            FixSessionStatus.CONNECTED,
                                                                            true));
    }
    /**
     * Generate a test order single.
     *
     * @return an <code>OrderSingle</code> value
     */
    protected OrderSingle generateOrder()
    {
        return generateOrder(null);
    }
    /**
     * Generate a test order single with the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return an <code>OrderSingle</code> value
     */
    protected OrderSingle generateOrder(BrokerID inBrokerId)
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setBrokerID(inBrokerId);
        order.setInstrument(new Equity("METC"));
        order.setOrderType(OrderType.Market);
        order.setQuantity(BigDecimal.TEN);
        order.setSide(Side.Buy);
        return order;
    }
    /**
     * Verify that the order status is NEW for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusNew(final OrderID inRootOrderId,
                                               final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.New);
    }
    /**
     * Verify that the order status is FILLED for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusFilled(final OrderID inRootOrderId,
                                                  final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.Filled);
    }
    /**
     * Verify that the order status is PARTIALLY FILLED for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusPartiallyFilled(final OrderID inRootOrderId,
                                                           final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.PartiallyFilled);
    }
    /**
     * Verify that the order status for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @param inExpectedOrderStatus an <code>OrderStatus</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatus(final OrderID inRootOrderId,
                                            final OrderID inOrderId,
                                            final OrderStatus inExpectedOrderStatus)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                OrderSummary orderStatus = orderSummaryService.findByRootOrderIdAndOrderId(inRootOrderId,
                                                                                          inOrderId);
                if(orderStatus == null) {
                    return false;
                }
                return orderStatus.getOrderStatus() == inExpectedOrderStatus;
            }
        },10);
    }
    /**
     * Wait for and return the next trade message.
     *
     * @return a <code>TradeMessage</code> value
     * @throws Exception if the message does not arrive
     */
    protected TradeMessage waitForNextTradeMessage()
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !tradeMessages.isEmpty();
            }
        },10);
        return tradeMessages.remove(0);
    }
    /**
     * 
     *
     *
     * @param inSessionId
     * @return
     * @throws Exception
     */
    protected Message waitForReceiverMessage(SessionID inSessionId)
            throws Exception
    {
        return receiver.getNextApplicationMessage(inSessionId);
    }
    /**
     * 
     *
     *
     * @param inSessionId
     * @return
     * @throws Exception
     */
    protected Message waitForSenderMessage(SessionID inSessionId)
            throws Exception
    {
        return sender.getNextApplicationMessage(inSessionId);
    }
    /**
     * Verify that all brokers are connected.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyAllBrokersReady()
            throws Exception
    {
        for(FixSession fixSession : fixSessionProvider.findFixSessions()) {
            verifySessionLoggedOn(new BrokerID(fixSession.getBrokerId()));
        }
    }
    /**
     * Verify that the given session is disabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDisabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verify that the given session is deleted.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDeleted(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                return status == null || status.getStatus() == FixSessionStatus.DELETED;
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                if(session == null) {
                    return false;
                }
                if(!session.isLoggedOn()) {
                    return false;
                }
                if(DateTime.now().isBefore(new DateTime(session.getStartTime()).plusSeconds(2))) {
                    return false;
                }
                return true;
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                return session == null || !session.isLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is enabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionEnabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verifies that the given broker matches the given status.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inExpectedStatus a <code>Boolean</code> value or <code>null</code>
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyBrokerStatus(BrokerID inBrokerId,
                                      Boolean inExpectedStatus)
            throws Exception
    {
        BrokerStatus brokerStatus = brokerService.getBrokerStatus(inBrokerId);
        if(inExpectedStatus == null) {
            assertNull(brokerStatus);
        } else {
            assertEquals(inExpectedStatus,
                         brokerStatus.getLoggedOn());
        }
    }
    /**
     * Get an initiator broker.
     *
     * @return a <code>Broker</code> value
     */
    protected Broker getInitiator()
    {
        for(FixSession fixSession : fixSessionProvider.findFixSessions(false,1,1)) {
            return brokerService.getBroker(new BrokerID(fixSession.getBrokerId()));
        }
        throw new UnsupportedOperationException("No initiators configured!");
    }
    /**
     * Get the trader user.
     *
     * @return a <code>User</code> value
     */
    protected User getTraderUser()
    {
        User trader = userService.findByName("trader");
        assertNotNull(trader);
        return trader;
    }
    /**
     * Get the FIX versions for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object fixVersionParameters()
    {
        List<Object> results = Lists.newArrayList();
        for(FIXVersion fixVersion : getFixVersionList()) {
            results.add(new Object[] { fixVersion });
        }
        return results.toArray();
    }
    /**
     * Generates a message with the given comma-separated fields.
     *
     * @param inHeaderFields a <code>String</code> value
     * @param inFields a <code>String</code> value
     * @param inMsgType a <code>String</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static Message buildMessage(String inHeaderFields,
                                          String inFields,
                                          String inMsgType,
                                          FIXMessageFactory inFactory)
            throws Exception
    {
        Map<Integer,String> fields = new HashMap<>();
        Message message = inFactory.createMessage(inMsgType);
        String[] pairs = inHeaderFields.split(",");
        if(pairs != null) {
            for(String pair : pairs) {
                pair = StringUtils.trimToNull(pair);
                String[] components = pair.split("=");
                fields.put(Integer.parseInt(components[0]),
                           components[1]);
            }
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.getHeader().setString(entry.getKey(),
                                          entry.getValue());
        }
        fields.clear();
        pairs = inFields.split(",");
        for(String pair : pairs) {
            pair = StringUtils.trimToNull(pair);
            String[] components = pair.split("=");
            fields.put(Integer.parseInt(components[0]),
                       components[1]);
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.setString(entry.getKey(),
                              entry.getValue());
        }
        return message;
    }
    /**
     * Generate an execution report based on the given inputs.
     *
     * @param inOrder a <code>Message</code> value
     * @param inOrderData an <code>OrderData</code> value
     * @param inOrderId a <code>String</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inExecutionType an <code>ExecutionType</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message generateExecutionReport(Message inOrder,
                                              CalculatedOrderData inOrderData,
                                              String inOrderId,
                                              org.marketcetera.trade.OrderStatus inOrderStatus,
                                              ExecutionType inExecutionType,
                                              FIXMessageFactory inFactory)
            throws Exception
    {
        return generateExecutionReport(inOrder,
                                       inOrderData,
                                       inOrderId,
                                       inOrder.getString(quickfix.field.ClOrdID.FIELD),
                                       inOrder.isSetField(quickfix.field.OrigClOrdID.FIELD)?inOrder.getString(quickfix.field.OrigClOrdID.FIELD):null,
                                       inOrderStatus,
                                       inExecutionType,
                                       inFactory);
    }
    /**
     * 
     *
     *
     * @param inOrder
     * @param inOrderData
     * @param inClOrdId
     * @param inOrigClOrdId
     * @param inOrderId
     * @param inOrderStatus
     * @param inExecutionType
     * @param inFactory
     * @return
     * @throws Exception
     */
    protected Message generateExecutionReport(Message inOrder,
                                              CalculatedOrderData inOrderData,
                                              String inOrderId,
                                              String inClOrdId,
                                              String inOrigClOrdId,
                                              org.marketcetera.trade.OrderStatus inOrderStatus,
                                              ExecutionType inExecutionType,
                                              FIXMessageFactory inFactory)
            throws Exception
    {
        boolean commaNeeded = false;
        StringBuilder body = new StringBuilder();
        if(inOrder.isSetField(quickfix.field.Account.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Account.FIELD).append('=').append(inOrder.getString(quickfix.field.Account.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.AvgPx.FIELD).append('=').append(inOrderData.calculateAveragePrice().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ClOrdID.FIELD).append('=').append(inClOrdId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.CumQty.FIELD).append('=').append(inOrderData.calculateCumQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecID.FIELD).append('=').append(PlatformServices.generateId()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecTransType.FIELD).append('=').append(quickfix.field.ExecTransType.NEW); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastPx.FIELD).append('=').append(inOrderData.calculateLastPx().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastQty.FIELD).append('=').append(inOrderData.calculateLastQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderID.FIELD).append('=').append(inOrderId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderQty.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.OrderQty.FIELD).toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdStatus.FIELD).append('=').append(inOrderStatus.getFIXValue()); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.OrdType.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdType.FIELD).append('=').append(inOrder.getChar(quickfix.field.OrdType.FIELD)); commaNeeded = true;
        }
        if(inOrigClOrdId != null) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrigClOrdID.FIELD).append('=').append(inOrigClOrdId); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.Price.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Price.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.Price.FIELD).toPlainString()); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Side.FIELD).append('=').append(inOrder.getChar(quickfix.field.Side.FIELD)); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Symbol.FIELD).append('=').append(inOrder.getString(quickfix.field.Symbol.FIELD)); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.Text.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Text.FIELD).append('=').append(inOrder.getString(quickfix.field.Text.FIELD)); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.TimeInForce.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.TimeInForce.FIELD).append('=').append(inOrder.getChar(quickfix.field.TimeInForce.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.TransactTime.FIELD).append('=').append(TimeFactoryImpl.FULL_MILLISECONDS.print(System.currentTimeMillis())); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecType.FIELD).append('=').append(inExecutionType.getFIXValue()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LeavesQty.FIELD).append('=').append(inOrderData.calculateLeavesQty().toPlainString()); commaNeeded = true;
        return buildMessage(MsgType.FIELD+"="+MsgType.EXECUTION_REPORT,
                            body.toString(),
                            MsgType.EXECUTION_REPORT,
                            inFactory);
    }
    /**
     * Get list of FIX versions to use for testing.
     *
     * @return a <code>List&lt;FIXVersion&gt;</code> value
     */
    private List<FIXVersion> getFixVersionList()
    {
        List<FIXVersion> fixVersions = Lists.newArrayList();
        for(FIXVersion fixVersion : FIXVersion.values()) {
            if(fixVersion != FIXVersion.FIX_SYSTEM) {
                fixVersions.add(fixVersion);
            }
        }
        return fixVersions;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public class CalculatedOrderData
    {
        /**
         * Add a price/qty pair.
         *
         * @param inPrice a <code>BigDecimal</code> value
         * @param inQty a <code>BigDecimal</code> value
         */
        public void add(BigDecimal inPrice,
                        BigDecimal inQty)
        {
            tuples.add(new PriceQtyTuple(inPrice,inQty));
        }
        /**
         * Add the given execution to the calculation list.
         *
         * @param inExecution a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public void addExecution(Message inExecution)
                throws Exception
        {
            if(inExecution.isSetField(quickfix.field.LastPx.FIELD) && inExecution.isSetField(quickfix.field.LastQty.FIELD)) {
                add(inExecution.getDecimal(quickfix.field.LastPx.FIELD),
                    inExecution.getDecimal(quickfix.field.LastQty.FIELD));
            }
        }
        /**
         * Calculate the average price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateAveragePrice()
        {
            BigDecimal result = BigDecimal.ZERO;
            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal extendedQty = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                totalQty = totalQty.add(amount.getQty());
                extendedQty = extendedQty.add(amount.getPrice().multiply(amount.getQty()));
            }
            if(totalQty.compareTo(BigDecimal.ZERO) != 0) {
                result = extendedQty.divide(totalQty,
                                            divisionContext);
            }
            return result;
        }
        /**
         * Calculate the cumulative quantity from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateCumQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                result = result.add(amount.getQty());
            }
            return result;
        }
        /**
         * Calculate the leaves quantity from the order data.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLeavesQty()
        {
            return orderQuantity.subtract(calculateCumQty());
        }
        /**
         * Calculate the last price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastPx()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getPrice();
            }
            return result;
        }
        /**
         * Calculate the last qty from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getQty();
            }
            return result;
        }
        /**
         * Get the orderPrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderPrice()
        {
            return orderPrice;
        }
        /**
         * Sets the orderPrice value.
         *
         * @param inOrderPrice a <code>BigDecimal</code> value
         */
        public void setOrderPrice(BigDecimal inOrderPrice)
        {
            orderPrice = inOrderPrice;
        }
        /**
         * Get the orderQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderQuantity()
        {
            return orderQuantity;
        }
        /**
         * Sets the orderQuantity value.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         */
        public void setOrderQuantity(BigDecimal inOrderQuantity)
        {
            orderQuantity = inOrderQuantity;
        }
        /**
         * Generate an order status request for the given order sent from the given session.
         *
         * @param inOrderMessage a <code>Message</code> value
         * @param inSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         */
        public Message generateOrderStatusRequest(Message inOrderMessage,
                                                  SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inOrderMessage);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_STATUS_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderCancel(Message inOrderMessage,
                                           SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(PlatformServices.generateId()));
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderReplace(Message inOrderMessage,
                                            SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(PlatformServices.generateId()));
            orderMessages.add(order);
            return order;
        }
        /**
         * Generate an order with the given instrument targeted to the given session.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inSenderSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message generateOrder(Instrument inInstrument,
                                     SessionID inSenderSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSenderSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            String account = PlatformServices.generateId();
            String clOrdId = PlatformServices.generateId();
            StringBuilder body = new StringBuilder();
            body.append(quickfix.field.Account.FIELD).append('=').append(account).append(',');
            body.append(quickfix.field.ClOrdID.FIELD).append('=').append(clOrdId).append(',');
            body.append(quickfix.field.HandlInst.FIELD).append('=').append(quickfix.field.HandlInst.MANUAL_ORDER).append(',');
            body.append(quickfix.field.OrderQty.FIELD).append('=').append(orderQuantity.toPlainString()).append(',');
            if(orderType != null) {
                body.append(quickfix.field.OrdType.FIELD).append('=').append(orderType.getFIXValue()).append(',');
            }
            if(orderPrice != null) {
                body.append(quickfix.field.Price.FIELD).append('=').append(orderPrice.toPlainString()).append(',');
            }
            body.append(quickfix.field.Side.FIELD).append('=').append(side.getFIXValue()).append(',');
            Message order = buildMessage("35="+MsgType.ORDER_SINGLE,
                                         body.toString(),
                                         MsgType.ORDER_SINGLE,
                                         factory);
            order.setField(new quickfix.field.TransactTime(new Date()));
            InstrumentToMessage<?> instrumentFunction = InstrumentToMessage.SELECTOR.forInstrument(inInstrument);
            DataDictionary fixDictionary = FIXMessageUtil.getDataDictionary(inSenderSessionId);
            if(!instrumentFunction.isSupported(fixDictionary,
                                               quickfix.field.MsgType.ORDER_SINGLE)) {
                throw new I18NException(Messages.UNSUPPORTED_INSTRUMENT);
            }
            instrumentFunction.set(inInstrument,
                                   fixDictionary,
                                   quickfix.field.MsgType.ORDER_SINGLE,
                                   order);
            orderMessages.add(order);
            return order;
        }
        /**
         * Create a new CalculatedOrderData instance.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         * @param inOrderPrice a <code>BigDecimal</code> value or <code>null</code>
         * @param inOrderType an <code>OrderType</code> value
         * @param inSide a <code>Side</code> value
         */
        public CalculatedOrderData(BigDecimal inOrderQuantity,
                                   BigDecimal inOrderPrice,
                                   OrderType inOrderType,
                                   Side inSide)
        {
            orderQuantity = inOrderQuantity;
            orderPrice = inOrderPrice;
            orderType = inOrderType;
            side = inSide;
        }
        /**
         * collection of price/qty tuples
         */
        private final List<PriceQtyTuple> tuples = new ArrayList<>();
        /**
         * order quantity value
         */
        protected BigDecimal orderQuantity;
        /**
         * 
         */
        protected BigDecimal orderPrice;
        protected String orderId;
        protected final OrderType orderType;
        protected final Side side;
        protected final Deque<Message> orderMessages = Lists.newLinkedList();
        protected final List<Message> executionMessages = Lists.newArrayList();
    }
    /**
     * holds trade messages received during a test
     */
    protected final List<TradeMessage> tradeMessages = Lists.newArrayList();
    /**
     * listens for trade messages received during a test
     */
    protected final TradeMessageListener tradeMessageListener = new TradeMessageListener() {
        @Override
        public void receiveTradeMessage(TradeMessage inTradeMessage)
        {
            synchronized(tradeMessages) {
                tradeMessages.add(inTradeMessage);
                tradeMessages.notifyAll();
            }
        }
    };
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname:127.0.0.1}")
    protected String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port:18999}")
    protected int rpcPort = 18999;
    /**
     * provides access to admin services
     */
    protected AdminClient adminClient;
    /**
     * provides data store access to permission objects
     */
    @Autowired
    protected PersistentPermissionDao permissionDao;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    protected PermissionFactory permissionFactory;
    /**
     * creates {@link AdminClient} objects
     */
    @Autowired
    protected AdminRpcClientFactory adminClientFactory;
    /**
     * provides access to authorization services
     */
    @Autowired
    protected AuthorizationService authorizationService;
    /**
     * creates {@link MutableUser} objects
     */
    @Autowired
    protected MutableUserFactory userFactory;
    /**
     * provides access to module services
     */
    @Autowired
    protected ModuleManager moduleManager;
    /**
     * broker selector
     */
    @Autowired
    protected TestBrokerSelector selector;
    /**
     * test FIX brokers (receives requests from initiators)
     */
    @Autowired
    protected Receiver receiver;
    /**
     * test FIX brokers (sends requests to acceptors)
     */
    @Autowired
    protected Sender sender;
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * provides access to report services
     */
    @Autowired
    protected ReportService reportService;
    /**
     * provides access to broker services
     */
    @Autowired
    protected BrokerService brokerService;
    /**
     * provides access to FIX sessions
     */
    @Autowired
    protected FixSessionProvider fixSessionProvider;
    /**
     * provides access to trade services
     */
    @Autowired
    protected TradeService tradeService;
    /**
     * provides access to order summary services
     */
    @Autowired
    protected OrderSummaryService orderSummaryService;
    /**
     * provides access to cluster services
     */
    @Autowired
    protected ClusterService clusterService;
    /**
     * test application context
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * trade message publishers value
     */
    @Autowired
    protected Collection<TradeMessagePublisher> tradeMessagePublishers = Lists.newArrayList();
    /**
     * test order converter module
     */
    protected ModuleURN orderConverterModuleUrn;
    /**
     * test trade message converter module
     */
    protected ModuleURN tradeMessageConverterModuleUrn;
    /**
     * test trade message persistence module
     */
    protected ModuleURN tradeMessagePersistenceModuleUrn;
    /**
     * transaction module URN
     */
    protected ModuleURN transactionModuleUrn;
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
     * provides access to incoming messages
     */
    @Autowired
    private IncomingMessagePublisher incomingMessagePublisher;
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}

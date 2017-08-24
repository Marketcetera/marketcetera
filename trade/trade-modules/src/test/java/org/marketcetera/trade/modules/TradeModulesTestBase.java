package org.marketcetera.trade.modules;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradeMessagePublisher;
import org.marketcetera.trade.service.TradeServerTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides common behavior for trade modules tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=TradeModuleTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public abstract class TradeModulesTestBase
        extends TradeServerTestBase
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
        orderConverterModuleUrn = OrderConverterModuleFactory.INSTANCE_URN;
        tradeMessageConverterModuleUrn = TradeMessageConverterModuleFactory.INSTANCE_URN;
        tradeMessagePersistenceModuleUrn = TradeMessagePersistenceModuleFactory.INSTANCE_URN;
        transactionModuleUrn = TransactionModuleFactory.INSTANCE_URN;
        super.setup();
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
     * Start the order converter module, if necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void startModulesIfNecessary()
            throws Exception
    {
        super.startModulesIfNecessary();
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
     * trade message publishers value
     */
    @Autowired
    protected Collection<TradeMessagePublisher> tradeMessagePublishers = Lists.newArrayList();
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
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
     * creates user objects
     */
    @Autowired
    protected UserFactory userFactory;
}

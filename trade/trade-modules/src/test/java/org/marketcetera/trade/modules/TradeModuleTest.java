package org.marketcetera.trade.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.brokers.Broker;
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
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.marketcetera.trade.service.TradeTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import quickfix.Message;

/* $License$ */

/**
 * Test {@link OrderConverterModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/test.xml" })
public class TradeModuleTest
        extends TradeTestBase
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
        orderConverterModuleUrn = OrderConverterModuleFactory.INSTANCE_URN;
        startModulesIfNecessary();
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
     * Test the wrong data type.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testWrongDataType()
            throws Exception
    {
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getDataRequest(headwaterInstance,
                                                                          receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(this,
                                                            dataFlow);
        assertTrue(receivedData.isEmpty());
    }
    /**
     * Test that an order can be targeted and converted for a specific broker.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testSpecifiedBroker()
            throws Exception
    {
        Broker target = null;
        for(Broker broker : brokerService.getBrokers()) {
            if(!broker.getFixSession().isAcceptor() && broker.getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        makeBrokerAvailable(target.getBrokerId());
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(target.getBrokerId());
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getDataRequest(headwaterInstance,
                                                                          receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(testOrder,
                                                            dataFlow);
        waitForMessages(1,
                        receivedData);
        Message convertedMessage = (Message)receivedData.getFirst();
        assertNotNull(convertedMessage);
    }
    /**
     * Test that an order can be targeted and converted with no broker selected.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testNoSpecifiedBroker()
            throws Exception
    {
        Broker target = null;
        for(Broker broker : brokerService.getBrokers()) {
            if(!broker.getFixSession().isAcceptor() && broker.getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        makeBrokerAvailable(target.getBrokerId());
        TestBrokerSelector selector = applicationContext.getBean(TestBrokerSelector.class);
        selector.setSelectedBrokerId(target.getBrokerId());
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getDataRequest(headwaterInstance,
                                                                          receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(testOrder,
                                                            dataFlow);
        waitForMessages(1,
                        receivedData);
        Message convertedMessage = (Message)receivedData.getFirst();
        assertNotNull(convertedMessage);
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
     * Build a data request for the order converter module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getDataRequest(String inHeadwaterInstance,
                                         Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
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
                SLF4JLoggerProxy.debug(TradeModuleTest.this,
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
     * Start the initiator and acceptor modules, if necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void startModulesIfNecessary()
            throws Exception
    {
        if(!moduleManager.getModuleInfo(orderConverterModuleUrn).getState().isStarted()) {
            moduleManager.start(orderConverterModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(orderConverterModuleUrn).getState());
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
     * data flows created during the test
     */
    private final Collection<DataFlowID> dataFlows = Lists.newArrayList();
    /**
     * test acceptor module
     */
    private ModuleURN orderConverterModuleUrn;
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

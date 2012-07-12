package org.marketcetera.strategy;

import static org.junit.Assert.*;
import static org.marketcetera.strategy.Messages.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.*;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.strategy.StrategyTestBase.MockClient;
import org.marketcetera.trade.*;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;
import org.marketcetera.util.test.CollectionAssert;

import quickfix.Message;

/* $License$ */

/**
 * Tests {@link AbstractRunningStrategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class AbstractRunningStrategyTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        try {
            ClientManager.setClientFactory(new MockClient.MockClientFactory());
            ClientManager.init(null);
        } catch (ClientInitException ignored) {}
        OrderHistoryManagerTest.once();
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void before()
            throws Exception
    {
        strategy = new MockRunningStrategy();
        strategy.start();
        strategy.setStrategy(new MockStrategy());
        servicesProvider = new MockServicesProvider();
        factory = Factory.getInstance();
        reset();
    }
    /**
     * Tests {@link AbstractRunningStrategy#send(Object)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test@Ignore
    public void testSend()
            throws Exception
    {
        // send something that isn't an OrderSingle
        assertTrue(strategy.send(this));
        verifySentObjects(new Object[] { this } );
        reset();
        // send null
        assertFalse(strategy.send(null));
        verifyLoggedEvents(new String[] { INVALID_DATA.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] {} );
        reset();
        // create an order with no order ID
        OrderSingle order = factory.createOrderSingle();
        order.setOrderID(null);
        assertNull(order.getOrderID());
        assertFalse(strategy.send(order));
        verifyLoggedEvents(new String[] { INVALID_ORDER.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] {} );
        reset();
        // create an invalid Order
        order = factory.createOrderSingle();
        try {
            Validations.validate(order);
            fail("Expected " + order + " to fail validation");
        } catch (OrderValidationException expected) {}
        assertNotNull(order.getOrderID());
        assertFalse(strategy.send(order));
        verifyLoggedEvents(new String[] { ORDER_VALIDATION_FAILED.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] {} );
        reset();
        // send valid order
        order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        Validations.validate(order);
        assertTrue(strategy.send(order));
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        verifyLoggedEvents(new String[] {});
        verifyOpenOrders(new OrderSingle[] { order } );
        verifySentObjects(new Object[] { order } );
    }
    /**
     * Tests {@link AbstractRunningStrategy#cancelOrder(OrderID, boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test@Ignore
    public void testCancelOrder()
            throws Exception
    {
        assertNull(strategy.cancelOrder(null,
                                        false));
        verifyLoggedEvents(new String[] { INVALID_CANCEL.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] {} );
        reset();
        OrderSingle order = factory.createOrderSingle();
        order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.Filled));
        // order should now be closed (not cancellable)
        assertNull(strategy.cancelOrder(order.getOrderID(),
                                        false));
        verifyLoggedEvents(new String[] { INVALID_ORDERID.getText(String.valueOf(strategy),
                                                                  order.getOrderID().getValue()) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] {} );
        reset();
        // create a new order
        order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        strategy.send(order);
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        OrderCancel cancel = strategy.cancelOrder(order.getOrderID(),
                                                  false);
        assertNotNull(cancel);
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order } );
        verifySentObjects(new Object[] { order } );
        assertEquals(order.getOrderID(),
                     cancel.getOriginalOrderID());
        assertFalse(order.getOrderID().equals(cancel.getOrderID()));
        reset();
        // one more time
        cancel = strategy.cancelOrder(order.getOrderID(),
                                      true);
        assertNotNull(cancel);
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order } );
        verifySentObjects(new Object[] { cancel } );
        assertEquals(order.getOrderID(),
                     cancel.getOriginalOrderID());
        assertFalse(order.getOrderID().equals(cancel.getOrderID()));
        // replace the order and cancel
        reset();
        order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        strategy.send(order);
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        reset();
        OrderSingle replaceOrder = factory.createOrderSingle();
        replaceOrder = factory.createOrderSingle();
        replaceOrder.setOrderType(OrderType.Market);
        replaceOrder.setQuantity(EventTestBase.generateDecimalValue());
        replaceOrder.setSide(Side.Buy);
        replaceOrder.setInstrument(new Equity("METC"));
        OrderReplace replace = strategy.cancelReplace(order.getOrderID(),
                                                      replaceOrder,
                                                      true);
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(replace.getOrderID().getValue(),
                                                                                             order.getOrderID().getValue(),
                                                                                             OrderStatus.Replaced));
        verifyLoggedEvents(new String[0]);
        verifySentObjects(new Object[] { replace } );
        assertTrue(strategy.getOpenOrderIDs().contains(replace.getOrderID()));
        assertFalse(strategy.getOpenOrderIDs().contains(order.getOrderID()));
        // able to cancel by the replace order ID
        reset();
        cancel = strategy.cancelOrder(replace.getOrderID(),
                                      true);
        assertNotNull(cancel);
        verifyLoggedEvents(new String[0]);
        verifySentObjects(new Object[] { cancel } );
    }
    /**
     * Tests {@link AbstractRunningStrategy#cancelAllOrders()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test@Ignore
    public void testCancelAllOrders()
            throws Exception
    {
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[0]);
        assertEquals(0,
                     strategy.cancelAllOrders());
        verifySentObjects(new Object[0]);
        // create an order and close it
        OrderSingle order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        assertTrue(strategy.send(order));
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order } );
        verifySentObjects(new Object[] { order } );
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.Filled));
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[] { order } );
        reset();
        assertEquals(0,
                     strategy.cancelAllOrders());
        verifySentObjects(new Object[0]);
        // create two orders and leave them open
        order = factory.createOrderSingle();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        assertTrue(strategy.send(order));
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order } );
        verifySentObjects(new Object[] { order } );
        OrderSingle order2 = factory.createOrderSingle();
        order2.setOrderType(OrderType.Market);
        order2.setQuantity(EventTestBase.generateDecimalValue());
        order2.setSide(Side.Buy);
        order2.setInstrument(new Equity("METC"));
        assertTrue(strategy.send(order2));
        strategy.onExecutionReportRedirected(OrderHistoryManagerTest.generateExecutionReport(order2.getOrderID().getValue(),
                                                                                             null,
                                                                                             OrderStatus.PartiallyFilled));
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order, order2 } );
        verifySentObjects(new Object[] { order, order2 } );
        reset();
        assertEquals(2,
                     strategy.cancelAllOrders());
        boolean order1Canceled = false;
        boolean order2Canceled = false;
        for(Object object : servicesProvider.sentObjects) {
            assertTrue(object instanceof OrderCancel);
            OrderCancel cancel = (OrderCancel)object;
            if(cancel.getOriginalOrderID().equals(order.getOrderID())) {
                order1Canceled = true;
            }
            if(cancel.getOriginalOrderID().equals(order2.getOrderID())) {
                order2Canceled = true;
            }
        }
        assertTrue(order1Canceled);
        assertTrue(order2Canceled);
        reset();
    }
    /**
     * Tests {@link AbstractRunningStrategy#cancelReplace(OrderID, OrderSingle, boolean)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test@Ignore
    public void testCancelReplace()
            throws Exception
    {
        OrderSingle order = factory.createOrderSingle();
        assertNull(strategy.cancelReplace(null,
                                          order,
                                          false));
        verifyLoggedEvents(new String[] { INVALID_REPLACEMENT_ORDER.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[0]);
        reset();
        assertNull(strategy.cancelReplace(order.getOrderID(),
                                          null,
                                          false));
        verifyLoggedEvents(new String[] { INVALID_REPLACEMENT_ORDER.getText(String.valueOf(strategy)) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[0]);
        reset();
        order.setOrderType(OrderType.Market);
        order.setQuantity(EventTestBase.generateDecimalValue());
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        OrderSingle replaceModel = factory.createOrderSingle();
        replaceModel.setQuantity(order.getQuantity().add(BigDecimal.ONE));
        assertFalse(replaceModel.getQuantity().equals(order.getQuantity()));
        // order is not open
        assertFalse(strategy.getOpenOrderIDs().contains(order.getOrderID()));
        assertNull(strategy.cancelReplace(order.getOrderID(),
                                          replaceModel,
                                          false));
        verifyLoggedEvents(new String[] { INVALID_ORDERID.getText(String.valueOf(strategy),
                                                                  order.getOrderID().getValue()) });
        verifyOpenOrders(new OrderSingle[0]);
        verifySentObjects(new Object[0]);
        reset();
        // open the order
        ExecutionReport report = OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                                 null,
                                                                                 OrderStatus.PartiallyFilled);
        strategy.onExecutionReportRedirected(report);
        assertTrue(strategy.getOpenOrderIDs().contains(order.getOrderID()));
        replaceModel.setPrice(EventTestBase.generateDecimalValue());
        assertNotNull(replaceModel.getPrice());
        OrderReplace replace = strategy.cancelReplace(order.getOrderID(),
                                                      replaceModel,
                                                      false);
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order });
        verifySentObjects(new Object[0]);
        assertNotNull(replace);
        assertNull(replace.getBrokerOrderID());
        assertEquals(replaceModel.getQuantity(),
                     replace.getQuantity());
        assertEquals(OrderType.Market,
                     report.getOrderType());
        assertNull(replace.getPrice());
        OrderHistoryManagerTest.orderType = OrderType.Limit;
        report = OrderHistoryManagerTest.generateExecutionReport(order.getOrderID().getValue(),
                                                                 null,
                                                                 OrderStatus.PartiallyFilled);
        strategy.onExecutionReportRedirected(report);
        assertEquals(OrderType.Limit,
                     report.getOrderType());
        assertNotNull(report.getPrice());
        reset();
        replace = strategy.cancelReplace(order.getOrderID(),
                                         replaceModel,
                                         true);
        verifyLoggedEvents(new String[0]);
        verifyOpenOrders(new OrderSingle[] { order });
        verifySentObjects(new Object[] { replace });
        assertNotNull(replace);
        assertNull(replace.getBrokerOrderID());
        assertEquals(replaceModel.getQuantity(),
                     replace.getQuantity());
        assertEquals(replaceModel.getPrice(),
                     replace.getPrice());
        // replace a replaced order
        OrderHistoryManagerTest.orderType = OrderType.Market;
        reset();
        report = OrderHistoryManagerTest.generateExecutionReport("order-" + System.nanoTime(),
                                                                 order.getOrderID().getValue(),
                                                                 OrderStatus.Replaced);
        strategy.onExecutionReportRedirected(report);
        verifyLoggedEvents(new String[0]);
        verifySentObjects(new Object[0]);
        assertTrue(strategy.getOpenOrderIDs().contains(report.getOrderID()));
        assertFalse(strategy.getOpenOrderIDs().contains(order.getOrderID()));
        replaceModel.setPrice(EventTestBase.generateDecimalValue());
        // can't replace the original
        replace = strategy.cancelReplace(order.getOrderID(),
                                         replaceModel,
                                         false);
        assertNull(replace);
        verifyLoggedEvents(new String[] { INVALID_ORDERID.getText(String.valueOf(strategy),
                                                                  order.getOrderID().getValue()) });
        verifySentObjects(new Object[0]);
        // can replace the replaced
        reset();
        replace = strategy.cancelReplace(report.getOrderID(),
                                         replaceModel,
                                         true);
        verifyLoggedEvents(new String[0]);
        verifySentObjects(new Object[] { replace });
        assertNotNull(replace);
        assertNull(replace.getBrokerOrderID());
        assertEquals(replaceModel.getQuantity(),
                     replace.getQuantity());
    }
    /**
     * Tests {@link AbstractRunningStrategy#getOrderStatus(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetOrderStatus()
            throws Exception
    {
        // null order
        assertNull(strategy.getOrderStatus(null));
        // non-existent order
        assertNull(strategy.getOrderStatus(new OrderID("this-id-doesn't-exist-" + System.nanoTime())));
        // open order
        OrderID orderID = new OrderID("order-" + System.nanoTime());
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport(orderID.getValue(),
                                                                                  null,
                                                                                  OrderStatus.PartiallyFilled);
        strategy.onExecutionReportRedirected(report1);
        assertEquals(OrderStatus.PartiallyFilled,
                     strategy.getOrderStatus(orderID));
        // replaced order
        ExecutionReport report2 = OrderHistoryManagerTest.generateExecutionReport("order-" + System.nanoTime(),
                                                                                  orderID.getValue(),
                                                                                  OrderStatus.Replaced);
        strategy.onExecutionReportRedirected(report2);
        // original order shows as replaced
        assertEquals(OrderStatus.Replaced,
                     strategy.getOrderStatus(orderID));
        // as does the current order
        assertEquals(OrderStatus.Replaced,
                     strategy.getOrderStatus(report2.getOrderID()));
        // filled order
        ExecutionReport report3 = OrderHistoryManagerTest.generateExecutionReport(report2.getOrderID().getValue(),
                                                                                  null,
                                                                                  OrderStatus.Filled);
        strategy.onExecutionReportRedirected(report3);
        assertEquals(OrderStatus.Filled,
                     strategy.getOrderStatus(orderID));
        assertEquals(OrderStatus.Filled,
                     strategy.getOrderStatus(report2.getOrderID()));
        assertEquals(OrderStatus.Filled,
                     strategy.getOrderStatus(report3.getOrderID()));
        // canceled order
        ExecutionReport report4 = OrderHistoryManagerTest.generateExecutionReport("order-" + System.nanoTime(),
                                                                                  report3.getOrderID().getValue(),
                                                                                  OrderStatus.Canceled);
        strategy.onExecutionReportRedirected(report4);
        assertEquals(OrderStatus.Canceled,
                     strategy.getOrderStatus(orderID));
        assertEquals(OrderStatus.Canceled,
                     strategy.getOrderStatus(report2.getOrderID()));
        assertEquals(OrderStatus.Canceled,
                     strategy.getOrderStatus(report3.getOrderID()));
        assertEquals(OrderStatus.Canceled,
                     strategy.getOrderStatus(report4.getOrderID()));
        // cancel-rejected order
        OrderCancelReject report5 = OrderHistoryManagerTest.generateOrderCancelReject("order-" + System.nanoTime(),
                                                                                      report4.getOrderID().getValue());
        strategy.onCancelRejectRedirected(report5);
        assertEquals(OrderStatus.Rejected,
                     strategy.getOrderStatus(orderID));
        assertEquals(OrderStatus.Rejected,
                     strategy.getOrderStatus(report2.getOrderID()));
        assertEquals(OrderStatus.Rejected,
                     strategy.getOrderStatus(report3.getOrderID()));
        assertEquals(OrderStatus.Rejected,
                     strategy.getOrderStatus(report4.getOrderID()));
        assertEquals(OrderStatus.Rejected,
                     strategy.getOrderStatus(report5.getOrderID()));
    }
    /**
     * Resets the test objects as necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void reset()
            throws Exception
    {
        servicesProvider.reset();
        strategy.initializeReportHistoryManager();
    }
    /**
     * Verifies that the actual objects sent match the given expected objects.
     *
     * @param inExpectedObjects an <code>Object[]</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySentObjects(Object[] inExpectedObjects)
            throws Exception
    {
        assertTrue(Arrays.equals(inExpectedObjects,
                                 servicesProvider.sentObjects.toArray()));
    }
    /**
     * Verifies that the actual submitted orders match the expected submitted orders. 
     *
     * @param inExpectedOrders an <code>OrderSingle[]</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyOpenOrders(OrderSingle[] inExpectedOrders)
            throws Exception
    {
        OrderID[] expectedOrderIDs = new OrderID[inExpectedOrders.length];
        int counter = 0;
        for(OrderSingle order : inExpectedOrders) {
            expectedOrderIDs[counter++] = order.getOrderID();
        }
        CollectionAssert.assertArrayPermutation(expectedOrderIDs,
                                                strategy.getOpenOrderIDs().toArray(new OrderID[0]));
    }
    /**
     * Verifies that the actual logged events match the expected logged events. 
     *
     * @param inExpectedLogEvents a <code>String[]</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyLoggedEvents(String[] inExpectedMessageContents)
            throws Exception
    {
        assertEquals(inExpectedMessageContents.length,
                     servicesProvider.loggedEvents.size());
        int counter = 0;
        for(LogEvent event : servicesProvider.loggedEvents) {
            assertEquals(inExpectedMessageContents[counter++],
                         event.getMessage());
        }
    }
    /**
     * Test <code>Strategy</code> implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    private class MockStrategy
            implements Strategy
    {
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#dataReceived(java.lang.Object)
         */
        @Override
        public void dataReceived(Object inData)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getScript()
         */
        @Override
        public String getScript()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getLanguage()
         */
        @Override
        public Language getLanguage()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getName()
         */
        @Override
        public String getName()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getParameters()
         */
        @Override
        public Properties getParameters()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#start()
         */
        @Override
        public void start()
                throws StrategyException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#stop()
         */
        @Override
        public void stop()
                throws Exception
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getStatus()
         */
        @Override
        public Status getStatus()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getServicesProvider()
         */
        @Override
        public ServicesProvider getServicesProvider()
        {
            return servicesProvider;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getDefaultNamespace()
         */
        @Override
        public String getDefaultNamespace()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.Strategy#getExecutor()
         */
        @Override
        public Executor getExecutor()
        {
            throw new UnsupportedOperationException(); // TODO
        }
    }
    /**
     * Test <code>ServicesProvider</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    private class MockServicesProvider
            implements ServicesProvider
    {
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#send(java.lang.Object)
         */
        @Override
        public void send(Object inData)
        {
            sentObjects.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#cancelOrder(org.marketcetera.trade.OrderCancel)
         */
        @Override
        public void cancelOrder(OrderCancel inCancel)
        {
            sentObjects.add(inCancel);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#cancelReplace(org.marketcetera.trade.OrderReplace)
         */
        @Override
        public void cancelReplace(OrderReplace inReplace)
        {
            sentObjects.add(inReplace);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#sendSuggestion(org.marketcetera.trade.Suggestion)
         */
        @Override
        public void sendSuggestion(Suggestion inSuggestion)
        {
            sentObjects.add(inSuggestion);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#sendEvent(org.marketcetera.event.Event, java.lang.String, java.lang.String)
         */
        @Override
        public void sendEvent(Event inEvent,
                              String inProvider,
                              String inNamespace)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#sendNotification(org.marketcetera.core.notifications.Notification)
         */
        @Override
        public void sendNotification(Notification inNotification)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#log(org.marketcetera.event.LogEvent)
         */
        @Override
        public void log(LogEvent inMessage)
        {
            loggedEvents.add(inMessage);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#requestMarketData(org.marketcetera.marketdata.MarketDataRequest)
         */
        @Override
        public int requestMarketData(MarketDataRequest inRequest)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#requestProcessedMarketData(org.marketcetera.marketdata.MarketDataRequest, java.lang.String[], java.lang.String, java.lang.String)
         */
        @Override
        public int requestProcessedMarketData(MarketDataRequest inRequest,
                                              String[] inStatements,
                                              String inCEPSource,
                                              String inNamespace)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#cancelDataRequest(int)
         */
        @Override
        public void cancelDataRequest(int inDataRequestID)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#cancelAllDataRequests()
         */
        @Override
        public void cancelAllDataRequests()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#requestCEPData(java.lang.String[], java.lang.String, java.lang.String)
         */
        @Override
        public int requestCEPData(String[] inStatements,
                                  String inSource,
                                  String inNamespace)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#sendMessage(quickfix.Message, org.marketcetera.trade.BrokerID)
         */
        @Override
        public void sendMessage(Message inMessage,
                                BrokerID inBroker)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#statusChanged(org.marketcetera.strategy.Status, org.marketcetera.strategy.Status)
         */
        @Override
        public void statusChanged(Status inOldStatus,
                                  Status inNewStatus)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#createDataFlow(org.marketcetera.module.DataRequest[], boolean)
         */
        @Override
        public DataFlowID createDataFlow(DataRequest[] inRequests,
                                         boolean inAppendDataSink)
                throws ModuleException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#cancelDataFlow(org.marketcetera.module.DataFlowID)
         */
        @Override
        public void cancelDataFlow(DataFlowID inDataFlowID)
                throws ModuleException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getBrokers()
         */
        @Override
        public List<BrokerStatus> getBrokers()
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
         */
        @Override
        public BigDecimal getPositionAsOf(Date inDate,
                                          Equity inEquity)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getAllPositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Equity>, BigDecimal> getAllPositionsAsOf(Date inDate)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
         */
        @Override
        public BigDecimal getFuturePositionAsOf(Date inDate,
                                                Future inFuture)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getAllFuturePositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
         */
        @Override
        public BigDecimal getOptionPositionAsOf(Date inDate,
                                                Option inOption)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getAllOptionPositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
         */
        @Override
        public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                           String... inOptionRoots)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getUnderlying(java.lang.String)
         */
        @Override
        public String getUnderlying(String inOptionRoot)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getOptionRoots(java.lang.String)
         */
        @Override
        public Collection<String> getOptionRoots(String inUnderlying)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getURN()
         */
        @Override
        public ModuleURN getURN()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#getUserData()
         */
        @Override
        public Properties getUserData()
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.ServicesProvider#setUserData(java.util.Properties)
         */
        @Override
        public void setUserData(Properties inData)
                throws ConnectionException, ClientInitException
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /**
         * Resets the <code>MockServicesProvide</code> object.
         */
        private void reset()
        {
            loggedEvents.clear();
            sentObjects.clear();
        }
        /**
         * events logged by the underlying strategy 
         */
        private final List<LogEvent> loggedEvents = new ArrayList<LogEvent>();
        /**
         * objects sent by the underlying strategy
         */
        private final List<Object> sentObjects = new ArrayList<Object>();
    }
    /**
     * Test <code>AbstractRunningStrategy</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    private class MockRunningStrategy
            extends AbstractRunningStrategy
    {
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onTrade(org.marketcetera.event.TradeEvent)
         */
        @Override
        public void onTrade(TradeEvent inTrade)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onBid(org.marketcetera.event.BidEvent)
         */
        @Override
        public void onBid(BidEvent inBid)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onAsk(org.marketcetera.event.AskEvent)
         */
        @Override
        public void onAsk(AskEvent inAsk)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onMarketstat(org.marketcetera.event.MarketstatEvent)
         */
        @Override
        public void onMarketstat(MarketstatEvent inStatistics)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onDividend(org.marketcetera.event.DividendEvent)
         */
        @Override
        public void onDividend(DividendEvent inDividend)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onExecutionReport(org.marketcetera.trade.ExecutionReport)
         */
        @Override
        public void onExecutionReport(ExecutionReport inExecutionReport)
        {
            reports.add(inExecutionReport);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onCancelReject(org.marketcetera.trade.OrderCancelReject)
         */
        @Override
        public void onCancelReject(OrderCancelReject inCancelReject)
        {
            rejects.add(inCancelReject);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onOther(java.lang.Object)
         */
        @Override
        public void onOther(Object inEvent)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onCallback(java.lang.Object)
         */
        @Override
        public void onCallback(Object inData)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onStart()
         */
        @Override
        public void onStart()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.RunningStrategy#onStop()
         */
        @Override
        public void onStop()
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /**
         * stores the reports received
         */
        private final List<ExecutionReport> reports = new ArrayList<ExecutionReport>();
        /**
         * stores the rejects received
         */
        private final List<OrderCancelReject> rejects = new ArrayList<OrderCancelReject>();
    }
    /**
     * test services provider
     */
    private MockServicesProvider servicesProvider;
    /**
     * test strategy
     */
    private MockRunningStrategy strategy;
    /**
     * test trade object factory
     */
    private Factory factory;
}

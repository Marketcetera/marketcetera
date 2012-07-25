package org.marketcetera.client.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.MockClient;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;

/* $License$ */

/**
 * Tests {@link LiveOrderHistoryManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class LiveOrderHistoryManagerTest
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
        ClientManager.setClientFactory(new MockClient.MockClientFactory());
        ClientManager.init(null);
        client = (MockClient)ClientManager.getInstance();
        OrderHistoryManagerTest.once();
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        client.reset();
    }
    /**
     * Tests {@link LiveOrderHistoryManager#LiveOrderHistoryManager(java.util.Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testConstructor()
            throws Exception
    {
        LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        assertEquals(new Date(0),
                     manager.getReportHistoryOrigin());
        manager = new LiveOrderHistoryManager(new Date(Long.MIN_VALUE));
        assertEquals(new Date(Long.MIN_VALUE),
                     manager.getReportHistoryOrigin());
        manager = new LiveOrderHistoryManager(new Date(Long.MAX_VALUE));
        assertEquals(new Date(Long.MAX_VALUE),
                     manager.getReportHistoryOrigin());
        manager = new LiveOrderHistoryManager(new Date(0));
        assertEquals(new Date(0),
                     manager.getReportHistoryOrigin());
    }
    /**
     * Tests {@link LiveOrderHistoryManager#getOpenOrders()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetOpenOrders()
            throws Exception
    {
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        assertFalse(manager.isRunning());
        new ExpectedFailure<IllegalStateException>(org.marketcetera.client.Messages.OPEN_ORDER_LIST_NOT_READY.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                manager.getOpenOrders();
            }
        };
        manager.start();
        final Map<OrderID,ExecutionReport> openOrders = manager.getOpenOrders();
        final Set<OrderID> orderIds = manager.getOrderIds();
        assertTrue(openOrders.isEmpty());
        assertTrue(openOrders.isEmpty());
        // add a non-open report
        ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.Filled);
        assertFalse(report1.getOrderStatus().isCancellable());
        manager.add(report1);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !orderIds.isEmpty();
            }
        });
        // there's a non-zero chance of a race-condition here because the orderIds collection is populated
        //  before the openOrder collection is updated. we're in trouble because we're trying to show
        //  a negative - there's no deterministic way to wait for a lack of update. instead, add an open order, which
        //  is deterministically detectable and make sure it is the only one received.
        ReportBase report2 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.PartiallyFilled);
        assertTrue(report2.getOrderStatus().isCancellable());
        manager.add(report2);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !openOrders.isEmpty();
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report2,
                     openOrders.get(report2.getOrderID()));
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                openOrders.clear();
            }
        };
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                openOrders.put(new OrderID("orderID"),
                               OrderHistoryManagerTest.generateExecutionReport("orderID",
                                                                               null,
                                                                               OrderStatus.PartiallyFilled));
            }
        };
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                openOrders.keySet().iterator().remove();
            }
        };
    }
    /**
     * Tests that the open orders collection picks up existing open orders.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOpenOrdersPopulatedFromSnapshot()
            throws Exception
    {
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        Set<ReportBase> historicalReports = client.getReports();
        assertTrue(historicalReports.isEmpty());
        // generate a few open orders and a few non-open orders
        ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.New);
        Thread.sleep(250);
        ReportBase report2 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.Canceled);
        Thread.sleep(250);
        ReportBase report3 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.Replaced);
        Thread.sleep(250);
        ReportBase report4 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.Filled);
        historicalReports.add(report1);
        historicalReports.add(report2);
        historicalReports.add(report3);
        historicalReports.add(report4);
        manager.start();
        Map<OrderID,ExecutionReport> openOrders = manager.getOpenOrders();
        assertEquals(2,
                     openOrders.size());
        assertEquals(4,
                     manager.getOrderIds().size());
        assertEquals(report1,
                     openOrders.get(report1.getOrderID()));
        assertEquals(report3,
                     openOrders.get(report3.getOrderID()));
    }
    /**
     * Tests that the open order collection is updated when an order closes.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOpenOrderCloses()
            throws Exception
    {
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        manager.start();
        final Map<OrderID,ExecutionReport> openOrders = manager.getOpenOrders();
        assertTrue(openOrders.isEmpty());
        ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                             null,
                                                                             OrderStatus.New);
        manager.add(report1);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !openOrders.isEmpty();
            }
        });
        ReportBase report2 = OrderHistoryManagerTest.generateExecutionReport(report1.getOrderID().getValue(),
                                                                             null,
                                                                             OrderStatus.Filled);
        manager.add(report2);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return openOrders.isEmpty();
            }
        });
    }
    /**
     * Tests the order lifecycle.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOrderLifecycle()
            throws Exception
    {
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        manager.start();
        final Map<OrderID,ExecutionReport> openOrders = manager.getOpenOrders();
        assertTrue(openOrders.isEmpty());
        final ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                   null,
                                                                                   OrderStatus.New);
        manager.add(report1);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !openOrders.isEmpty();
            }
        });
        ReportBase report2 = OrderHistoryManagerTest.generateExecutionReport(report1.getOrderID().getValue(),
                                                                             null,
                                                                             OrderStatus.PendingReplace);
        manager.add(report2);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report1.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.PendingReplace);
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report2,
                     openOrders.get(report1.getOrderID()));
        final ReportBase report3 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                   report1.getOrderID().getValue(),
                                                                                   OrderStatus.Replaced);
        manager.add(report3);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report3.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.Replaced) &&
                       openOrders.size() == 1;
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report3,
                     openOrders.get(report3.getOrderID()));
        final ReportBase report4 = OrderHistoryManagerTest.generateExecutionReport(report3.getOrderID().getValue(),
                                                                                   null,
                                                                                   OrderStatus.PartiallyFilled);
        manager.add(report4);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report4.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.PartiallyFilled);
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report4,
                     openOrders.get(report4.getOrderID()));
        final ReportBase report5 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                   report4.getOrderID().getValue(),
                                                                                   OrderStatus.PendingReplace);
        manager.add(report5);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report5.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.PendingReplace) &&
                       openOrders.size() == 1;
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report5,
                     openOrders.get(report5.getOrderID()));
        final ReportBase report6 = OrderHistoryManagerTest.generateExecutionReport(report5.getOrderID().getValue(),
                                                                                   report4.getOrderID().getValue(),
                                                                                   OrderStatus.Replaced);
        manager.add(report6);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report6.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.Replaced) &&
                       openOrders.size() == 1;
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report6,
                     openOrders.get(report6.getOrderID()));
        final ReportBase report7 = OrderHistoryManagerTest.generateExecutionReport(report6.getOrderID().getValue(),
                                                                                   null,
                                                                                   OrderStatus.PendingCancel);
        manager.add(report7);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                ReportBase report = openOrders.get(report7.getOrderID());
                if(report == null) {
                    return false;
                }
                return report.getOrderStatus().equals(OrderStatus.PendingCancel);
            }
        });
        assertEquals(1,
                     openOrders.size());
        assertEquals(report7,
                     openOrders.get(report7.getOrderID()));
        final ReportBase report8 = OrderHistoryManagerTest.generateOrderCancelReject("order-" + counter.incrementAndGet(),
                                                                                     report7.getOrderID().getValue());
        manager.add(report8);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return openOrders.isEmpty();
            }
        });
    }
    /**
     * Tests what happens if an error occurs connecting to the client.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void clientExceptionTest()
            throws Exception
    {
        ConnectionException exception = new ConnectionException(org.marketcetera.client.Messages.DONT_ADD_REPORTS);
        client.setGetReportsSinceException(exception);
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        new ExpectedFailure<RuntimeException>(ConnectionException.class.getCanonicalName() + ": " + org.marketcetera.client.Messages.DONT_ADD_REPORTS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                manager.start();
            }
        };
    }
    /**
     * Tests start and stop cycles. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void startAndStopTest()
            throws Exception
    {
        final LiveOrderHistoryManager manager = new LiveOrderHistoryManager(null);
        assertFalse(manager.isRunning());
        assertNotNull(manager.toString());
        manager.stop();
        assertFalse(manager.isRunning());
        assertNotNull(manager.toString());
        manager.start();
        assertTrue(manager.isRunning());
        assertNotNull(manager.toString());
        manager.start();
        assertTrue(manager.isRunning());
        assertNotNull(manager.toString());
    }
    /**
     * test client used to simulate connections to the server
     */
    private static MockClient client;
    /**
     * counter used for unique identifiers
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
}

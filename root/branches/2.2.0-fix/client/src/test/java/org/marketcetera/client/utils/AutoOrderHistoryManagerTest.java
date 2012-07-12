package org.marketcetera.client.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.MockClient;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.*;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;

/* $License$ */

/**
 * Tests {@link AutoOrderHistoryManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class AutoOrderHistoryManagerTest
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
     * Verifies that the constructor works.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testConstructor()
            throws Exception
    {
        AutoOrderHistoryManager manager = new AutoOrderHistoryManager(null);
        assertEquals(new Date(0),
                     manager.getReportHistoryOrigin());
        manager = new AutoOrderHistoryManager(new Date(Long.MIN_VALUE));
        assertEquals(new Date(Long.MIN_VALUE),
                     manager.getReportHistoryOrigin());
        manager = new AutoOrderHistoryManager(new Date(Long.MAX_VALUE));
        assertEquals(new Date(Long.MAX_VALUE),
                     manager.getReportHistoryOrigin());
        manager = new AutoOrderHistoryManager(new Date(0));
        assertEquals(new Date(0),
                     manager.getReportHistoryOrigin());
    }
    /**
     * Verifies that {@link LiveOrderHistoryManager#add(org.marketcetera.trade.ReportBase)} is not allowed.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddNotAllowed()
            throws Exception
    {
        final AutoOrderHistoryManager manager = new AutoOrderHistoryManager(null);
        new ExpectedFailure<UnsupportedOperationException>(org.marketcetera.client.Messages.DONT_ADD_REPORTS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                manager.add(null);
            }
        };
        new ExpectedFailure<UnsupportedOperationException>(org.marketcetera.client.Messages.DONT_ADD_REPORTS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                manager.add(OrderHistoryManagerTest.generateExecutionReport("order",
                                                                            null,
                                                                            OrderStatus.New));
            }
        };
    }
    /**
     * Tests {@link AutoOrderHistoryManager#receiveExecutionReport(org.marketcetera.trade.ExecutionReport)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReceiveExecutionReport()
            throws Exception
    {
        final AutoOrderHistoryManager orderManager = new AutoOrderHistoryManager(null);
        orderManager.start();
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                  null,
                                                                                  OrderStatus.New);
        final Map<OrderID,ExecutionReport> actualOpenOrders = orderManager.getOpenOrders();
        assertTrue(actualOpenOrders.isEmpty());
        orderManager.receiveExecutionReport(null);
        assertTrue(actualOpenOrders.isEmpty());
        orderManager.receiveExecutionReport(report1);
        assertEquals(1,
                     actualOpenOrders.size());
        assertEquals(report1,
                     actualOpenOrders.values().iterator().next());
    }
    /**
     * Tests {@link AutoOrderHistoryManager#receiveCancelReject(org.marketcetera.trade.OrderCancelReject)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReceiveOrderCancelReject()
            throws Exception
    {
        final AutoOrderHistoryManager orderManager = new AutoOrderHistoryManager(null);
        orderManager.start();
        final Map<OrderID,ExecutionReport> actualOpenOrders = orderManager.getOpenOrders();
        assertTrue(actualOpenOrders.isEmpty());
        orderManager.receiveCancelReject(null);
        assertTrue(actualOpenOrders.isEmpty());
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                  null,
                                                                                  OrderStatus.PendingNew);
        ExecutionReport report2 = OrderHistoryManagerTest.generateExecutionReport(report1.getOrderID().getValue(),
                                                                                  null,
                                                                                  OrderStatus.New);
        ExecutionReport report3 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                  report2.getOrderID().getValue(),
                                                                                  OrderStatus.PendingCancel);
        OrderCancelReject report4 = OrderHistoryManagerTest.generateOrderCancelReject(report2.getOrderID().getValue(),
                                                                                      report3.getOrderID().getValue());
        orderManager.receiveExecutionReport(report1);
        assertEquals(1,
                     actualOpenOrders.size());
        assertEquals(report1,
                     actualOpenOrders.values().iterator().next());
        orderManager.receiveExecutionReport(report2);
        assertEquals(1,
                     actualOpenOrders.size());
        assertEquals(report2,
                     actualOpenOrders.values().iterator().next());
        orderManager.receiveExecutionReport(report3);
        assertEquals(1,
                     actualOpenOrders.size());
        assertEquals(report3,
                     actualOpenOrders.values().iterator().next());
        orderManager.receiveCancelReject(report4);
        assertTrue(actualOpenOrders.isEmpty());
    }
    /**
     * Tests {@link AutoOrderHistoryManager#start()} and {@link AutoOrderHistoryManager#stop()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartAndStop()
            throws Exception
    {
        final AutoOrderHistoryManager orderManager = new AutoOrderHistoryManager(null);
        Set<OrderID> orderIDs = orderManager.getOrderIds();
        assertTrue(orderIDs.isEmpty());
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-" + counter.incrementAndGet(),
                                                                                  null,
                                                                                  OrderStatus.PendingNew);
        client.sendToListeners(report1);
        assertTrue(orderIDs.isEmpty());
        orderManager.start();
        client.sendToListeners(report1);
        assertEquals(1,
                     orderIDs.size());
        assertEquals(report1.getOrderID(),
                     orderIDs.iterator().next());
        orderManager.stop();
        assertTrue(orderIDs.isEmpty());
        ExecutionReport report2 = OrderHistoryManagerTest.generateExecutionReport(report1.getOrderID().getValue(),
                                                                                  null,
                                                                                  OrderStatus.New);
        client.sendToListeners(report2);
        assertTrue(orderIDs.isEmpty());
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

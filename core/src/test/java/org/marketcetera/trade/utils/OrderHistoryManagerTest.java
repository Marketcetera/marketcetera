package org.marketcetera.trade.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.CollectionAssert;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TransactTime;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Tests {@link OrderHistoryManager} class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class OrderHistoryManagerTest
{
    /**
     * Run once before all unit tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        factory = Factory.getInstance();
        FIXDataDictionary dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(dataDictionary == null) {
            FIXDataDictionaryManager.initialize(fixVersion, 
                                                fixVersion.getDataDictionaryName());
            dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        }
    }
    /**
     * Tests {@link OrderHistoryManager#getLatestReportFor(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetLatestReportFor()
            throws Exception
    {
        OrderHistoryManager manager = new OrderHistoryManager();
        assertNull(manager.getLatestReportFor(null));
        assertNull(manager.getLatestReportFor(new OrderID("some-orderid-that-doesn't-exist")));
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        manager.add(report1);
        assertEquals(report1,
                     manager.getLatestReportFor(report1.getOrderID()));
        assertNull(manager.getLatestReportFor(new OrderID("some-orderid-that-doesn't-exist")));
    }
    /**
     * Tests {@link OrderHistoryManager#add(ReportBase)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAdd()
            throws Exception
    {
        final OrderHistoryManager orderManager = new OrderHistoryManager();
        Multimap<OrderID,ReportBase> expectedReports = LinkedHashMultimap.create();
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                orderManager.add(null);
            }
        };
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        assertEquals(report1.getOrderID(),
                     report2.getOrderID());
        orderManager.add(report2);
        expectedReports.clear();
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report2,report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          report1.getOrderID().toString(),
                                                          OrderStatus.Replaced);
        assertEquals(report1.getOrderID(),
                     report3.getOriginalOrderID());
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        expectedReports.clear();
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report4 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report4.getOrderID()));
        assertFalse(report3.getOrderID().equals(report4.getOrderID()));
        orderManager.add(report4);
        expectedReports.putAll(report4.getOrderID(),
                               Arrays.asList(report4));
        verifyOrderHistory(orderManager,
                           expectedReports);
    }
    /**
     * Tests {@link OrderHistoryManager#getReportHistoryFor(OrderID)} and {@link OrderHistoryManager#getRootOrderIdFor(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetReportHistoryFor()
            throws Exception
    {
        final OrderHistoryManager orderManager = new OrderHistoryManager();
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                orderManager.getReportHistoryFor(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                orderManager.getRootOrderIdFor(null);
            }
        };
        assertTrue(orderManager.getReportHistoryFor(new OrderID("this-order-doesn't-exist")).isEmpty());
        assertNull(orderManager.getRootOrderIdFor(new OrderID("this-order-doesn't-exist")));
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        assertEquals(report1.getOrderID(),
                     orderManager.getRootOrderIdFor(report1.getOrderID()));
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report2, report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        assertEquals(report1.getOrderID(),
                     orderManager.getRootOrderIdFor(report1.getOrderID()));
        assertEquals(report1.getOrderID(),
                     orderManager.getRootOrderIdFor(report2.getOrderID()));
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report2, report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report3 },
                                                orderManager.getReportHistoryFor(report3.getOrderID()).toArray(new ExecutionReport[0]));
        assertEquals(report1.getOrderID(),
                     orderManager.getRootOrderIdFor(report1.getOrderID()));
        assertEquals(report1.getOrderID(),
                     orderManager.getRootOrderIdFor(report2.getOrderID()));
        assertEquals(report3.getOrderID(),
                     orderManager.getRootOrderIdFor(report3.getOrderID()));
    }
    /**
     * Tests {@link OrderHistoryManager#getOrderIds()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetOrderIDs()
            throws Exception
    {
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderIds().isEmpty());
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        CollectionAssert.assertArrayPermutation(new OrderID[] { report1.getOrderID() },
                                                orderManager.getOrderIds().toArray(new OrderID[0]));
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        CollectionAssert.assertArrayPermutation(new OrderID[] { report1.getOrderID() },
                                                orderManager.getOrderIds().toArray(new OrderID[0]));
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        CollectionAssert.assertArrayPermutation(new OrderID[] { report1.getOrderID(), report3.getOrderID() },
                                                orderManager.getOrderIds().toArray(new OrderID[0]));
    }
    /**
     * Tests {@link OrderHistoryManager#clear()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testClear()
            throws Exception
    {
        Multimap<OrderID,ReportBase> expectedReports = LinkedHashMultimap.create();
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderIds().isEmpty());
        orderManager.clear();
        assertTrue(orderManager.getOrderIds().isEmpty());
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ExecutionReport[] { report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear();
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.add(report1);
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ExecutionReport[] { report2, report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear();
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.add(report1);
        orderManager.add(report2);
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          report1.getOrderID().toString(),
                                                          OrderStatus.Replaced);
        assertEquals(report1.getOrderID(),
                     report3.getOriginalOrderID());
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        expectedReports.clear();
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear();
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.add(report1);
        orderManager.add(report2);
        orderManager.add(report3);
        ExecutionReport report4 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report4.getOrderID()));
        assertFalse(report3.getOrderID().equals(report4.getOrderID()));
        orderManager.add(report4);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        expectedReports.putAll(report4.getOrderID(),
                               Arrays.asList(report4));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear();
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
    }
    /**
     * Tests {@link OrderHistoryManager#clear(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testClearOrderHistoryForOrderId()
            throws Exception
    {
        Multimap<OrderID,ReportBase> expectedReports = LinkedHashMultimap.create();
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderIds().isEmpty());
        orderManager.clear(null);
        assertTrue(orderManager.getOrderIds().isEmpty());
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ExecutionReport[] { report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear(report1.getOrderID());
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.add(report1);
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ExecutionReport[] { report2, report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.clear(report2.getOrderID());
        expectedReports.clear();
        verifyOrderHistory(orderManager,
                           expectedReports);
        orderManager.add(report1);
        orderManager.add(report2);
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          report1.getOrderID().toString(),
                                                          OrderStatus.Replaced);
        assertEquals(report1.getOrderID(),
                     report3.getOriginalOrderID());
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        expectedReports.clear();
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report3,report2,report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report4 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report4.getOrderID()));
        assertFalse(report3.getOrderID().equals(report4.getOrderID()));
        orderManager.add(report4);
        expectedReports.clear();
        expectedReports.putAll(report4.getOrderID(),
                               Arrays.asList(report4));
        orderManager.clear(report3.getOrderID());
        verifyOrderHistory(orderManager,
                           expectedReports);
    }
    /**
     * Tests the ability to process order cancel rejects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCancelRejects()
            throws Exception
    {
        Multimap<OrderID,ReportBase> expectedReports = LinkedHashMultimap.create();
        OrderHistoryManager orderManager = new OrderHistoryManager();
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ReportBase[] { report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
        expectedReports.clear();
        OrderCancelReject report2 = generateOrderCancelReject("order-" + counter.incrementAndGet(),
                                                              report1.getOrderID().getValue());
        orderManager.add(report2);
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(new ReportBase[] { report2, report1 }));
        verifyOrderHistory(orderManager,
                           expectedReports);
    }
    /**
     * Tests {@link OrderHistoryManager#getOrderChain(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOrderChain()
            throws Exception
    {
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderChain(null).isEmpty());
        ExecutionReport report1 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        Set<OrderID> report1OrderChain = orderManager.getOrderChain(report1.getOrderID());
        assertEquals(1,
                     report1OrderChain.size());
        assertTrue(report1OrderChain.contains(report1.getOrderID()));
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().getValue(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        assertEquals(1,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        ExecutionReport report3 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          report1.getOrderID().getValue(),
                                                          OrderStatus.PendingReplace);
        orderManager.add(report3);
        Set<OrderID> report3OrderChain = orderManager.getOrderChain(report3.getOrderID());
        assertEquals(2,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID(), report3.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        assertEquals(report1OrderChain,
                     report3OrderChain);
        ExecutionReport report4 = generateExecutionReport(report3.getOrderID().getValue(),
                                                          report1.getOrderID().getValue(),
                                                          OrderStatus.Replaced);
        orderManager.add(report4);
        assertEquals(2,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID(), report3.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        assertEquals(report1OrderChain,
                     report3OrderChain);
        ExecutionReport report5 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          report4.getOrderID().getValue(),
                                                          OrderStatus.PendingCancel);
        orderManager.add(report5);
        Set<OrderID> report5OrderChain = orderManager.getOrderChain(report5.getOrderID());
        assertEquals(3,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID(), report3.getOrderID(), report5.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        assertEquals(report1OrderChain,
                     report3OrderChain);
        assertEquals(report1OrderChain,
                     report5OrderChain);
        OrderCancelReject report6 = generateOrderCancelReject(report5.getOrderID().getValue(),
                                                              report4.getOrderID().getValue());
        orderManager.add(report6);
        Set<OrderID> report6OrderChain = orderManager.getOrderChain(report6.getOrderID());
        assertEquals(3,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID(), report3.getOrderID(), report5.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        assertEquals(report1OrderChain,
                     report3OrderChain);
        assertEquals(report1OrderChain,
                     report5OrderChain);
        assertEquals(report1OrderChain,
                     report6OrderChain);
        ExecutionReport report9 = generateExecutionReport("order-" + counter.incrementAndGet(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report9);
        Set<OrderID> report9OrderChain = orderManager.getOrderChain(report9.getOrderID());
        assertEquals(3,
                     report1OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report1.getOrderID(), report3.getOrderID(), report5.getOrderID() },
                                 report1OrderChain.toArray(new OrderID[report1OrderChain.size()])));
        assertEquals(report1OrderChain,
                     report3OrderChain);
        assertEquals(report1OrderChain,
                     report5OrderChain);
        assertEquals(report1OrderChain,
                     report6OrderChain);
        assertEquals(1,
                     report9OrderChain.size());
        assertTrue(Arrays.equals(new OrderID[] { report9.getOrderID() },
                                 report9OrderChain.toArray(new OrderID[report9OrderChain.size()])));
        orderManager.clear(report9.getOrderID());
        assertTrue(report9OrderChain.isEmpty());
        orderManager.clear();
        assertTrue(report1OrderChain.isEmpty());
        assertTrue(report3OrderChain.isEmpty());
        assertTrue(report5OrderChain.isEmpty());
        assertTrue(report6OrderChain.isEmpty());
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
        final OrderHistoryManager manager = new OrderHistoryManager();
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
     * Tests that {@link OrderHistoryManager#getReportHistoryFor(OrderID)} is not populated when the initial value is empty.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSubsequentPopulation()
            throws Exception
    {
        OrderHistoryManager orderManager = new OrderHistoryManager();
        OrderID orderID = new OrderID("myorder-" + System.nanoTime());
        Deque<ReportBase> reportHistory = orderManager.getReportHistoryFor(orderID);
        assertTrue(reportHistory.isEmpty());
        ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport(orderID.getValue(),
                                                                             null,
                                                                             OrderStatus.New);
        orderManager.add(report1);
        assertTrue(reportHistory.isEmpty());
        reportHistory = orderManager.getReportHistoryFor(orderID);
        assertEquals(1,
                     reportHistory.size());
        assertEquals(report1,
                     reportHistory.getFirst());
    }
    /**
     * Tests that pre-searching for an order doesn't affect later order processing.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testPreSearchOrderChain()
            throws Exception
    {
        OrderHistoryManager orderManager = new OrderHistoryManager();
        // create original order
        OrderID orderID = new OrderID("myorder-" + System.nanoTime());
        ReportBase report1 = OrderHistoryManagerTest.generateExecutionReport(orderID.getValue(),
                                                                             null,
                                                                             OrderStatus.New);
        orderManager.add(report1);
        assertEquals(orderManager.getLatestReportFor(orderID).getOrderStatus(),
                     OrderStatus.New);
        // create replacement order with a different status and pre-search for it
        OrderID replacementOrderID = new OrderID("myorder-" + System.nanoTime());
        assertFalse(orderID.equals(replacementOrderID));
        ReportBase report2 = OrderHistoryManagerTest.generateExecutionReport(replacementOrderID.getValue(),
                                                                             orderID.getValue(),
                                                                             OrderStatus.Canceled);
        orderManager.add(report2);
        Deque<ReportBase> originalReportHistory = orderManager.getReportHistoryFor(orderID);
        Deque<ReportBase> replacementReportHistory = orderManager.getReportHistoryFor(replacementOrderID);
        assertEquals(OrderStatus.Canceled,
                     orderManager.getLatestReportFor(orderID).getOrderStatus());
        assertEquals(OrderStatus.Canceled,
                     orderManager.getLatestReportFor(replacementOrderID).getOrderStatus());
        assertEquals(orderManager.getLatestReportFor(orderID),
                     orderManager.getLatestReportFor(replacementOrderID));
        assertEquals(originalReportHistory.size(),
                     replacementReportHistory.size());
    }
    /**
     * Verifies that the given <code>OrderHistoryManager</code> contains the given <code>ReportBase</code> objects.
     * 
     * <p>The <code>ReportBase</code> values are assumed to be in the order they are expected to appear.
     *
     * @param inManager an <code>OrderTracker</code> value
     * @param inExpectedReports a <code>List&lt;ReportBase&gt;</code> value containing the expected reports
     * @throws Exception if an unexpected error occurs
     */
    private void verifyOrderHistory(OrderHistoryManager inManager,
                                    Multimap<OrderID,ReportBase> inExpectedReports)
            throws Exception
    {
        assertNotNull(inManager.toString());
        assertNotNull(inManager.display());
        if(SLF4JLoggerProxy.isDebugEnabled(OrderHistoryManagerTest.class)) {
            SLF4JLoggerProxy.debug(OrderHistoryManagerTest.class,
                                   inManager.display());
        }
        for(Map.Entry<OrderID,ReportBase> entry : inExpectedReports.entries()) {
            Collection<ReportBase> expectedEntryReports = inExpectedReports.get(entry.getKey());
            Collection<ReportBase> actualEntryReports = inManager.getReportHistoryFor(entry.getKey());
            assertEquals(expectedEntryReports.size(),
                         actualEntryReports.size());
            Iterator<ReportBase> expectedIterator = expectedEntryReports.iterator();
            Iterator<ReportBase> actualIterator = actualEntryReports.iterator();
            while(expectedIterator.hasNext()) {
                assertTrue(actualIterator.hasNext());
                assertEquals(expectedIterator.next(),
                             actualIterator.next());
            }
        }
        Set<ReportBase> expectedOpenOrders = new HashSet<ReportBase>();
        Set<OrderID> expectedOpenOrderIds = new HashSet<OrderID>();
        Set<OrderID> allActualOrders = inManager.getOrderIds();
        for(OrderID orderID : allActualOrders) {
            ReportBase latestReport = inManager.getLatestReportFor(orderID);
            if(latestReport.getOrderStatus().isCancellable()) {
                expectedOpenOrders.add(latestReport);
                expectedOpenOrderIds.add(latestReport.getOrderID());
            }
        }
        Map<OrderID,ExecutionReport> actualOpenOrders = inManager.getOpenOrders();
        CollectionAssert.assertArrayPermutation(expectedOpenOrders.toArray(new ReportBase[expectedOpenOrders.size()]),
                                                actualOpenOrders.values().toArray(new ReportBase[actualOpenOrders.size()]));
        CollectionAssert.assertArrayPermutation(expectedOpenOrderIds.toArray(new OrderID[expectedOpenOrderIds.size()]),
                                                actualOpenOrders.keySet().toArray(new OrderID[actualOpenOrders.size()]));
    }
    /**
     * Generates an <code>ExecutionReport</code> for the given <code>OrderID</code> value and <code>OrderStatus</code>.
     * 
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderHistoryManagerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOriginalOrderID a <code>String</code> value or <code>null</code>
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static ExecutionReport generateExecutionReport(String inOrderID,
                                                          String inOriginalOrderID,
                                                          OrderStatus inOrderStatus)
            throws Exception
    {
        BrokerID broker = new BrokerID("broker-" + counter.incrementAndGet());
        UserID user = new UserID(counter.incrementAndGet());
        return factory.createExecutionReport(generateFixExecutionReport(inOrderID,
                                                                        inOriginalOrderID,
                                                                        inOrderStatus),
                                             broker,
                                             Originator.Broker,
                                             user,
                                             user);
    }
    /**
     * Generates an <code>OrderCancelReject</code> for the given <code>OrderID</code> value.
     *
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderHistoryManagerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOriginalOrderID a <code>String</code> value
     * @return an <code>OrderCancelReject</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static OrderCancelReject generateOrderCancelReject(String inOrderID,
                                                              String inOriginalOrderID)
            throws Exception
    {
        BrokerID broker = new BrokerID("broker-" + counter.incrementAndGet());
        UserID user = new UserID(counter.incrementAndGet());
        return factory.createOrderCancelReject(generateFixOrderCancelReject(inOrderID,
                                                                            inOriginalOrderID),
                                               broker,
                                               Originator.Broker,
                                               user,
                                               user);
    }
    /**
     * Generates a <code>Message</code> containing an <code>ExecutionReport</code> for the given <code>OrderID</code> value and <code>OrderStatus</code>.
     * 
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderHistoryManagerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOriginalOrderID a <code>String</code> value or <code>null</code>
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static Message generateFixExecutionReport(String inOrderID,
                                                     String inOriginalOrderID,
                                                     OrderStatus inOrderStatus)
            throws Exception
    {
        Message msg = fixVersion.getMessageFactory().createMessage(MsgType.EXECUTION_REPORT);
        msg.getHeader().setField(new MsgSeqNum(counter.incrementAndGet()));
        msg.getHeader().setField(new SenderCompID("sender"));
        msg.getHeader().setField(new TargetCompID("target"));
        msg.getHeader().setField(new SendingTime(new Date()));
        msg.setField(new ExecID(String.valueOf(counter.incrementAndGet())));
        if(orderType != null) {
            switch(orderType) {
                case Market:
                    msg.setField(new OrdType(OrdType.MARKET));
                    break;
                case Limit:
                    msg.setField(new OrdType(OrdType.LIMIT));
                    msg.setField(new Price(EventTestBase.generateDecimalValue()));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        msg.setField(new Symbol("colin-rocks"));
        msg.setField(new Side(Side.BUY));
        msg.setField(new OrdStatus(inOrderStatus.getFIXValue()));
        msg.setField(new AvgPx(EventTestBase.generateDecimalValue()));
        msg.setField(new quickfix.field.OrderID(inOrderID));
        msg.setField(new ClOrdID(inOrderID));
        msg.setField(new CumQty(EventTestBase.generateDecimalValue()));
        msg.setField(new ExecType(ExecType.NEW));
        msg.setField(new LeavesQty(EventTestBase.generateDecimalValue()));
        msg.setField(new OrderQty(EventTestBase.generateDecimalValue()));
        msg.setField(new LastPx(EventTestBase.generateDecimalValue()));
        msg.setField(new LastQty(EventTestBase.generateDecimalValue()));
        msg.setField(new TransactTime(new Date()));
        if(inOriginalOrderID != null) {
            msg.setField(new OrigClOrdID(inOriginalOrderID));
        }
        msg.toString();
        FIXDataDictionaryManager.getFIXDataDictionary(fixVersion).getDictionary().validate(msg);
        return msg;
    }
    /**
     * Generates a <code>Message</code> containing an <code>OrderCancelReject</code> for the given <code>OrderID</code> value.
     * 
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderHistoryManagerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOriginalOrderID a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static Message generateFixOrderCancelReject(String inOrderID,
                                                       String inOriginalOrderID)
            throws Exception
    {
        Message msg = fixVersion.getMessageFactory().createMessage(MsgType.ORDER_CANCEL_REJECT);
        msg.getHeader().setField(new MsgSeqNum(counter.incrementAndGet()));
        msg.getHeader().setField(new SenderCompID("sender"));
        msg.getHeader().setField(new TargetCompID("target"));
        msg.getHeader().setField(new SendingTime(new Date()));
        msg.setField(new OrdStatus(OrderStatus.Rejected.getFIXValue()));
        msg.setField(new quickfix.field.OrderID(inOrderID));
        msg.setField(new ClOrdID(inOrderID));
        msg.setField(new TransactTime(new Date()));
        msg.setField(new CxlRejResponseTo(CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        msg.setField(new OrigClOrdID(inOriginalOrderID));
        msg.toString();
        FIXDataDictionaryManager.getFIXDataDictionary(fixVersion).getDictionary().validate(msg);
        return msg;
    }
    /**
     * the factory used to construct FIX-agnostic objects 
     */
    private static Factory factory;
    /**
     * the FIX version used to construct FIX messages
     */
    private static FIXVersion fixVersion = FIXVersion.FIX_SYSTEM;
    /**
     * user to guarantee unique ids
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
    /**
     * value used in generated execution reports 
     */
    public static OrderType orderType = OrderType.Market;
}

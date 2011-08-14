package org.marketcetera.trade.utils;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.test.CollectionAssert;

import quickfix.Message;
import quickfix.field.*;
import quickfix.field.Side;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Tests {@link OrderHistoryManager} class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        LoggerConfiguration.logSetup();
        factory = Factory.getInstance();
        FIXDataDictionary dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(dataDictionary == null) {
            FIXDataDictionaryManager.initialize(fixVersion, 
                                                fixVersion.getDataDictionaryURL());
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
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
                                                          null,
                                                          OrderStatus.New);
        manager.add(report1);
        assertEquals(report1,
                     manager.getLatestReportFor(report1.getOrderID()));
        assertNull(manager.getLatestReportFor(new OrderID("some-orderid-that-doesn't-exist")));
    }
    /**
     * Tests {@link OrderHistoryManager#add(ExecutionReport)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAdd()
            throws Exception
    {
        final OrderHistoryManager orderManager = new OrderHistoryManager();
        Multimap<OrderID,ExecutionReport> expectedReports = LinkedHashMultimap.create();
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                orderManager.add(null);
            }
        };
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report3 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report4 = generateExecutionReport("order-" + System.nanoTime(),
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
     * Tests {@link OrderHistoryManager#getReportHistoryFor(OrderID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetReportHistoryFor()
            throws Exception
    {
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getReportHistoryFor(null).isEmpty());
        assertTrue(orderManager.getReportHistoryFor(new OrderID("this-order-doesn't-exist")).isEmpty());
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
                                                          null,
                                                          OrderStatus.New);
        orderManager.add(report1);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().toString(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        orderManager.add(report2);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report2, report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        ExecutionReport report3 = generateExecutionReport("order-" + System.nanoTime(),
                                                          null,
                                                          OrderStatus.New);
        assertFalse(report1.getOrderID().equals(report3.getOrderID()));
        orderManager.add(report3);
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report2, report1 },
                                                orderManager.getReportHistoryFor(report1.getOrderID()).toArray(new ExecutionReport[0]));
        CollectionAssert.assertArrayPermutation(new ExecutionReport[] { report3 },
                                                orderManager.getReportHistoryFor(report3.getOrderID()).toArray(new ExecutionReport[0]));
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
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report3 = generateExecutionReport("order-" + System.nanoTime(),
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
        Multimap<OrderID,ExecutionReport> expectedReports = LinkedHashMultimap.create();
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderIds().isEmpty());
        orderManager.clear();
        assertTrue(orderManager.getOrderIds().isEmpty());
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report3 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report4 = generateExecutionReport("order-" + System.nanoTime(),
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
        Multimap<OrderID,ExecutionReport> expectedReports = LinkedHashMultimap.create();
        OrderHistoryManager orderManager = new OrderHistoryManager();
        assertTrue(orderManager.getOrderIds().isEmpty());
        orderManager.clear(null);
        assertTrue(orderManager.getOrderIds().isEmpty());
        verifyOrderHistory(orderManager,
                           expectedReports);
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report3 = generateExecutionReport("order-" + System.nanoTime(),
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
        ExecutionReport report4 = generateExecutionReport("order-" + System.nanoTime(),
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
     * Verifies that the given <code>OrderTracker</code> contains the given <code>ExecutionReport</code> objects.
     * 
     * <p>The <code>ExecutionReport</code> values are assumed to be in the order they are expected to appear.
     *
     * @param inManager an <code>OrderTracker</code> value
     * @param inExpectedReports a <code>List&lt;ExecutionReport&gt;</code> value containing the expected reports
     * @throws Exception if an unexpected error occurs
     */
    private void verifyOrderHistory(OrderHistoryManager inManager,
                                    Multimap<OrderID,ExecutionReport> inExpectedReports)
            throws Exception
    {
        assertNotNull(inManager.toString());
        for(Map.Entry<OrderID,ExecutionReport> entry : inExpectedReports.entries()) {
            Collection<ExecutionReport> expectedEntryReports = inExpectedReports.get(entry.getKey());
            Collection<ExecutionReport> actualEntryReports = inManager.getReportHistoryFor(entry.getKey());
            assertEquals(expectedEntryReports.size(),
                         actualEntryReports.size());
            Iterator<ExecutionReport> expectedIterator = expectedEntryReports.iterator();
            Iterator<ExecutionReport> actualIterator = actualEntryReports.iterator();
            while(expectedIterator.hasNext()) {
                assertTrue(actualIterator.hasNext());
                assertEquals(expectedIterator.next(),
                             actualIterator.next());
            }
        }
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
        BrokerID broker = new BrokerID("broker-" + System.nanoTime());
        UserID user = new UserID(System.nanoTime());
        return factory.createExecutionReport(generateFixExecutionReport(inOrderID,
                                                                        inOriginalOrderID,
                                                                        inOrderStatus),
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
        msg.setField(new ExecID(String.valueOf(System.nanoTime())));
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
}

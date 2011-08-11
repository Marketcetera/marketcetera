package org.marketcetera.trade.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
     * Tests {@link OrderHistoryManager#add(org.marketcetera.trade.ExecutionReport)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddBasicLifecycle()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderHistoryManager().add(null);
            }
        };
        Multimap<OrderID,ExecutionReport> expectedReports = LinkedHashMultimap.create();
        verifyOrderHistory(new OrderHistoryManager(),
                           expectedReports);
        ExecutionReport report1 = generateExecutionReport("order-" + System.nanoTime(),
                                                          null,
                                                          OrderStatus.New);
        expectedReports.put(report1.getOrderID(),
                            report1);
        assertNotNull(report1.getOrderID());
        assertNull(report1.getOriginalOrderID());
        OrderHistoryManager orderManager = new OrderHistoryManager();
        orderManager.add(report1);
        verifyOrderHistory(orderManager,
                           expectedReports);
        verifyOrderHistory(orderManager,
                           expectedReports);
        // add a new ER for the same order
        ExecutionReport report2 = generateExecutionReport(report1.getOrderID().getValue(),
                                                          null,
                                                          OrderStatus.PartiallyFilled);
        expectedReports.removeAll(report1.getOrderID());
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report2, report1));
        orderManager.add(report2);
        verifyOrderHistory(orderManager,
                           expectedReports);
        // create a report with an original order ID
        ExecutionReport report3 = generateExecutionReport("report-" + System.nanoTime(),
                                                          report2.getOrderID().getValue(),
                                                          OrderStatus.Replaced);
        assertNotNull(report3.getOriginalOrderID());
        orderManager.add(report3);
        expectedReports.removeAll(report1.getOrderID());
        // order chain should now show up for both original and new order ID
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report3, report2, report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report3, report2, report1));
        verifyOrderHistory(orderManager,
                           expectedReports);
        // replace the replaced with another replace
        ExecutionReport report4 = generateExecutionReport("report-" + System.nanoTime(),
                                                          report3.getOrderID().getValue(),
                                                          OrderStatus.Replaced);
        assertNotNull(report4.getOriginalOrderID());
        orderManager.add(report4);
        expectedReports.clear();
        // order chain should now have all 4 for any of the family
        expectedReports.putAll(report1.getOrderID(),
                               Arrays.asList(report4, report3, report2, report1));
        expectedReports.putAll(report3.getOrderID(),
                               Arrays.asList(report4, report3, report2, report1));
        expectedReports.putAll(report4.getOrderID(),
                               Arrays.asList(report4, report3, report2, report1));
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

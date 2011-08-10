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
import org.marketcetera.util.test.CollectionAssert;

import quickfix.Message;
import quickfix.field.*;
import quickfix.field.Side;

/* $License$ */

/**
 * Tests {@link OrderTracker} class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderTrackerTest
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
     * Tests {@link OrderTracker#add(org.marketcetera.trade.ExecutionReport)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAdd()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker().add(null);
            }
        };
        verifyTracker(new OrderTracker(),
                      EMPTY_REPORTS);
        Collection<ExecutionReport> expectedReports = new ArrayList<ExecutionReport>();
        ExecutionReport report = generateExecutionReport("order-" + System.nanoTime(),
                                                         null,
                                                         OrderStatus.New);
        expectedReports.add(report);
        assertNotNull(report.getOrderID());
        assertNull(report.getOriginalOrderID());
        OrderTracker tracker = new OrderTracker();
        tracker.add(report);
        verifyTracker(tracker,
                      expectedReports);
        // add the same report again
        tracker.add(report);
        expectedReports.add(report);
        verifyTracker(tracker,
                      expectedReports);
        // add a new ER for the same order
        report = generateExecutionReport(report.getOrderID().getValue(),
                                         null,
                                         OrderStatus.PartiallyFilled);
        expectedReports.add(report);
        tracker.add(report);
        verifyTracker(tracker,
                      expectedReports);
        // create a report with an original order ID
        report = generateExecutionReport(report.getOrderID().getValue(),
                                         "report-" + System.nanoTime(),
                                         OrderStatus.Replaced);
        assertNotNull(report.getOriginalOrderID());
        tracker.add(report);
        expectedReports.add(report);
        verifyTracker(tracker,
                      expectedReports);
        // create an ER with 
    }
    /**
     * Verifies that the given <code>OrderTracker</code> contains the given <code>ExecutionReport</code> objects.
     * 
     * <p>The <code>ExecutionReport</code> values are assumed to be in the order they are expected to appear.
     *
     * @param inTracker an <code>OrderTracker</code> value
     * @param inExpectedReports a <code>Collection&lt;ExecutionReport&gt;</code> value containing the expected reports
     * @throws Exception if an unexpected error occurs
     */
    private void verifyTracker(OrderTracker inTracker,
                               Collection<ExecutionReport> inExpectedReports)
            throws Exception
    {
        assertNotNull(inTracker.toString());
        Set<OrderID> actualOrderIds = inTracker.getOrderIds();
        Set<OrderID> expectedOrderIds = new HashSet<OrderID>();
        Map<OrderID,Collection<ExecutionReport>> expectedReports = new HashMap<OrderID,Collection<ExecutionReport>>();
        for(ExecutionReport report : inExpectedReports) {
            Collection<ExecutionReport> reports = expectedReports.get(report.getOrderID());
            if(reports == null) {
                reports = new ArrayList<ExecutionReport>();
                expectedReports.put(report.getOrderID(),
                                    reports);
            }
            reports.add(report);
            expectedOrderIds.add(report.getOrderID());
            if(report.getOriginalOrderID() != null) {
                expectedOrderIds.add(report.getOriginalOrderID());
                reports = expectedReports.get(report.getOriginalOrderID());
                if(reports == null) {
                    reports = new ArrayList<ExecutionReport>();
                    expectedReports.put(report.getOriginalOrderID(),
                                        reports);
                }
                reports.add(report);
            }
        }
        CollectionAssert.assertArrayPermutation(expectedOrderIds.toArray(),
                                                actualOrderIds.toArray());
        for(Map.Entry<OrderID,Collection<ExecutionReport>> entry : expectedReports.entrySet()) {
            Collection<ExecutionReport> expectedEntryReports = expectedReports.get(entry.getKey());
            Collection<ExecutionReport> actualEntryReports = inTracker.getReportHistoryFor(entry.getKey());
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
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderTrackerTest#fixVersion}.
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
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderTrackerTest#fixVersion}.
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
    /**
     * static value to use for empty reports
     */
    private static final List<ExecutionReport> EMPTY_REPORTS = Collections.emptyList();
}

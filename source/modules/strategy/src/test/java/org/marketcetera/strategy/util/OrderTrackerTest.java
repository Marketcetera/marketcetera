package org.marketcetera.strategy.util;

import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.marketcetera.strategy.Messages.*;

import java.math.BigDecimal;
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
import org.marketcetera.trade.SecurityType;

import quickfix.Message;
import quickfix.field.*;
import quickfix.field.Side;

/* $License$ */

/**
 * Tests {@link OrderTracker}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderTrackerTest
{
    @BeforeClass
    public static void setupOrderTracker()
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
     * Tests the <code>OrderTracker</code> non-private constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void constructor()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(NULL_ORDER.getText())
        {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker(null);
            }
        };
        // unknown subclass of Order
        final Order order = new Order() {
            @Override
            public BrokerID getBrokerID()
            {
                return null;
            }
            @Override
            public SecurityType getSecurityType()
            {
                return null;
            }
            @Override
            public void setBrokerID(BrokerID inBrokerID)
            {
            }
            private static final long serialVersionUID = 1L;
        };
        new ExpectedFailure<UnsupportedOperationException>(UNKNOWN_ORDER_TYPE.getText(order))
        {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker(order);
            }
        };
        final FIXOrder badFixOrder = generateFIXOrder();
        badFixOrder.getMessage().removeField(ClOrdID.FIELD);
        // unknown subclass of Order
        new ExpectedFailure<IllegalArgumentException>(NO_ORDER_ID.getText(badFixOrder))
        {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker(badFixOrder);
            }
        };
        // null OrderID
        badFixOrder.getMessage().setField(new ClOrdID());
        new ExpectedFailure<IllegalArgumentException>(NULL_ORDER_ID.getText(badFixOrder))
        {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker(badFixOrder);
            }
        };
        // empty OrderID
        badFixOrder.getMessage().setField(new ClOrdID("  "));
        new ExpectedFailure<IllegalArgumentException>(NULL_ORDER_ID.getText(badFixOrder))
        {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderTracker(badFixOrder);
            }
        };
        // good FIX order
        FIXOrder fixOrder = generateFIXOrder();
        verifyOrderTracker(new OrderTracker(fixOrder),
                           fixOrder,
                           new OrderID(fixOrder.getFields().get(ClOrdID.FIELD)),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        // good OrderBase order
        OrderBase orderBase = factory.createOrderSingle(fixOrder.getMessage(),
                                                        fixOrder.getBrokerID());
        verifyOrderTracker(new OrderTracker(orderBase),
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
    }
    /**
     * Tests the ability to process <code>ExecutionReport</code> values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReportProcessing()
            throws Exception
    {
        FIXOrder fixOrder = generateFIXOrder();
        OrderBase orderBase = factory.createOrderSingle(fixOrder.getMessage(),
                                                        fixOrder.getBrokerID());
        final OrderTracker tracker = new OrderTracker(orderBase);
        new ExpectedFailure<IllegalArgumentException>(NULL_REPORT.getText())
        {
            @Override
            protected void run()
                    throws Exception
            {
                tracker.process(null);
            }
        };
        // execution report has an empty (and mismatching) OrderID
        ExecutionReport report = generateExecutionReport("    ",
                                                         OrderStatus.New);
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(OrderStatus.Unknown,
                     tracker.process(report));
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        // execution report has a non-empty (but still mismatching) OrderID
        OrderID otherOrderID = new OrderID("order-" + System.nanoTime());
        assertFalse(tracker.getUnderlyingOrderID().equals(otherOrderID));
        report = generateExecutionReport(otherOrderID.getValue(),
                                         OrderStatus.New);
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(OrderStatus.Unknown,
                     tracker.process(report));
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        // matching OrderID with all values
        report = generateExecutionReport(orderBase.getOrderID().getValue(),
                                         OrderStatus.New);
        List<ExecutionReport> expectedReports = new ArrayList<ExecutionReport>();
        expectedReports.add(report);
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           expectedReports,
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
    }
    /**
     * Tests <code>ExecutionReport</code> values with <code>null</code> quantity values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNullExecutionReportValues()
            throws Exception
    {
        FIXOrder fixOrder = generateFIXOrder();
        OrderBase orderBase = factory.createOrderSingle(fixOrder.getMessage(),
                                                        fixOrder.getBrokerID());
        OrderTracker tracker = new OrderTracker(orderBase);
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        // generate an execution report with a null order quantity
        Message rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                                       OrderStatus.New);
        rawReport.removeField(OrderQty.FIELD);
        ExecutionReport report = generateExecutionReport(rawReport);
        assertNull(report.getOrderQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getOrderQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getOrderQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           ZERO,
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // null average price
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(AvgPx.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getAveragePrice());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getAveragePrice());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getAveragePrice());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           ZERO,
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // null cumulative quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(CumQty.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getCumulativeQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getCumulativeQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getCumulativeQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           ZERO,
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // null last price
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(LastPx.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getLastPrice());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLastPrice());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLastPrice());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           ZERO,
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // null last quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(LastQty.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getLastQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLastQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLastQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           ZERO,
                           report.getLeavesQuantity());
        // null leaves quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(LeavesQty.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getLeavesQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLeavesQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLeavesQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           ZERO);
        // null order status
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.removeField(OrdStatus.FIELD);
        report = generateExecutionReport(rawReport);
        assertNull(report.getOrderStatus());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(OrderStatus.Unknown,
                     tracker.process(report));
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.Unknown,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
    }
    /**
     * Tests <code>ExecutionReport</code> values with <code>null</code> quantity values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testZeroExecutionReportValues()
            throws Exception
    {
        FIXOrder fixOrder = generateFIXOrder();
        OrderBase orderBase = factory.createOrderSingle(fixOrder.getMessage(),
                                                        fixOrder.getBrokerID());
        OrderTracker tracker = new OrderTracker(orderBase);
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        // generate an execution report with a zero order quantity
        Message rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                                       OrderStatus.New);
        rawReport.setField(new OrderQty(ZERO));
        ExecutionReport report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getOrderQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getOrderQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getOrderQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // zero average price
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.setField(new AvgPx(ZERO));
        report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getAveragePrice());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getAveragePrice());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getAveragePrice());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // zero cumulative quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.setField(new CumQty(ZERO));
        report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getCumulativeQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getCumulativeQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getCumulativeQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // zero last price
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.setField(new LastPx(ZERO));
        report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getLastPrice());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLastPrice());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLastPrice());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // zero last quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.setField(new LastQty(ZERO));
        report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getLastQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLastQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLastQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        // zero leaves quantity
        tracker = new OrderTracker(orderBase);
        rawReport = generateFixExecutionReport(orderBase.getOrderID().getValue(),
                                               OrderStatus.New);
        rawReport.setField(new LeavesQty(ZERO));
        report = generateExecutionReport(rawReport);
        assertEquals(ZERO,
                     report.getLeavesQuantity());
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(ZERO,
                     tracker.getLeavesQuantity());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        assertEquals(ZERO,
                     tracker.getLeavesQuantity());
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           Arrays.asList(new ExecutionReport[] { report }),
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
    }
    /**
     * Tests the processing of a number of realistic execution reports.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMultipleReports()
            throws Exception
    {
        FIXOrder fixOrder = generateFIXOrder();
        OrderBase orderBase = factory.createOrderSingle(fixOrder.getMessage(),
                                                        fixOrder.getBrokerID());
        OrderTracker tracker = new OrderTracker(orderBase);
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           EMPTY_REPORTS,
                           OrderStatus.Unknown,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO,
                           ZERO);
        ExecutionReport report = generateExecutionReport(orderBase.getOrderID().getValue(),
                                                         OrderStatus.New);
        List<ExecutionReport> expectedReports = new ArrayList<ExecutionReport>();
        expectedReports.add(report);
        assertEquals(OrderStatus.Unknown,
                     tracker.getStatus());
        assertEquals(OrderStatus.New,
                     tracker.process(report));
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           expectedReports,
                           OrderStatus.New,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
        report = generateExecutionReport(orderBase.getOrderID().getValue(),
                                         OrderStatus.Filled);
        expectedReports.add(report);
        assertEquals(OrderStatus.New,
                     tracker.getStatus());
        assertEquals(OrderStatus.Filled,
                     tracker.process(report));
        verifyOrderTracker(tracker,
                           orderBase,
                           orderBase.getOrderID(),
                           expectedReports,
                           OrderStatus.Filled,
                           report.getOrderQuantity(),
                           report.getAveragePrice(),
                           report.getCumulativeQuantity(),
                           report.getLastPrice(),
                           report.getLastQuantity(),
                           report.getLeavesQuantity());
    }
    /**
     * Generates a FIXOrder with test values guaranteed to be valid.
     *
     * @return a <code>FIXOrder</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static FIXOrder generateFIXOrder()
            throws Exception
    {
        BrokerID broker = new BrokerID("broker-" + System.nanoTime());
        Message msg = fixVersion.getMessageFactory().createMessage(MsgType.ORDER_SINGLE);
        msg.getHeader().setField(new MsgSeqNum(counter.incrementAndGet()));
        msg.getHeader().setField(new SenderCompID("sender"));
        msg.getHeader().setField(new TargetCompID("target"));
        msg.getHeader().setField(new SendingTime(new Date()));
        msg.setField(new Symbol("symbol-" + System.nanoTime()));
        msg.setField(new Side(Side.BUY));
        msg.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        msg.setField(new OrdType(OrdType.MARKET));
        msg.setField(new ClOrdID(String.valueOf(System.nanoTime())));
        msg.setField(new TransactTime(new Date()));
        msg.toString();
        FIXDataDictionaryManager.getFIXDataDictionary(fixVersion).getDictionary().validate(msg);
        return factory.createOrder(msg,
                                   broker);
    }
    /**
     * Generates an <code>ExecutionReport</code> for the given <code>OrderID</code> value and <code>OrderStatus</code>.
     * 
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderTrackerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static ExecutionReport generateExecutionReport(String inOrderID,
                                                          OrderStatus inOrderStatus)
            throws Exception
    {
        BrokerID broker = new BrokerID("broker-" + System.nanoTime());
        UserID user = new UserID(System.nanoTime());
        return factory.createExecutionReport(generateFixExecutionReport(inOrderID,
                                                                        inOrderStatus),
                                             broker,
                                             Originator.Broker,
                                             user,
                                             user);
    }
    /**
     * Generates an <code>ExecutionReport</code> for the given <code>OrderID</code> value and <code>OrderStatus</code>.
     * 
     * <p><code>ExecutionReport</code> objects generated by this method are guaranteed to be valid according to {@link OrderTrackerTest#fixVersion}.
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inMessage a <code>Message</code> to use for the <code>ExecutionReport</code>
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static ExecutionReport generateExecutionReport(Message inMessage)
            throws Exception
    {
        BrokerID broker = new BrokerID("broker-" + System.nanoTime());
        UserID user = new UserID(System.nanoTime());
        return factory.createExecutionReport(inMessage,
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
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static Message generateFixExecutionReport(String inOrderID,
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
        msg.toString();
        FIXDataDictionaryManager.getFIXDataDictionary(fixVersion).getDictionary().validate(msg);
        return msg;
    }
    /**
     * Verifies the given actual <code>OrderTracker</code> matches the given expected attributes.
     *
     * @param inActualOrderTracker
     * @param inExpectedUnderlyingOrder
     * @param inExpectedUnderlyingOrderID
     * @param inExpectedReports
     * @param inExpectedOrderStatus
     * @param inExpectedQuantity
     * @param inExpectedAveragePrice
     * @param inExpectedCumulativeQuantity
     * @param inExpectedLastPrice
     * @param inExpectedLastQuantity
     * @param inExpectedLeavesQuantity
     * @throws Exception
     */
    private void verifyOrderTracker(OrderTracker inActualOrderTracker,
                                    Order inExpectedUnderlyingOrder,
                                    OrderID inExpectedUnderlyingOrderID,
                                    List<ExecutionReport> inExpectedReports,
                                    OrderStatus inExpectedOrderStatus,
                                    BigDecimal inExpectedQuantity,
                                    BigDecimal inExpectedAveragePrice,
                                    BigDecimal inExpectedCumulativeQuantity,
                                    BigDecimal inExpectedLastPrice,
                                    BigDecimal inExpectedLastQuantity,
                                    BigDecimal inExpectedLeavesQuantity)
            throws Exception
    {
        assertNotNull(inActualOrderTracker.toString());
        assertEquals(inExpectedUnderlyingOrder,
                     inActualOrderTracker.getUnderlyingOrder());
        assertEquals(inExpectedUnderlyingOrderID,
                     inActualOrderTracker.getUnderlyingOrderID());
        assertEquals(inExpectedReports,
                     inActualOrderTracker.getReports());
        assertEquals(inExpectedOrderStatus,
                     inActualOrderTracker.getStatus());
        assertEquals(inExpectedQuantity,
                     inActualOrderTracker.getOrderQuantity());
        assertEquals(inExpectedAveragePrice,
                     inActualOrderTracker.getAveragePrice());
        assertEquals(inExpectedCumulativeQuantity,
                     inActualOrderTracker.getCumulativeQuantity());
        assertEquals(inExpectedLastPrice,
                     inActualOrderTracker.getLastPrice());
        assertEquals(inExpectedLastQuantity,
                     inActualOrderTracker.getLastQuantity());
        assertEquals(inExpectedLeavesQuantity,
                     inActualOrderTracker.getLeavesQuantity());
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
/*
[Charon StrategyAgent] 20100815T214323239Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2001,ExecutionType=Rejected,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=0,OrderCapacity=null,OrderID=1281908459781,OrderQuantity=100,OrderStatus=Rejected,OrderType=Limit,OriginalOrderID=null,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11000,SendingTime=Sun Aug 15 14:43:30 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=Broker is unavailable. Try submitting your order again at a later time, or use another broker,TimeInForce=Day,TransactTime=Sun Aug 15 14:43:30 PDT 2010,BrokerOrderID=NONE,Price=1000,FIX Message=8=FIX.4.29=30235=834=049=ORS52=20100815-21:43:30.41156=ORS Client6=011=128190845978114=017=200120=021=131=032=037=NONE38=10039=840=244=100054=155=MSFT58=Broker is unavailable. Try submitting your order again at a later time, or use another broker59=060=20100815-21:43:30.410150=8151=0167=CS10=160}
[Charon StrategyAgent] 20100815T214515288Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2002,ExecutionType=PendingNew,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=10,OrderCapacity=null,OrderID=10001,OrderQuantity=10,OrderStatus=PendingNew,OrderType=Limit,OriginalOrderID=null,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11001,SendingTime=Sun Aug 15 14:45:22 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 14:45:22 PDT 2010,BrokerOrderID=NONE,Price=1000,FIX Message=8=FIX.4.29=19735=834=049=ORS52=20100815-21:45:22.95856=ORS Client6=011=1000114=017=200220=021=131=032=037=NONE38=1039=A40=244=100054=155=MSFT59=060=20100815-21:45:22.957150=A151=10167=CS10=110}
[Charon StrategyAgent] 20100815T214515555Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=12001,ExecutionType=New,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=10,OrderCapacity=null,OrderID=10001,OrderQuantity=10,OrderStatus=New,OrderType=Limit,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11002,SendingTime=Sun Aug 15 14:45:29 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 14:45:28 PDT 2010,BrokerOrderID=12000,Price=1000,FIX Message=8=FIX.4.29=20335=834=249=MRKTC-EXCH52=20100815-21:45:29.00356=210l-colin-a6=011=1000114=017=1200120=031=032=037=1200038=1039=040=244=100054=155=MSFT59=060=20100815-21:45:28.956150=0151=10167=CS10=154}
[Charon StrategyAgent] 20100815T214613473Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2003,ExecutionType=PendingNew,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=10,OrderCapacity=null,OrderID=10002,OrderQuantity=10,OrderStatus=PendingNew,OrderType=Market,OriginalOrderID=null,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11003,SendingTime=Sun Aug 15 14:46:21 PDT 2010,Side=Sell,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 14:46:21 PDT 2010,BrokerOrderID=NONE,Price=null,FIX Message=8=FIX.4.29=18935=834=049=ORS52=20100815-21:46:21.11856=ORS Client6=011=1000214=017=200320=021=131=032=037=NONE38=1039=A40=154=255=MSFT59=060=20100815-21:46:21.118150=A151=10167=CS10=243}
[Charon StrategyAgent] 20100815T214613600Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12003,ExecutionType=Unknown,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=0,OrderCapacity=null,OrderID=10001,OrderQuantity=10,OrderStatus=Filled,OrderType=Limit,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11004,SendingTime=Sun Aug 15 14:46:27 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 14:46:27 PDT 2010,BrokerOrderID=12000,Price=1000,FIX Message=8=FIX.4.29=21035=834=449=MRKTC-EXCH52=20100815-21:46:27.03856=210l-colin-a6=100011=1000114=1017=1200320=031=100032=1037=1200038=1039=240=244=100054=155=MSFT59=060=20100815-21:46:27.038150=2151=0167=CS10=241}
[Charon StrategyAgent] 20100815T214613624Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12004,ExecutionType=Unknown,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=0,OrderCapacity=null,OrderID=10002,OrderQuantity=10,OrderStatus=Filled,OrderType=Market,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11005,SendingTime=Sun Aug 15 14:46:27 PDT 2010,Side=Sell,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 14:46:27 PDT 2010,BrokerOrderID=12002,Price=null,FIX Message=8=FIX.4.29=20235=834=549=MRKTC-EXCH52=20100815-21:46:27.03956=210l-colin-a6=100011=1000214=1017=1200420=031=100032=1037=1200238=1039=240=154=255=MSFT59=060=20100815-21:46:27.038150=2151=0167=CS10=145}
[Charon StrategyAgent] 20100815T222714352Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2004,ExecutionType=PendingNew,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=1000,OrderCapacity=null,OrderID=10003,OrderQuantity=1000,OrderStatus=PendingNew,OrderType=Limit,OriginalOrderID=null,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11006,SendingTime=Sun Aug 15 15:27:20 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:20 PDT 2010,BrokerOrderID=NONE,Price=1000,FIX Message=8=FIX.4.29=20135=834=049=ORS52=20100815-22:27:20.74856=ORS Client6=011=1000314=017=200420=021=131=032=037=NONE38=100039=A40=244=100054=155=MSFT59=060=20100815-22:27:20.748150=A151=1000167=CS10=029}
[Charon StrategyAgent] 20100815T222714479Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=12006,ExecutionType=New,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=1000,OrderCapacity=null,OrderID=10003,OrderQuantity=1000,OrderStatus=New,OrderType=Limit,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11007,SendingTime=Sun Aug 15 15:27:27 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:27 PDT 2010,BrokerOrderID=12005,Price=1000,FIX Message=8=FIX.4.29=20835=834=8749=MRKTC-EXCH52=20100815-22:27:27.77056=210l-colin-a6=011=1000314=017=1200620=031=032=037=1200538=100039=040=244=100054=155=MSFT59=060=20100815-22:27:27.769150=0151=1000167=CS10=180}
[Charon StrategyAgent] 20100815T222728041Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2005,ExecutionType=PendingNew,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=10,OrderCapacity=null,OrderID=10004,OrderQuantity=10,OrderStatus=PendingNew,OrderType=Limit,OriginalOrderID=null,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11008,SendingTime=Sun Aug 15 15:27:34 PDT 2010,Side=Sell,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:34 PDT 2010,BrokerOrderID=NONE,Price=1,FIX Message=8=FIX.4.29=19435=834=049=ORS52=20100815-22:27:34.43456=ORS Client6=011=1000414=017=200520=021=131=032=037=NONE38=1039=A40=244=154=255=MSFT59=060=20100815-22:27:34.434150=A151=10167=CS10=213}
[Charon StrategyAgent] 20100815T222728149Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12008,ExecutionType=Unknown,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=990,OrderCapacity=null,OrderID=10003,OrderQuantity=1000,OrderStatus=PartiallyFilled,OrderType=Limit,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11009,SendingTime=Sun Aug 15 15:27:41 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:41 PDT 2010,BrokerOrderID=12005,Price=1000,FIX Message=8=FIX.4.29=21535=834=8849=MRKTC-EXCH52=20100815-22:27:41.46856=210l-colin-a6=100011=1000314=1017=1200820=031=100032=1037=1200538=100039=140=244=100054=155=MSFT59=060=20100815-22:27:41.468150=1151=990167=CS10=020}
[Charon StrategyAgent] 20100815T222728204Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12009,ExecutionType=Unknown,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=0,OrderCapacity=null,OrderID=10004,OrderQuantity=10,OrderStatus=Filled,OrderType=Limit,OriginalOrderID=null,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11010,SendingTime=Sun Aug 15 15:27:41 PDT 2010,Side=Sell,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:41 PDT 2010,BrokerOrderID=12007,Price=1,FIX Message=8=FIX.4.29=20835=834=8949=MRKTC-EXCH52=20100815-22:27:41.46856=210l-colin-a6=100011=1000414=1017=1200920=031=100032=1037=1200738=1039=240=244=154=255=MSFT59=060=20100815-22:27:41.468150=2151=0167=CS10=188}
[Charon StrategyAgent] 20100815T222736304Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2006,ExecutionType=PendingReplace,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=1000,OrderCapacity=null,OrderID=10006,OrderQuantity=1000,OrderStatus=PendingReplace,OrderType=Limit,OriginalOrderID=10003,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11011,SendingTime=Sun Aug 15 15:27:42 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:42 PDT 2010,BrokerOrderID=NONE,Price=100,FIX Message=8=FIX.4.29=20935=834=049=ORS52=20100815-22:27:42.70556=ORS Client6=011=1000614=017=200620=021=131=032=037=NONE38=100039=E40=241=1000344=10054=155=MSFT59=060=20100815-22:27:42.705150=E151=1000167=CS10=147}
[Charon StrategyAgent] 20100815T222736437Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12011,ExecutionType=PendingReplace,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=990,OrderCapacity=null,OrderID=10006,OrderQuantity=1000,OrderStatus=PendingReplace,OrderType=Limit,OriginalOrderID=10003,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11012,SendingTime=Sun Aug 15 15:27:49 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:49 PDT 2010,BrokerOrderID=12010,Price=100,FIX Message=8=FIX.4.29=22335=834=9049=MRKTC-EXCH52=20100815-22:27:49.74356=210l-colin-a6=100011=1000614=1017=1201120=031=100032=1037=1201038=100039=E40=241=1000344=10054=155=MSFT59=060=20100815-22:27:49.743150=E151=990167=CS10=156}
[Charon StrategyAgent] 20100815T222736489Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12012,ExecutionType=Replace,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=990,OrderCapacity=null,OrderID=10006,OrderQuantity=1000,OrderStatus=Replaced,OrderType=Limit,OriginalOrderID=10003,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11013,SendingTime=Sun Aug 15 15:27:49 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:49 PDT 2010,BrokerOrderID=12010,Price=100,FIX Message=8=FIX.4.29=22335=834=9149=MRKTC-EXCH52=20100815-22:27:49.74456=210l-colin-a6=100011=1000614=1017=1201220=031=100032=1037=1201038=100039=540=241=1000344=10054=155=MSFT59=060=20100815-22:27:49.744150=5151=990167=CS10=128}
[Charon StrategyAgent] 20100815T222739796Z WARN event Execution Report:{Account=null,AveragePrice=0,CumulativeQuantity=0,BrokerID=broker1,ExecutionID=2007,ExecutionType=PendingCancel,LastMarket=null,LastPrice=0,LastQuantity=0,LeavesQuantity=1000,OrderCapacity=null,OrderID=10008,OrderQuantity=1000,OrderStatus=PendingCancel,OrderType=null,OriginalOrderID=10006,Originator=Server,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11014,SendingTime=Sun Aug 15 15:27:46 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=null,TransactTime=Sun Aug 15 15:27:46 PDT 2010,BrokerOrderID=NONE,Price=null,FIX Message=8=FIX.4.29=18735=834=049=ORS52=20100815-22:27:46.17156=ORS Client6=011=1000814=017=200720=031=032=037=NONE38=100039=641=1000654=155=MSFT60=20100815-22:27:46.170150=6151=1000167=CS10=200}
[Charon StrategyAgent] 20100815T222739904Z WARN event Execution Report:{Account=null,AveragePrice=1000,CumulativeQuantity=10,BrokerID=broker1,ExecutionID=12013,ExecutionType=Canceled,LastMarket=null,LastPrice=1000,LastQuantity=10,LeavesQuantity=0,OrderCapacity=null,OrderID=10008,OrderQuantity=1000,OrderStatus=Canceled,OrderType=Limit,OriginalOrderID=10006,Originator=Broker,ActorUserID=1,ViewerUserID=1,PositionEffect=null,ReportID=11015,SendingTime=Sun Aug 15 15:27:53 PDT 2010,Side=Buy,Symbol=Equity[symbol=MSFT],Text=null,TimeInForce=Day,TransactTime=Sun Aug 15 15:27:53 PDT 2010,BrokerOrderID=12010,Price=100,FIX Message=8=FIX.4.29=22135=834=9249=MRKTC-EXCH52=20100815-22:27:53.21956=210l-colin-a6=100011=1000814=1017=1201320=031=100032=1037=1201038=100039=440=241=1000644=10054=155=MSFT59=060=20100815-22:27:53.219150=4151=0167=CS10=001}
 
 Header
 BeginString [8R] = FIX.4.2
 BodyLength [9R] = 167
 MsgSeqNum [34R] = 11
 MsgType [35R] = NewOrderSingle [D]
 SenderCompID [49R] = colin
 SendingTime [52R] = 20100820-20:40:54.965
 TargetCompID [56R] = MRKTC-EXCH
Body
 ClOrdID [11R] = 1282336603527
 HandlInst [21R] = AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION [1]
 OrderQty [38] = 100
 OrdType [40R] = LIMIT [2]
 Price [44] = 100
 Side [54R] = BUY [1]
 Symbol [55R] = ES-201012
 TimeInForce [59] = DAY [0]
 TransactTime [60R] = 20100820-20:40:54.880
 SecurityType [167] = FUTURE [FUT]
 MaturityMonthYear [200] = 201012
Trailer
 CheckSum [10R] = 102

 */

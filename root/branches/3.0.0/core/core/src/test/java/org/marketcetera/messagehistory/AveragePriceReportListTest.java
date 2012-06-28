package org.marketcetera.messagehistory;

import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.position.impl.ExpectedListChanges;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;

import quickfix.Message;
import quickfix.field.*;
import quickfix.field.Side;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

/* $License$ */

/**
 * Test {@link AveragePriceReportListTest}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: AveragePriceReportListTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class AveragePriceReportListTest extends FIXVersionedTestCase {
    public AveragePriceReportListTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(AveragePriceReportListTest.class,
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
    }

    public void testInsertExecutionReport() throws Exception {
        EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(10), null,
                new BigDecimal(10), new BigDecimal(11), new BigDecimal(10),
                new BigDecimal(11), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(1, averagePriceList.size());
        Message avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(10.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
        assertEquals(11.0, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);

        Message message2 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.NEW, Side.BUY,
                new BigDecimal(10), null, new BigDecimal(110), new BigDecimal(
                        111), new BigDecimal(110), new BigDecimal(111),
                new Equity("IBM"), "account", "text");
        message2.setField(new LeavesQty(190.0));
        source.add(new ReportHolder(createReport(message2), "IBM"));

        assertEquals(1, averagePriceList.size());
        avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(120.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
        assertEquals(102.66666, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);

        Message message3 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PENDING_NEW, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new Equity("IBM"),
                "account", "text");
        message3.setField(new OrderQty(1000));
        source.add(new ReportHolder(createReport(message3), "IBM"));

        assertEquals(1, averagePriceList.size());
        avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(120.0, avgPriceMessage.getDouble(CumQty.FIELD), .0001);
        assertEquals(102.66666, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);
        assertEquals(1000.0, avgPriceMessage.getDouble(OrderQty.FIELD), .0001);

    }

    public void testAddOrderFirst() throws Exception {
        EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.PENDING_NEW, Side.BUY, new BigDecimal(
                        1000), null, new BigDecimal(0), null, new BigDecimal(
                        100), new BigDecimal(3), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(0));
        message.setField(new ExecTransType(ExecTransType.NEW));
        message.setField(new ExecType(ExecType.NEW));
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(1, averagePriceList.size());
        Message avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(1000.0, avgPriceMessage.getDouble(OrderQty.FIELD), .0001);

    }

    public void testRemove() throws Exception {
        final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(0), null, new BigDecimal(10), new BigDecimal(11), new BigDecimal(
                        10), new BigDecimal(11), new Equity("IBM"), "account", "text");
        Message message2 = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(0), null, new BigDecimal(10), new BigDecimal(11), new BigDecimal(
                        10), new BigDecimal(11), new Equity("MSFT"), "account", "text");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message), "IBM"));
        source.add(new ReportHolder(createReport(message2), "MSFT"));

        assertEquals(2, averagePriceList.size());

        // any remove empties the list (since TradeReportsHistory only supports clear())
        source.addListEventListener(new ExpectedListChanges<ReportHolder>("avg", new int[] {
                ListEvent.DELETE, 0, ListEvent.DELETE, 0 }));
        source.remove(0);
        assertEquals(0, averagePriceList.size());
    }

    public void testUpdate() throws Exception {
        final EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
        AveragePriceReportList averagePriceList = new AveragePriceReportList(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
        final Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(0), null, new BigDecimal(10), new BigDecimal(11), new BigDecimal(
                        10), new BigDecimal(11), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message), "IBM"));

        assertEquals(1, averagePriceList.size());

        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                source.set(0, new ReportHolder(createReport(message), "IBM"));
            }
        };
    }

    public void testInstrument() throws Exception {
        if (fixVersion != FIXVersion.FIX40) {
            EventList<ReportHolder> source = new BasicEventList<ReportHolder>();
            AveragePriceReportList averagePriceList = new AveragePriceReportList(
                    FIXVersion.FIX_SYSTEM.getMessageFactory(), source);
            Option option = new Option("IBM", "200912", BigDecimal.ONE,
                    OptionType.Put);
            Message message = msgFactory.newExecutionReport("clordid1",
                    "clordid1", "execido1", OrdStatus.PENDING_NEW, Side.BUY,
                    new BigDecimal(1000), null, new BigDecimal(0), null,
                    new BigDecimal(100), new BigDecimal(3), option, "account", "text");
            message.setField(new LeavesQty(0));
            message.setField(new ExecTransType(ExecTransType.NEW));
            message.setField(new ExecType(ExecType.NEW));
            source.add(new ReportHolder(createReport(message), "IBM"));
            assertEquals(1, averagePriceList.size());
            ReportHolder holder = averagePriceList.get(0);
            ExecutionReport avgPriceReport = (ExecutionReport) holder
                    .getReport();
            assertThat(avgPriceReport.getOrderQuantity(), comparesEqualTo(1000));
            assertEquals(option, avgPriceReport.getInstrument());
            assertEquals("IBM", holder.getUnderlying());
        }
    }

    private ExecutionReport createReport(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
                new BrokerID("null"), Originator.Server, null, null);
    }
}

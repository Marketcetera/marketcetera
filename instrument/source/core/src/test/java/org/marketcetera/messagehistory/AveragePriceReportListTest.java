package org.marketcetera.messagehistory;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.position.impl.ExpectedListChanges;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Originator;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

/* $License$ */

/**
 * Test {@link AveragePriceReportListTest}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
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
                new BigDecimal(11), new MSymbol("IBM"), "account");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message)));

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
                new MSymbol("IBM"), "account");
        message2.setField(new LeavesQty(190.0));
        source.add(new ReportHolder(createReport(message2)));

        assertEquals(1, averagePriceList.size());
        avgPriceMessage = averagePriceList.get(0).getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader()
                .getString(MsgType.FIELD));
        assertEquals(120.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
        assertEquals(102.66666, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);

        Message message3 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PENDING_NEW, Side.BUY,
                new BigDecimal(1000), null, new BigDecimal(0), null,
                new BigDecimal(100), new BigDecimal(3), new MSymbol("IBM"),
                "account");
        message3.setField(new OrderQty(1000));
        source.add(new ReportHolder(createReport(message3)));

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
                        100), new BigDecimal(3), new MSymbol("IBM"), "account");
        message.setField(new LeavesQty(0));
        message.setField(new ExecTransType(ExecTransType.NEW));
        message.setField(new ExecType(ExecType.NEW));
        source.add(new ReportHolder(createReport(message)));

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
                        10), new BigDecimal(11), new MSymbol("IBM"), "account");
        Message message2 = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(0), null, new BigDecimal(10), new BigDecimal(11), new BigDecimal(
                        10), new BigDecimal(11), new MSymbol("MSFT"), "account");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message)));
        source.add(new ReportHolder(createReport(message2)));

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
                        10), new BigDecimal(11), new MSymbol("IBM"), "account");
        message.setField(new LeavesQty(90.0));
        source.add(new ReportHolder(createReport(message)));

        assertEquals(1, averagePriceList.size());

        new ExpectedTestFailure(UnsupportedOperationException.class) {
            @Override
            protected void execute() throws Throwable {
                source.set(0, new ReportHolder(createReport(message)));
            }
        }.run();
    }

    private ExecutionReport createReport(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
                new BrokerID("null"), Originator.Server, null, null);
    }
}

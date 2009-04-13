package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.OrderID;
import org.marketcetera.persist.PersistTestBase;
import org.marketcetera.event.HasFIXMessage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import org.apache.log4j.Level;

import javax.persistence.TemporalType;
import java.util.List;
import java.util.Date;
import java.util.concurrent.Callable;
import java.math.BigDecimal;

import quickfix.field.*;

/* $License$ */
/**
 * Verifies {@link ExecutionReportSummary}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ExecReportSummaryTest extends ReportsTestBase {
    /**
     * Verify empty actor/viewer.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void emptyViewer()
        throws Exception
    {
        PersistentReport.save
            (createExecReport
             ("o1",null,"sym",Side.Buy,OrderStatus.PartiallyFilled,
              BigDecimal.TEN,BigDecimal.TEN,BigDecimal.ONE,BigDecimal.ONE,
              BROKER,null,null));
        PersistentReport.save
            (createExecReport
             ("o1",null,"sym",Side.Buy,OrderStatus.PartiallyFilled,
              BigDecimal.TEN,BigDecimal.TEN,BigDecimal.ONE,BigDecimal.ONE));

        MultiExecReportSummary query=MultiExecReportSummary.all();
        query.setEntityOrder(MultiExecReportSummary.BY_ID);
        List<ExecutionReportSummary> summary=query.fetch();
        assertEquals(2,summary.size());

        assertNull(summary.get(0).getViewer());
        assertNull(summary.get(0).getViewerID());
        assertEquals(sViewer.getName(),summary.get(1).getViewer().getName());
        assertEquals(sViewerID,summary.get(1).getViewerID());
    }

    /**
     * Verifies no summaries are saved for cancel rejects.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void rejectNotSaved() throws Exception {
        OrderCancelReject reject = createCancelReject();
        PersistentReport.save(reject);
        //report got saved
        assertEquals(1, MultiPersistentReportQuery.all().fetchCount());
        //but the summary didn't
        MultiExecReportSummary query = MultiExecReportSummary.all();
        assertEquals(0, query.fetchCount());
        assertEquals(0, query.fetch().size());
    }

    /**
     * Verifies that summary is saved for an execution report and that it's
     * saved with expected values for all attributes.
     * <p>
     * Also verifies that the root ID is correctly fetched an assigned
     * when a chain of execution reports are received.
     * <p>
     * Also verifies that sorting the results by ID works.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void execReportSave() throws Exception {
        String orderID1 = "ord1";
        String symbol = "sym";
        // A report with null orig ID. The root ID should be set to orderID1
        ExecutionReport report1 = createExecReport(orderID1, null,
                symbol, Side.Buy, OrderStatus.PartiallyFilled,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE);
        PersistentReport.save(report1);
        //report got saved
        MultiPersistentReportQuery reportQuery = MultiPersistentReportQuery.all();
        assertEquals(1, reportQuery.fetchCount());
        //and so did the summary
        MultiExecReportSummary query = MultiExecReportSummary.all();
        query.setEntityOrder(MultiExecReportSummary.BY_ID);

        assertEquals(1, query.fetchCount());
        List<ExecutionReportSummary> summary = query.fetch();
        assertEquals(1, summary.size());
        assertSummary(summary.get(0), report1.getAveragePrice(),
                report1.getCumulativeQuantity(), report1.getLastPrice(),
                report1.getLastQuantity(), report1.getOrderID(),
                report1.getOrderStatus(), null, reportQuery.fetch().get(0),
                report1.getOrderID(), report1.getSendingTime(),
                report1.getSide(), report1.getSymbol().getFullSymbol());

        String orderID2 = "ord2";
        // A report with orig ID set to previous order. The root ID should be set to orderID1
        ExecutionReport report2 = createExecReport(orderID2, orderID1,
                symbol, Side.Buy, OrderStatus.PartiallyFilled,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE);
        PersistentReport.save(report2);
        //report got saved
        assertEquals(2, reportQuery.fetchCount());
        //and so did the summary
        assertEquals(2, query.fetchCount());
        summary = query.fetch();
        assertEquals(2, summary.size());
        assertSummary(summary.get(1), report2.getAveragePrice(),
                report2.getCumulativeQuantity(), report2.getLastPrice(),
                report2.getLastQuantity(), report2.getOrderID(),
                report2.getOrderStatus(), report2.getOriginalOrderID(),
                reportQuery.fetch().get(1),
                report1.getOrderID(), report2.getSendingTime(),
                report2.getSide(), report2.getSymbol().getFullSymbol());

        String orderID3 = "ord3";
        // A report with orig ID set to previous order. The root ID should be set to orderID1
        ExecutionReport report3 = createExecReport(orderID3, orderID2,
                symbol, Side.Buy, OrderStatus.PartiallyFilled,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE);
        PersistentReport.save(report3);
        //report3 got saved
        assertEquals(3, reportQuery.fetchCount());
        //and so did the summary
        assertEquals(3, query.fetchCount());
        summary = query.fetch();
        assertEquals(3, summary.size());
        assertSummary(summary.get(2), report3.getAveragePrice(),
                report3.getCumulativeQuantity(), report3.getLastPrice(),
                report3.getLastQuantity(), report3.getOrderID(),
                report3.getOrderStatus(), report3.getOriginalOrderID(),
                reportQuery.fetch().get(2),
                report1.getOrderID(), report3.getSendingTime(),
                report3.getSide(), report3.getSymbol().getFullSymbol());
    }

    /**
     * Verifies behavior when the report has original order ID value
     * set but no order corresponding to that could be found in the system.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void execReportOrigOrderNotPresent() throws Exception {
        //Create a report with orig orderID value such that no
        //record of an exec report with that order ID value exists
        ExecutionReport report = createExecReport("ord1", "ord2",
                "s", Side.Buy, OrderStatus.PartiallyFilled,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE);
        PersistentReport.save(report);
        //report got saved
        MultiPersistentReportQuery reportQuery = MultiPersistentReportQuery.all();
        assertEquals(1, reportQuery.fetchCount());
        //and so did the summary
        MultiExecReportSummary query = MultiExecReportSummary.all();

        assertEquals(1, query.fetchCount());
        List<ExecutionReportSummary> summary = query.fetch();
        assertEquals(1, summary.size());
        assertSummary(summary.get(0), report.getAveragePrice(),
                report.getCumulativeQuantity(), report.getLastPrice(),
                report.getLastQuantity(), report.getOrderID(),
                report.getOrderStatus(), report.getOriginalOrderID(),
                reportQuery.fetch().get(0),
                report.getOriginalOrderID(), report.getSendingTime(),
                report.getSide(), report.getSymbol().getFullSymbol());
    }
    
    @Test
    public void nullAvgPriceFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                AvgPx.FIELD);
        assertNull(report.getAveragePrice());
        nonNullCVCheck("avgPrice", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullCumQtyFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                CumQty.FIELD);
        assertNull(report.getCumulativeQuantity());
        nonNullCVCheck("cumQuantity", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullOrderIDFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                ClOrdID.FIELD);
        assertNull(report.getOrderID());
        nonNullCVCheck("orderID", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullOrderStatusFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                OrdStatus.FIELD);
        assertNull(report.getOrderStatus());
        nonNullCVCheck("orderStatus", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullSendingTimeFail() throws Exception {
        final ExecutionReport report = (ExecutionReport) removeSendingTime(
                createDummyExecReport());
        nonNullCVCheck("sendingTime", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullSideFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                quickfix.field.Side.FIELD);
        assertNull(report.getSide());
        nonNullCVCheck("side", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    @Test
    public void nullSymbolFail() throws Exception {
        final ExecutionReport report = removeField(createDummyExecReport(),
                Symbol.FIELD);
        assertNull(report.getSymbol());
        nonNullCVCheck("symbol", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(report);
                return null;
            }
        });
    }

    /**
     * Verifies that the query has expected default values.
     */
    @Test
    public void queryDefaults() {
        MultiExecReportSummary query = MultiExecReportSummary.all();
        assertEquals(null, query.getEntityOrder());
        assertEquals(-1, query.getFirstResult());
        assertEquals(-1, query.getMaxResult());
    }

    private static ExecutionReport removeField(ExecutionReport inReport,
                                               int inTag) {
        ((HasFIXMessage)inReport).getMessage().removeField(inTag);
        return inReport;
    }

    private ExecutionReport createDummyExecReport() throws Exception {
        return createExecReport("o1", null, "s", Side.Buy,
                OrderStatus.Filled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
    }
    
    private static void assertSummary(ExecutionReportSummary inSummary,
                                      BigDecimal inAvgPrice,
                                      BigDecimal inCumQuantity,
                                      BigDecimal inLastPrice,
                                      BigDecimal inLastQuantity,
                                      OrderID inOrderID,
                                      OrderStatus inOrderStatus,
                                      OrderID inOrigOrderID,
                                      PersistentReport inReport,
                                      OrderID inRootID,
                                      Date inSendingTime,
                                      Side inSide,
                                      String inSymbol) throws Exception {
        assertBigDecimalEquals(inAvgPrice, inSummary.getAvgPrice());
        assertBigDecimalEquals(inCumQuantity, inSummary.getCumQuantity());
        assertBigDecimalEquals(inLastPrice, inSummary.getLastPrice());
        assertBigDecimalEquals(inLastQuantity, inSummary.getLastQuantity());
        assertEquals(inOrderID, inSummary.getOrderID());
        assertEquals(inOrderStatus, inSummary.getOrderStatus());
        assertEquals(inOrigOrderID, inSummary.getOrigOrderID());
        assertReportEquals(inReport.toReport(),
                inSummary.getReport().toReport());
        assertEquals(inReport.getViewerID(),inSummary.getViewerID());
        assertEquals(inRootID, inSummary.getRootID());
        PersistTestBase.assertCalendarEquals(inSendingTime,
                inSummary.getSendingTime(), TemporalType.TIMESTAMP);
        assertEquals(inSide, inSummary.getSide());
        assertEquals(inSymbol, inSummary.getSymbol());
    }

}

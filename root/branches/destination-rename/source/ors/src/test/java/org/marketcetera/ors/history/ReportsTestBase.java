package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.persist.PersistTestBase;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.trade.*;
import org.marketcetera.core.MSymbol;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.event.HasFIXMessage;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Callable;
import java.io.File;

/* $License$ */
/**
 * ReportsTestBase
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportsTestBase extends TestCaseBase {
    @BeforeClass
    public static void springSetup()
        throws Exception {
        PersistTestBase.springSetup(getSpringFiles());
        sMessageFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
        sServices = new ReportHistoryServices();
    }
    @Before
    @After
    public void cleanTables() throws Exception {
        MultiExecReportSummary erQuery = MultiExecReportSummary.all();
        MultiPersistentReportQuery prQuery = MultiPersistentReportQuery.all();
        erQuery.delete();
        prQuery.delete();
        //Verify everything's gone
        assertEquals(0, erQuery.fetchCount());
        assertEquals(0, erQuery.fetch().size());
        assertEquals(0, prQuery.fetchCount());
        assertEquals(0, prQuery.fetch().size());
    }

    static OrderCancelReject createCancelReject()
            throws MessageCreationException {
        return createCancelReject(BROKER);
    }
    static OrderCancelReject createCancelReject(BrokerID inBrokerID)
            throws MessageCreationException {
        Message msg = sMessageFactory.newOrderCancelReject();
        msg.setField(new ClOrdID("rejord1"));
        msg.setField(new OrigClOrdID("rejorigord1"));
        setSendingTime(msg);
        return Factory.getInstance().createOrderCancelReject(msg, inBrokerID);
    }

    static ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            String inSymbol, Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice)
            throws Exception {
        return createExecReport(inOrderID, inOrigOrderID, inSymbol,
                inSide, inOrderStatus, inCumQuantity, inAvgPrice,
                inLastQty, inLastPrice, BROKER);
    }
    static ExecutionReport createExecReport(String inOrderID,
                                            String inOrigOrderID,
                                            String inSymbol, Side inSide,
                                            OrderStatus inOrderStatus,
                                            BigDecimal inCumQuantity,
                                            BigDecimal inAvgPrice,
                                            BigDecimal inLastQty,
                                            BigDecimal inLastPrice,
                                            BrokerID inBrokerID)
            throws Exception {
        Message msg = sMessageFactory.newExecutionReport("ord1", inOrderID,
                "exec1", inOrderStatus.getFIXValue(), inSide.getFIXValue(),
                new BigDecimal("23.234"), new BigDecimal("343.343"),
                inLastQty, inLastPrice, inCumQuantity, inAvgPrice,
                new MSymbol(inSymbol), "account");
        if (inOrigOrderID != null) {
            msg.setField(new OrigClOrdID(inOrigOrderID));
        }
        setSendingTime(msg);
        return Factory.getInstance().createExecutionReport(
                msg, inBrokerID, Originator.Server);
    }

    private static void setSendingTime(Message inMsg) {
        inMsg.getHeader().setField(new SendingTime(new Date()));
    }

    static String[] getSpringFiles() {
        return new String[] {
            "file:"+DIR_ROOT+ File.separator+ //$NON-NLS-1$
            "conf"+File.separator+ //$NON-NLS-1$
            "persist_tests.xml"}; //$NON-NLS-1$
    }

    static void assertReportEquals(ReportBase inReport1,
                                   ReportBase inReport2) {
        if (inReport1 instanceof ExecutionReport) {
            TypesTestBase.assertExecReportEquals((ExecutionReport) inReport1,
                    (ExecutionReport)inReport2);
        } else {
            TypesTestBase.assertCancelRejectEquals(
                    (OrderCancelReject) inReport1,
                    (OrderCancelReject) inReport2);
        }
        TypesTestBase.assertFIXEquals(inReport1, inReport2);
        assertEquals(inReport1.getReportID(), inReport2.getReportID());

    }

    /**
     * Tests a not null ConstraintViolation Failure.
     *
     * @param inFieldName the field that is expected to have non-null
     * constraint violation failure.
     *
     * @param inTest the code snippet that will fail
     *
     * @throws Exception if there's an error.
     */
    protected static void nonNullCVCheck(String inFieldName,
                                         final Callable<?> inTest)
            throws Exception {
        String exceptMsg = new ExpectedFailure<PersistenceException>(null) {
            protected void run() throws Exception {
                inTest.call();
            }
        }.getException().getLocalizedDetail();
        assertTrue(exceptMsg, exceptMsg.contains(inFieldName));
    }

    protected static ReportBase removeSendingTime(ReportBase inReport) {
        HasFIXMessage fix = (HasFIXMessage) inReport;
        fix.getMessage().getHeader().removeField(SendingTime.FIELD);
        assertEquals(null, inReport.getSendingTime());
        return inReport;
    }

    static void assertBigDecimalEquals(BigDecimal inValue1, BigDecimal inValue2) {
        if(inValue1 == null ^ inValue2 == null) {
            org.junit.Assert.fail(getFailString(inValue1, inValue2));
        }
        if(inValue1 == null) {
            return;
        }
        org.junit.Assert.assertTrue(getFailString(inValue1, inValue2),
                inValue1.compareTo(inValue2) == 0);
    }

    private static String getFailString(Object inValue1, Object inValue2) {
        return new StringBuilder().append("expected<").append(inValue1).
                append(">, actual<").append(inValue2).append(">").toString();
    }

    /**
     * Sleeps for the minimal time that the db can differentiate between.
     *
     * @throws InterruptedException if interrupted.
     */
    protected static void sleepForSignificantTime() throws InterruptedException {
        PersistTestBase.sleepForSignificantTime();
    }

    protected static BigDecimal getPosition(Date inDate, String inSymbol)
            throws Exception {
        return sServices.getPositionAsOf(inDate, new MSymbol(inSymbol));
    }

    protected static ExecutionReport createAndSaveER(String inOrderID,
                                            String inOrigOrderID,
                                            String inSymbol, Side inSide,
                                            BigDecimal inCumQty) throws Exception {
        sleepForSignificantTime();
        ExecutionReport report = createExecReport(inOrderID, inOrigOrderID,
                inSymbol, inSide, OrderStatus.PartiallyFilled, inCumQty,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
        sServices.save(report);
        sleepForSignificantTime();
        return report;
    }

    private static final BrokerID BROKER = new BrokerID("TestBroker");
    private static FIXMessageFactory sMessageFactory;
    protected static ReportHistoryServices sServices;
}

package org.marketcetera.ors.history;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.marketcetera.core.instruments.MockUnderlyingSymbolSupport;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.messagehistory.TradeReportsHistoryTest;
import org.marketcetera.ors.PersistTestBase;
import org.marketcetera.ors.Principals;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;

import quickfix.Message;

/* $License$ */
/**
 * Verifies {@link PersistentReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Parameterized.class)
public class PersistentReportTest
        extends PersistTestBase
{
    /**
     * The test parameters that this test iterates through.
     *
     * @return the test parameters.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{new Equity("sym")},
                new Object[]{new Option("sym", "20101010", BigDecimal.TEN, OptionType.Put)},
                new Object[]{new Currency("USD", "CAD", "", "")}
        );
    }
    /**
     * Creates an instance.
     *
     * @param inInstrument the instrument for this test.
     */
    public PersistentReportTest(Instrument inInstrument) {
        mInstrument = inInstrument;
    }

    /**
     * Verify empty broker/actor/viewer.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void emptyBrokerActorViewer()
        throws Exception
    {
        PersistentReport report=new PersistentReport(createCancelReject(null,
                                                                        null,
                                                                        null),
                                                     null,
                                                     null);
        assertNull(report.getBrokerID());
        assertNull(report.getActor());
        assertNull(report.getActorID());
        assertNull(report.getViewer());
        assertNull(report.getViewerID());

        report=new PersistentReport(createCancelReject(BROKER,
                                                       actorID,
                                                       null),
                                    actor,
                                    null);
        assertEquals(BROKER,report.getBrokerID());
        assertEquals(actor.getId(),report.getActor().getId());
        assertEquals(actorID,report.getActorID());
        assertNull(report.getViewer());
        assertNull(report.getViewerID());

        report=new PersistentReport(createCancelReject(),
                                    actor,
                                    viewer);
        assertEquals(BROKER,report.getBrokerID());
        assertEquals(actor.getId(),report.getActor().getId());
        assertEquals(actorID,report.getActorID());
        assertEquals(viewer.getId(),report.getViewer().getId());
        assertEquals(viewerID,report.getViewerID());
    }

    /*
     * Verify that invalid checksum message is processed.
     */
    
    @Test
    public void testInvalidChecksum() throws Exception {    	
    	String messageString = "8=FIX.4.29=14135=86=011=1171508063701-server02/127.0.0.114=017=ZZ-INTERNAL20=\u000031=032=038=1039=044=1054=155=R58=INVALID_CHECKSUM_TEST60=20070215-02:54:27150=0151=1010=7";
    	TradeReportsHistory history = new TradeReportsHistory(FIXVersion.FIX_SYSTEM.getMessageFactory(), new MockUnderlyingSymbolSupport());
        Message aMessage = new Message(messageString,false);
        aMessage.getTrailer().setInt(10, 1);
        history.addIncomingMessage(TradeReportsHistoryTest.createServerReport(aMessage));
        ExecutionReport execReport = history.getLatestExecutionReport(new OrderID("1171508063701-server02/127.0.0.1"));
        PersistentReport pers = new PersistentReport(execReport,
                                                     actor,
                                                     viewer);
        ReportBase reportBase = pers.toReport();
        assertEquals("INVALID_CHECKSUM_TEST",reportBase.getText());
    }
 
    /**
     * Verify that the cancel reject report is saved and retrieved correctly.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void rejectSaveAndRetrieve() throws Exception {
        //Create order cancel reject, save and retrieve it.
        OrderCancelReject reject = createCancelReject();
        assertNull(reject.getReportID());
        reportHistoryServices.save(reject);
        assertNotNull(reject.getReportID());
        List<PersistentReport> reports = findAllPersistentReport();
        assertEquals(1, reports.size());
        OrderCancelReject retrieved = (OrderCancelReject) reports.get(0).toReport();
        assertReportEquals(reject,  retrieved);
        //Principals.
        assertSame(Principals.UNKNOWN,
                   reportService.getPrincipals(new OrderID("nonexistent")));
        Principals p = reportService.getPrincipals(reject.getOrderID());
        assertEquals(actorID,p.getActorID());
        assertEquals(viewerID,p.getViewerID());
    }

    /**
     * Verify that execution report is saved and retrieved correctly.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void execReportSaveAndRetrieve() throws Exception {
        //Create exec report, save and retrieve it.
        ExecutionReport report = createExecReport("o1", null, getInstrument(),
                Side.Buy, OrderStatus.New, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        assertNull(report.getReportID());
        reportHistoryServices.save(report);
        assertNotNull(report.getReportID());
        List<PersistentReport> reports = findAllPersistentReport();
        assertEquals(1, reports.size());
        ExecutionReport retrieved = (ExecutionReport) reports.get(0).toReport();
        assertReportEquals(report,  retrieved);

        //Principals.
        Principals p = reportService.getPrincipals(report.getOrderID());
        assertEquals(actorID,p.getActorID());
        assertEquals(viewerID,p.getViewerID());
    }

    /**
     * Verifies that we get a db constraint failure if sendingTime is
     * null.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void nullSendingTimeFailure() throws Exception {
        //null sending time in cancel reject
        nonNullCVCheck("sendingTime", new Callable<Object>(){
            public Object call() throws Exception {
                reportHistoryServices.save(removeSendingTime(
                        createCancelReject()));
                return null;
            }
        });
        //null sending time in exec report
        nonNullCVCheck("sendingTime", new Callable<Object>(){
            public Object call() throws Exception {
                reportHistoryServices.save(removeSendingTime(createExecReport("o1",
                        null, getInstrument(), Side.Buy, OrderStatus.DoneForDay,
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                        BigDecimal.ONE)));
                return null;
            }
        });
        List<PersistentReport> reports = findAllPersistentReport();
        //Verify we've got nothing persisted
        assertEquals(0, reports.size());
    }

    /**
     * Verifies that the ReportID assigned to the reports are always increasing
     * sequentially.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void verifyIDSequential() throws Exception {
        //Create multiple reports
        OrderCancelReject reject1 = createCancelReject();
        ExecutionReport report2 = createExecReport("o1",null, getInstrument(), Side.Sell,
                OrderStatus.Filled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        ExecutionReport report3 = createExecReport("o2",null, getInstrument(), Side.Buy,
                OrderStatus.PartiallyFilled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        OrderCancelReject reject4 = createCancelReject();
        //Save 'em
        reportHistoryServices.save(reject1);
        reportHistoryServices.save(report2);
        reportHistoryServices.save(report3);
        reportHistoryServices.save(reject4);
        //Verify that their IDs are assigned in ascending order
        assertTrue(reject4.getReportID().compareTo(report3.getReportID()) > 0);
        assertTrue(report3.getReportID().compareTo(report2.getReportID()) > 0);
        assertTrue(report2.getReportID().compareTo(reject1.getReportID()) > 0);
        //Retrieve them and verify that they're retrieved in order
        List<PersistentReport> reports = findAllPersistentReport();
        assertEquals(4, reports.size());
        assertRetrievedReports(reports, reject1, report2,
                report3, reject4);
    }

    /**
     * Tests sending time
     * {@link MultiPersistentReportQuery#getSendingTimeAfterFilter() filter}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void sendingTimeFiltering() throws Exception{
        Date time1 = new Date();
        sleepForSignificantTime();
        //Create multiple reports with dbTimePrecision between each events.
        OrderCancelReject reject1 = createCancelReject();
        sleepForSignificantTime();
        Date time2 = new Date();
        sleepForSignificantTime();
        ExecutionReport report2 = createExecReport("o1",null, getInstrument(), Side.Sell,
                OrderStatus.Filled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        sleepForSignificantTime();
        Date time3 = new Date();
        sleepForSignificantTime();
        ExecutionReport report3 = createExecReport("o2",null, getInstrument(), Side.Buy,
                OrderStatus.PartiallyFilled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        sleepForSignificantTime();
        Date time4 = new Date();
        sleepForSignificantTime();
        OrderCancelReject reject4 = createCancelReject();
        sleepForSignificantTime();
        Date time5 = new Date();
        //Save 'em
        reportHistoryServices.save(reject1);
        reportHistoryServices.save(report2);
        reportHistoryServices.save(report3);
        reportHistoryServices.save(reject4);
        //Retrieve them and verify that they're retrieved in order
        List<PersistentReport> reports = findAllPersistentReportSince(time1);
        //Retrieve all reports
        assertEquals(4, reports.size());
        assertRetrievedReports(reports, reject1, report2,
                report3, reject4);

        //Retrieve last 3 reports
        reports = findAllPersistentReportSince(time2);
        assertEquals(3, reports.size());
        assertRetrievedReports(reports, report2, report3, reject4);

        //Retrieve last 2 reports
        reports = findAllPersistentReportSince(time3);
        assertEquals(2, reports.size());
        assertRetrievedReports(reports, report3, reject4);

        //Retrieve last 1 reports
        reports = findAllPersistentReportSince(time4);
        assertEquals(1, reports.size());
        assertRetrievedReports(reports, reject4);

        //Retrieve no reports
        reports = findAllPersistentReportSince(time5);
        assertEquals(0, reports.size());
        assertRetrievedReports(reports);
    }
    /**
     * Tests viewer
     * {@link MultiPersistentReportQuery#getViewerFilter() filter}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void viewerFiltering()
        throws Exception
    {
        OrderCancelReject r1=
            createCancelReject();
        reportHistoryServices.save(r1);
        OrderCancelReject r2=
            createCancelReject(BROKER,actorID,null);
        reportHistoryServices.save(r2);
        OrderCancelReject r3=
            createCancelReject(BROKER,actorID,extraUserID);
        reportHistoryServices.save(r3);

        List<PersistentReport> reports = findAllPersistentReport();
        assertRetrievedReports(reports,r1,r2,r3);
        reports = findAllPersistentReportByViewer(viewer);
        assertRetrievedReports(reports,r1);
        reports = findAllPersistentReportByViewer(extraUser);
        assertRetrievedReports(reports,r3);
        reports = findAllPersistentReportByViewer(actor);
        assertRetrievedReports(reports);
    }
    @Test
    public void delete() {
        //Doesn't need to be tested as it's only needed for unit testing
        //execution reports are never deleted in production.
    }
    
    private void assertRetrievedReports(List<PersistentReport> inList,
                                        ReportBase... inReports)
            throws Exception {
        assertEquals(inReports.length, inList.size());
        int idx = 0;
        for(PersistentReport report: inList) {
            assertReportEquals(inReports[idx++], report.toReport());
        }
    }

    private Instrument getInstrument() {
        return mInstrument;
    }
    private final Instrument mInstrument;
}

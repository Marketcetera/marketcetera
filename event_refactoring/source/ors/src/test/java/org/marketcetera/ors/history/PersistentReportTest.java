package org.marketcetera.ors.history;

import org.marketcetera.ors.Principals;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.event.HasFIXMessage;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import quickfix.field.SendingTime;

/* $License$ */
/**
 * Verifies {@link PersistentReport}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class PersistentReportTest extends ReportsTestBase {
    /**
     * Verify empty broker/actor/viewer.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void emptyBrokerActorViewer()
        throws Exception
    {
        PersistentReport report=new PersistentReport
            (createCancelReject(null,null,null));
        assertNull(report.getBrokerID());
        assertNull(report.getActor());
        assertNull(report.getActorID());
        assertNull(report.getViewer());
        assertNull(report.getViewerID());

        report=new PersistentReport
            (createCancelReject(BROKER,sActorID,null));
        assertEquals(BROKER,report.getBrokerID());
        assertEquals(sActor.getId(),report.getActor().getId());
        assertEquals(sActorID,report.getActorID());
        assertNull(report.getViewer());
        assertNull(report.getViewerID());

        report=new PersistentReport
            (createCancelReject());
        assertEquals(BROKER,report.getBrokerID());
        assertEquals(sActor.getId(),report.getActor().getId());
        assertEquals(sActorID,report.getActorID());
        assertEquals(sViewer.getId(),report.getViewer().getId());
        assertEquals(sViewerID,report.getViewerID());
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
        PersistentReport.save(reject);
        assertNotNull(reject.getReportID());
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        assertEquals(1, query.fetchCount());
        List<PersistentReport> reports = query.fetch();
        assertEquals(1, reports.size());
        OrderCancelReject retrieved = (OrderCancelReject) reports.get(0).toReport();
        assertReportEquals(reject,  retrieved);

        //Principals.
        assertSame(Principals.UNKNOWN,
                   PersistentReport.getPrincipals(new OrderID("nonexistent")));
        Principals p=PersistentReport.getPrincipals(reject.getOrderID());
        assertEquals(sActorID,p.getActorID());
        assertEquals(sViewerID,p.getViewerID());
    }

    /**
     * Verify that execution report is saved and retrieved correctly.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void execReportSaveAndRetrieve() throws Exception {
        //Create exec report, save and retrieve it.
        ExecutionReport report = createExecReport("o1", null, "blue", Side.Buy,
                OrderStatus.New, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        assertNull(report.getReportID());
        PersistentReport.save(report);
        assertNotNull(report.getReportID());
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        assertEquals(1, query.fetchCount());
        List<PersistentReport> reports = query.fetch();
        assertEquals(1, reports.size());
        ExecutionReport retrieved = (ExecutionReport) reports.get(0).toReport();
        assertReportEquals(report,  retrieved);

        //Principals.
        Principals p=PersistentReport.getPrincipals(report.getOrderID());
        assertEquals(sActorID,p.getActorID());
        assertEquals(sViewerID,p.getViewerID());
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
                PersistentReport.save(removeSendingTime(
                        createCancelReject()));
                return null;
            }
        });
        //null sending time in exec report
        nonNullCVCheck("sendingTime", new Callable<Object>(){
            public Object call() throws Exception {
                PersistentReport.save(removeSendingTime(createExecReport("o1",
                        null, "i", Side.Buy, OrderStatus.DoneForDay,
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                        BigDecimal.ONE)));
                return null;
            }
        });
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        //Verify we've got nothing persisted
        assertEquals(0, query.fetchCount());
        assertEquals(0, query.fetch().size());
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
        ExecutionReport report2 = createExecReport("o1",null, "s", Side.Sell,
                OrderStatus.Filled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        ExecutionReport report3 = createExecReport("o2",null, "s", Side.Buy, 
                OrderStatus.PartiallyFilled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        OrderCancelReject reject4 = createCancelReject();
        //Save 'em
        PersistentReport.save(reject1);
        PersistentReport.save(report2);
        PersistentReport.save(report3);
        PersistentReport.save(reject4);
        //Verify that their IDs are assigned in ascending order
        assertTrue(reject4.getReportID().compareTo(report3.getReportID()) > 0);
        assertTrue(report3.getReportID().compareTo(report2.getReportID()) > 0);
        assertTrue(report2.getReportID().compareTo(reject1.getReportID()) > 0);
        //Retrieve them and verify that they're retrieved in order
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        assertEquals(4, query.fetchCount());
        assertRetrievedReports(query.fetch(), reject1, report2,
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
        ExecutionReport report2 = createExecReport("o1",null, "s", Side.Sell,
                OrderStatus.Filled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        sleepForSignificantTime();
        Date time3 = new Date();
        sleepForSignificantTime();
        ExecutionReport report3 = createExecReport("o2",null, "s", Side.Buy,
                OrderStatus.PartiallyFilled, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        sleepForSignificantTime();
        Date time4 = new Date();
        sleepForSignificantTime();
        OrderCancelReject reject4 = createCancelReject();
        sleepForSignificantTime();
        Date time5 = new Date();
        //Save 'em
        PersistentReport.save(reject1);
        PersistentReport.save(report2);
        PersistentReport.save(report3);
        PersistentReport.save(reject4);
        //Retrieve them and verify that they're retrieved in order
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        //Retrieve all reports
        query.setSendingTimeAfterFilter(time1);
        assertEquals(4, query.fetchCount());
        assertRetrievedReports(query.fetch(), reject1, report2,
                report3, reject4);

        //Retrieve last 3 reports
        query.setSendingTimeAfterFilter(time2);
        assertEquals(3, query.fetchCount());
        assertRetrievedReports(query.fetch(), report2, report3, reject4);

        //Retrieve last 2 reports
        query.setSendingTimeAfterFilter(time3);
        assertEquals(2, query.fetchCount());
        assertRetrievedReports(query.fetch(), report3, reject4);

        //Retrieve last 1 reports
        query.setSendingTimeAfterFilter(time4);
        assertEquals(1, query.fetchCount());
        assertRetrievedReports(query.fetch(), reject4);

        //Retrieve no reports
        query.setSendingTimeAfterFilter(time5);
        assertEquals(0, query.fetchCount());
        assertRetrievedReports(query.fetch());
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
        PersistentReport.save(r1);
        OrderCancelReject r2=
            createCancelReject(BROKER,sActorID,null);
        PersistentReport.save(r2);
        OrderCancelReject r3=
            createCancelReject(BROKER,sActorID,sExtraUserID);
        PersistentReport.save(r3);

        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        assertRetrievedReports(query.fetch(),r1,r2,r3);
        query.setViewerFilter(sViewer);
        assertRetrievedReports(query.fetch(),r1);
        query.setViewerFilter(sExtraUser);
        assertRetrievedReports(query.fetch(),r3);
        query.setViewerFilter(sActor);
        assertRetrievedReports(query.fetch());
    }

    /**
     * Verifies query defaults.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void queryDefaults() throws Exception {
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
        assertEquals(MultiPersistentReportQuery.BY_ID, query.getEntityOrder());
        assertNull(query.getSendingTimeAfterFilter());
        assertEquals(-1, query.getFirstResult());
        assertEquals(-1, query.getMaxResult());
    }

    /**
     * Verifies query setters.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void querySetters() throws Exception {
        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();

        //entity order verification
        query.setEntityOrder(null);
        assertEquals(null, query.getEntityOrder());
        query.setEntityOrder(MultiPersistentReportQuery.BY_ID);
        assertEquals(MultiPersistentReportQuery.BY_ID, query.getEntityOrder());

        //sending time verification
        query.setSendingTimeAfterFilter(null);
        assertEquals(null, query.getSendingTimeAfterFilter());
        Date d = new Date();
        query.setSendingTimeAfterFilter(d);
        assertEquals(d, query.getSendingTimeAfterFilter());
    }

    @Test
    public void delete() {
        //Doesn't need to be tested as it's only needed for unit testing
        //execution reports are never deleted in production.
    }
    
    private static void assertRetrievedReports(List<PersistentReport> inList,
                                               ReportBase... inReports)
            throws Exception {
        assertEquals(inReports.length, inList.size());
        int idx = 0;
        for(PersistentReport report: inList) {
            assertReportEquals(inReports[idx++], report.toReport());
        }
    }

}

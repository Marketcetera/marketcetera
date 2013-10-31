package org.marketcetera.ors.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.dao.ExecutionReportDao;
import org.marketcetera.ors.dao.PersistentReportDao;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.history.ReportType;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides access to report objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id$")
public class ReportServiceImpl
        implements ReportService
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ExecutionReportService#purgeReportsBefore(java.util.Date)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public int purgeReportsBefore(Date inPurgeDate)
    {
        List<PersistentReport> reports = persistentReportDao.findSince(inPurgeDate);
        if(reports == null || reports.isEmpty()) {
            return 0;
        }
        List<Long> ids = new ArrayList<Long>();
        for(PersistentReport report : reports) {
            ids.add(report.getId());
        }
        // delete report summaries first - need to use a manual query here to include the list param
        entityManager.createNativeQuery("DELETE FROM exec_reports WHERE report_id IN (:ids)").setParameter("ids",ids).executeUpdate();
        // now, delete the reports
        persistentReportDao.delete(reports);
        return reports.size();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getReportsSince(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public ReportBaseImpl[] getReportsSince(SimpleUser inUser,
                                            Date inDate)
    {
//        JPAQuery jpaQuery = new JPAQuery(entityManager);
//        QPersistentReport persistentReportQuery = QPersistentReport.persistentReport;
//        if (inUser != null && !inUser.isSuperuser()) {
//            jpaQuery = jpaQuery.from(persistentReportQuery).where(QPersistentReport.persistentReport.sendingTime.gt(inDate)
//                                                                  .and(QPersistentReport.persistentReport.viewer.eq(inUser)));
//        } else {
//            jpaQuery = jpaQuery.from(persistentReportQuery).where(QPersistentReport.persistentReport.sendingTime.gt(inDate));
//        }
//        jpaQuery = jpaQuery.orderBy(QPersistentReport.persistentReport.id);
////        MultiPersistentReportQuery query = MultiPersistentReportQuery.all();
////        query.setSendingTimeAfterFilter(inDate);
////        if (!inUser.isSuperuser()) {
////            query.setViewerFilter(inUser);
////        }
////        query.setEntityOrder(MultiPersistentReportQuery.BY_ID);
//
////        List<PersistentReport> reportList = jpaQuery.fetch().
//        ReportBaseImpl [] reports = new ReportBaseImpl[reportList.size()];
//        int i = 0;
//        for(PersistentReport report: reportList) {
//            reports[i++] = (ReportBaseImpl) report.toReport();
//        }
//        return reports;
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getEquityPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Equity inEquity)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOpenOrders(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    public List<ReportBase> getOpenOrders(SimpleUser inUser)
    {
//        List<ReportBase> reports = new ArrayList<ReportBase>();
//        
//        List<ReportBase> rawReports = reportService.getOpenOrders(inUser);
//        //ExecutionReportSummary.getOpenOrders(inUser);
//        try {
//            for(ExecutionReportSummary summary : rawReports) {
//                reports.add(summary.getReport().toReport());
//            }
//        } catch (ReportPersistenceException e) {
//            throw new PersistenceException(e);
//        }
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllEquityPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,
                                                                          Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getCurrencyPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser,
                                              Date inDate,
                                              Currency inCurrency)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllCurrencyPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser,
                                                                              Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllFuturePositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                          Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getFuturePositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOptionPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Option inOption)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(SimpleUser inUser,
                                                                          Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(SimpleUser inUser,
                                                                       Date inDate,
                                                                       String[] inSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#save(org.marketcetera.trade.ReportBase)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentReport save(ReportBase inReport)
    {
        PersistentReport report = persistentReportDao.save(new PersistentReport(inReport,
                                                                                inReport.getActorID() == null ? null : userDao.findOne(inReport.getActorID().getValue()),
                                                                                inReport.getViewerID() == null ? null : userDao.findOne(inReport.getViewerID().getValue())));
        if(report.getReportType() == ReportType.ExecutionReport) {
            ExecutionReportSummary reportSummary = new ExecutionReportSummary((ExecutionReport)inReport,
                                                                              report);
            // CD 17-Mar-2011 ORS-79
            // we need to find the correct root ID of the incoming ER. for cancels and cancel/replaces,
            //  this is easy - we can look up the root ID from the origOrderID. for a partial fill or fill
            //  of an original order, this is also easy - the rootID is just the orderID. the difficult case
            //  is a partial fill or fill of a replaced order. the origOrderID won't be present (not required)
            //  but there still exists an order chain to be respected or position reporting will be broken.
            //  therefore, the algorithm should be:
            // if the original orderID is present, use the root from that order
            // if it's not present, look for the rootID of an existing record with the same orderID
            SLF4JLoggerProxy.debug(this,
                                   "Searching for rootID for {}",  //$NON-NLS-1$
                                   reportSummary.getOrderID());
            OrderID orderId = null;
            if(reportSummary.getOrigOrderID() == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No origOrderID present, using orderID for query");  //$NON-NLS-1$
                orderId = reportSummary.getOrderID();
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Using origOrderID {} for query",  //$NON-NLS-1$
                                       reportSummary.getOrigOrderID());
                orderId = reportSummary.getOrigOrderID();
            }
            List<OrderID> list = executionReportDao.findRootIDForOrderID(orderId);
            if(list.isEmpty()) {
                SLF4JLoggerProxy.debug(this,
                                       "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
                // this is the first order in this chain
                reportSummary.setRootID(reportSummary.getOrderID());
            } else {
                OrderID rootID = (OrderID)list.get(0);
                SLF4JLoggerProxy.debug(this,
                                       "Not the first orderID in the chain, using {} for rootID",  //$NON-NLS-1$
                                       rootID);
                reportSummary.setRootID(rootID);
            }
            reportSummary = executionReportDao.save(reportSummary);
        }
        return report;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#delete(org.marketcetera.ors.history.PersistentReport)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(ReportBase inReport)
    {
        if(inReport == null) {
            return;
        }
        if(inReport instanceof PersistentReport) {
            persistentReportDao.delete(((PersistentReport)inReport));
        } else {
            PersistentReport report = persistentReportDao.findByReportID(inReport.getReportID());
            if(report != null) {
                persistentReportDao.delete(report);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getPrincipals(org.marketcetera.trade.OrderID)
     */
    @Override
    public Principals getPrincipals(OrderID inOrderID)
    {
//        return PersistentReport.getPrincipals(orderID);
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#findAll()
     */
    @Override
    public List<ExecutionReportSummary> findAllExecutionReportSummary()
    {
        return executionReportDao.findAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#findAllPersistentReport()
     */
    @Override
    public List<PersistentReport> findAllPersistentReport()
    {
        return persistentReportDao.findAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#findAllPersistentReportSince(java.util.Date)
     */
    @Override
    public List<PersistentReport> findAllPersistentReportSince(Date inDate)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#findAllPersistentReportByViewer(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    public List<PersistentReport> findAllPersistentReportByViewer(SimpleUser inViewer)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * provides datastore access to users
     */
    @Autowired
    private UserDao userDao;
    /**
     * provides datastore access to execution reports
     */
    @Autowired
    private ExecutionReportDao executionReportDao;
    /**
     * provides datastore access to persistent reports
     */
    @Autowired
    private PersistentReportDao persistentReportDao;
    /**
     * 
     */
    @PersistenceContext
    private EntityManager entityManager;
}

package org.marketcetera.ors.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.dao.ExecutionReportDao;
import org.marketcetera.ors.dao.PersistentReportDao;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@Transactional(readOnly=true)
public class ReportServiceImpl
        implements ReportService
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ExecutionReportService#purgeReportsBefore(java.util.Date)
     */
    @Override
    public int purgeReportsBefore(Date inPurgeDate)
    {
//        List<PersistentReport> reportList = persistentReportDao.findReportForOrderBefore(mPurgeDate.toDate());
//        int count = executionReportService.deleteReportsIn(reportList);
        throw new UnsupportedOperationException(); // TODO
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
    public PersistentReport save(ReportBase inReport)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#delete(org.marketcetera.ors.history.PersistentReport)
     */
    @Override
    public void delete(ReportBase inReport)
    {
//        PersistentReport report = persistentReportDao.findReportForOrder(inReport.getOrderID());
        throw new UnsupportedOperationException(); // TODO
        
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
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#findAllPersistentReport()
     */
    @Override
    public List<PersistentReport> findAllPersistentReport()
    {
        throw new UnsupportedOperationException(); // TODO
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
}

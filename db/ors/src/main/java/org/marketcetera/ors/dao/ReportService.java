package org.marketcetera.ors.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.history.PersistentReport;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ReportService
{
    /**
     * 
     *
     *
     * @param inPurgeDate
     * @return
     */
    public int purgeReportsBefore(Date inPurgeDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    public ReportBaseImpl[] getReportsSince(SimpleUser inUser,
                                            Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inEquity
     * @return
     */
    public BigDecimal getEquityPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Equity inEquity);
    /**
     *
     *
     * @param inUser
     * @return
     */
    public List<ReportBase> getOpenOrders(SimpleUser inUser);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inCurrency
     * @return
     */
    public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser,
                                              Date inDate,
                                              Currency inCurrency);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser,
                                                                             Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inFuture
     * @return
     */
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inOption
     * @return
     */
    public BigDecimal getOptionPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Option inOption);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inSymbols
     * @return
     */
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(SimpleUser inUser,
                                                                      Date inDate,
                                                                      String[] inSymbols);
    /**
     *
     *
     * @param inReport
     */
    public PersistentReport save(ReportBase inReport);
    /**
     *
     *
     * @param inReport
     */
    public void delete(ReportBase inReport);
    /**
     *
     *
     * @param inOrderID
     * @return
     */
    public Principals getPrincipals(OrderID inOrderID);
    /**
     * 
     *
     *
     * @return
     */
    public List<ExecutionReportSummary> findAllExecutionReportSummary();
    /**
     *
     *
     * @return
     */
    public List<PersistentReport> findAllPersistentReport();
    /**
     * 
     *
     *
     * @param inDate
     * @return
     */
    public List<PersistentReport> findAllPersistentReportSince(Date inDate);
    /**
     *
     *
     * @param inViewer
     * @return
     */
    public List<PersistentReport> findAllPersistentReportByViewer(SimpleUser inViewer);
}

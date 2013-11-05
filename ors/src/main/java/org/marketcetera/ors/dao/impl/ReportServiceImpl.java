package org.marketcetera.ors.dao.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.dao.ExecutionReportDao;
import org.marketcetera.ors.dao.PersistentReportDao;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.history.*;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;

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
    public List<ReportBase> getReportsSince(SimpleUser inUser,
                                            Date inDate)
    {
        QPersistentReport r = QPersistentReport.persistentReport;
        BooleanExpression where = r.sendingTime.goe(inDate);
        if(!inUser.isSuperuser()) {
            where = where.and(r.viewer.eq(inUser));
        }
        Sort sort = new Sort(Sort.Direction.ASC,
                             r.sendingTime.getMetadata().getName());
        // can expose the page and page size to allow paging through the api interfaces
        PageRequest page = new PageRequest(0,
                                           Integer.MAX_VALUE,
                                           sort);
        Iterable<PersistentReport> reports = persistentReportDao.findAll(where,
                                                                         page);
        List<ReportBase> results = new ArrayList<ReportBase>();
        for(PersistentReport report : reports) {
            results.add(report.toReport());
        }
        return results;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getEquityPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Equity inEquity)
    {
        return getPositionAsOf(inUser,
                               inDate,
                               inEquity,
                               new SymbolMatcher<Equity>() {
                                @Override
                                public BooleanExpression where(QExecutionReportSummary inTableProxy,
                                                               Equity inInstrument)
                                {
                                    return inTableProxy.symbol.eq(inInstrument.getSymbol());
                                }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOpenOrders(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    public List<ReportBase> getOpenOrders(SimpleUser inViewer)
    {
        List<ReportBase> reports = new ArrayList<ReportBase>();
        List<ExecutionReportSummary> rawReports = executionReportDao.findOpenOrders(inViewer);
        for(ExecutionReportSummary summary : rawReports) {
            reports.add(summary.getReport().toReport());
        }
        return reports;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllEquityPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate)
    {
        return getAllPositionsAsOf(inDate,
                                   inUser,
                                   SecurityType.CommonStock,
                                   new PositionTransformer<Equity>() {
            @Override
            public PositionKey<Equity> createPositionKey(String inSymbol,
                                                         String inExpiry,
                                                         BigDecimal inStrikePrice,
                                                         OptionType inOptionType,
                                                         String inAccount,
                                                         Long inTraderId)
            {
                return PositionKeyFactory.createEquityKey(inSymbol,
                                                          inAccount,
                                                          inTraderId == null ? null : String.valueOf(inTraderId));
            }});
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getCurrencyPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser,
                                              Date inDate,
                                              Currency inCurrency)
    {
        return getPositionAsOf(inUser,
                               inDate,
                               inCurrency,
                               new SymbolMatcher<Currency>() {
                                @Override
                                public BooleanExpression where(QExecutionReportSummary inTableProxy,
                                                               Currency inInstrument)
                                {
                                    return inTableProxy.symbol.eq(inInstrument.getSymbol());
                                }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllCurrencyPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser,
                                                                             Date inDate)
    {
        return getAllPositionsAsOf(inDate,
                                   inUser,
                                   SecurityType.Currency,
                                   new PositionTransformer<Currency>() {
            @Override
            public PositionKey<Currency> createPositionKey(String inSymbol,
                                                           String inExpiry,
                                                           BigDecimal inStrikePrice,
                                                           OptionType inOptionType,
                                                           String inAccount,
                                                           Long inTraderId)
            {
                return PositionKeyFactory.createCurrencyKey(inSymbol,
                                                            inAccount,
                                                            inTraderId == null ? null : String.valueOf(inTraderId));
            }});
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllFuturePositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                         Date inDate)
    {
        return getAllPositionsAsOf(inDate,
                                   inUser,
                                   SecurityType.Future,
                                   new PositionTransformer<Future>() {
            @Override
            public PositionKey<Future> createPositionKey(String inSymbol,
                                                         String inExpiry,
                                                         BigDecimal inStrikePrice,
                                                         OptionType inOptionType,
                                                         String inAccount,
                                                         Long inTraderId)
            {
                return PositionKeyFactory.createFutureKey(inSymbol,
                                                          inExpiry,
                                                          inAccount,
                                                          inTraderId == null ? null : String.valueOf(inTraderId));
            }});
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getFuturePositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture)
    {
        return getPositionAsOf(inUser,
                               inDate,
                               inFuture,
                               new SymbolMatcher<Future>() {
                                @Override
                                public BooleanExpression where(QExecutionReportSummary inTableProxy,
                                                               Future inInstrument)
                                {
                                    return inTableProxy.symbol.eq(inInstrument.getSymbol()).and(inTableProxy.expiry.eq(inInstrument.getExpiryAsString()));
                                }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOptionPositionAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Option inOption)
    {
        return getPositionAsOf(inUser,
                               inDate,
                               inOption,
                               new SymbolMatcher<Option>() {
                                @Override
                                public BooleanExpression where(QExecutionReportSummary inTableProxy,
                                                               Option inInstrument)
                                {
                                    return inTableProxy.symbol.eq(inInstrument.getSymbol())
                                            .and(inTableProxy.expiry.eq(inInstrument.getExpiry()))
                                            .and(inTableProxy.strikePrice.eq(inInstrument.getStrikePrice()))
                                            .and(inTableProxy.optionType.eq(inInstrument.getType()));
                                }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getAllOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(SimpleUser inUser,
                                                                         Date inDate)
    {
        return getAllPositionsAsOf(inDate,
                                   inUser,
                                   SecurityType.Option,
                                   new PositionTransformer<Option>() {
            @Override
            public PositionKey<Option> createPositionKey(String inSymbol,
                                                         String inExpiry,
                                                         BigDecimal inStrikePrice,
                                                         OptionType inOptionType,
                                                         String inAccount,
                                                         Long inTraderId)
            {
                return PositionKeyFactory.createOptionKey(inSymbol,
                                                          inExpiry,
                                                          inStrikePrice,
                                                          inOptionType,
                                                          inAccount,
                                                          inTraderId == null ? null : String.valueOf(inTraderId));
            }});
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.ReportService#getOptionPositionsAsOf(org.marketcetera.ors.security.SimpleUser, java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(SimpleUser inUser,
                                                                      Date inDate,
                                                                      String[] inSymbols)
    {
        return getAllPositionsAsOf(inDate,
                                   inUser,
                                   SecurityType.Option,
                                   new PositionTransformer<Option>() {
            @Override
            public PositionKey<Option> createPositionKey(String inSymbol,
                                                         String inExpiry,
                                                         BigDecimal inStrikePrice,
                                                         OptionType inOptionType,
                                                         String inAccount,
                                                         Long inTraderId)
            {
                return PositionKeyFactory.createOptionKey(inSymbol,
                                                          inExpiry,
                                                          inStrikePrice,
                                                          inOptionType,
                                                          inAccount,
                                                          inTraderId == null ? null : String.valueOf(inTraderId));
            }},inSymbols);
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
            OrderID rootID = executionReportDao.findRootIDForOrderID(orderId);
            if(rootID == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
                // this is the first order in this chain
                rootID = reportSummary.getOrderID();
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Not the first orderID in the chain, using {} for rootID",  //$NON-NLS-1$
                                       rootID);
            }
            reportSummary.setRootID(rootID);
            reportSummary = executionReportDao.save(reportSummary);
            // update is_open marker on other reports in the family (not sure if this should be run for ERs only or all reports)
            executionReportDao.updateOpenOrders(rootID,
                                                reportSummary.getId());
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
        List<PersistentReport> reports = persistentReportDao.findByOrderID(inOrderID);
        if(reports.isEmpty()) {
            return Principals.UNKNOWN;
        }
        PersistentReport report = reports.get(0);
        return new Principals(report.getActorID(),
                              report.getViewerID());
    }
    /**
     * Executes an all positions query by instrument type.
     *
     * @param inAsOfDate a <code>Date</code> value
     * @param inViewer a <code>SimpleUser</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inTupleTransformer a <code>PositionTransformer&lt;I&gt;</code> value
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>Map&lt;PositionKey&lt;I&gt;,BigDecimal&gt;</code> value
     */
    private <I extends Instrument> Map<PositionKey<I>,BigDecimal> getAllPositionsAsOf(Date inAsOfDate,
                                                                                      SimpleUser inViewer,
                                                                                      SecurityType inSecurityType,
                                                                                      PositionTransformer<I> inTupleTransformer,
                                                                                      String...inSymbols)
    {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QExecutionReportSummary e = new QExecutionReportSummary("e");
        BooleanExpression where = e.sendingTime.loe(inAsOfDate);
        where = where.and(e.securityType.eq(inSecurityType));
        if(!inViewer.isSuperuser()) {
            where = where.and(e.viewer.eq(inViewer));
        }
        if(inSymbols != null && inSymbols.length != 0) {
            where = where.and(e.symbol.in(inSymbols));
        }
        QExecutionReportSummary s = new QExecutionReportSummary("s");
        where = where.and(e.id.eq(new SQLSubQuery().where(s.rootOrderId.eq(e.rootOrderId).and(s.orderStatus.notIn(OrderStatus.PendingCancel,OrderStatus.PendingNew,OrderStatus.PendingReplace))).from(s).unique(s.id.max())));
        jpaQuery = jpaQuery.from(e).where(where);
        NumberExpression<BigDecimal> position = e.effectiveCumQuantity.sum();
        jpaQuery = jpaQuery.groupBy(e.symbol,e.expiry,e.strikePrice,e.optionType,e.account,e.actor).having(position.ne(BigDecimal.ZERO));
        jpaQuery = jpaQuery.orderBy(e.symbol.asc(),e.account.asc(),e.actor.id.asc());
        List<Tuple> results = jpaQuery.list(position,e.symbol,e.account,e.actor.id,e.expiry,e.strikePrice,e.optionType);
        Map<PositionKey<I>,BigDecimal> finalResults = new LinkedHashMap<PositionKey<I>,BigDecimal>();
        for(Tuple result : results) {
            finalResults.put(inTupleTransformer.createPositionKey(result.get(e.symbol),
                                                                  result.get(e.expiry),
                                                                  result.get(e.strikePrice),
                                                                  result.get(e.optionType),
                                                                  result.get(e.account),
                                                                  result.get(e.actor.id)),
                             result.get(position));
        }
        return finalResults;
    }
    /**
     * Executes a position query for the given instrument.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inInstrument an <code>I</code> value
     * @param inSymbolMatcher a <code>SymbolMatcher&lt;I&gt;</code> value
     * @return a <code>BigDecimal</code> value
     */
    private <I extends Instrument> BigDecimal getPositionAsOf(SimpleUser inUser,
                                                              Date inDate,
                                                              I inInstrument,
                                                              SymbolMatcher<I> inSymbolMatcher)
    {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QExecutionReportSummary a = new QExecutionReportSummary("a");
        BooleanExpression where = inSymbolMatcher.where(a,
                                                        inInstrument);
        where = where.and(a.securityType.eq(inInstrument.getSecurityType()));
        where = where.and(a.sendingTime.loe(inDate));
        if(!inUser.isSuperuser()) {
            where = where.and(a.viewer.eq(inUser));
        }
        QExecutionReportSummary b = new QExecutionReportSummary("b");
        where = where.and(a.id.eq(new SQLSubQuery().where(b.rootOrderId.eq(a.rootOrderId).and(b.orderStatus.notIn(OrderStatus.PendingCancel,OrderStatus.PendingNew,OrderStatus.PendingReplace))).from(b).unique(b.id.max())));
        jpaQuery = jpaQuery.from(a).where(where);
        BigDecimal result = jpaQuery.singleResult(a.effectiveCumQuantity.sum().as("position"));
        return result == null ? BigDecimal.ZERO : result;
    }
    /**
     * Translates a position tuple to a position key.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private interface PositionTransformer<I extends Instrument>
    {
        /**
         * Creates a <code>PositionKey</code> from the given inputs.
         *
         * @param inSymbol a <code>String</code> value
         * @param inExpiry a <code>String</code> value
         * @param inStrikePrice a <code>BigDecimal</code> value
         * @param inOptionType an <code>OptionType</code> value
         * @param inAccount a <code>String</code> value
         * @param inTraderId a <code>Long</code> value
         * @return a <code>PositionKey&lt;I&gt; value
         */
        PositionKey<I> createPositionKey(String inSymbol,
                                         String inExpiry,
                                         BigDecimal inStrikePrice,
                                         OptionType inOptionType,
                                         String inAccount,
                                         Long inTraderId);
    }
    /**
     * Creates a predicate used to match rows to a given instrument.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private interface SymbolMatcher<I extends Instrument>
    {
        /**
         * Builds a where predicate that matches records to the given instrument.
         *
         * @param inTableProxy a <code>QExecutionReportSummary</code> value
         * @param inInstrument an <code>I</code> value
         * @return a <code>BooleanExpression</code> value
         */
        BooleanExpression where(QExecutionReportSummary inTableProxy,
                                I inInstrument);
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
     * entity manager value used to construct queries
     */
    @PersistenceContext
    private EntityManager entityManager;
}

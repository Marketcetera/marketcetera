package org.marketcetera.trade.dao;

import java.util.Set;

import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentExecutionReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
public interface ExecutionReportDao
        extends PagingAndSortingRepository<PersistentExecutionReport,Long>,QuerydslPredicateExecutor<PersistentExecutionReport>
{
    /**
     * Finds the report summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return a <code>PersistentExecutionReport</code> value or <code>null</code>
     */
    PersistentExecutionReport findByReportId(long inReportId);
    /**
     * Find the most recent report for the given root order ID.
     *
     * @param inRootId an <code>OrderID</code> value
     * @param inPage a <code>Pageable</code> value
     * @return a <code>Page&lt;PersistentExecutionReport&gt;</code> value
     */
    @Query("select E1 from PersistentExecutionReport E1 where E1.rootOrderId=?1 and E1.sendingTime=(select max(E2.sendingTime) from PersistentExecutionReport E2 where E2.rootOrderId=?1) order by E1.id desc")
    Page<PersistentExecutionReport> findMostRecentReportFor(OrderID inRootId,
                                                            Pageable inPage);
    /**
     * Find a page of fill execution reports.
     *
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>Page&lt;ExecutionReport&gt;</code> value
     */
    @Query("select e from PersistentExecutionReport e where e.execType in ('PartialFill','Fill','Trade','TradeCorrect')")
    Page<ExecutionReportSummary> findAllFills(Pageable inPageRequest);
    /**
     * Find the average fill prices of a page of instrument/side tuples.
     *
     * @param inFillTypes a <code>Set&lt;ExecutionType&gt;</code> value
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>Page&lt;AverageFillQueryResult&gt;</code> value
     */
    @Query("select new org.marketcetera.trade.dao.AverageFillQueryResult(e.symbol,e.securityType,e.expiry,e.strikePrice,e.optionType,e.side,sum(e.lastQuantity*e.lastPrice)/sum(e.lastQuantity),sum(e.cumQuantity)) from PersistentExecutionReport e where e.execType in ?1 group by e.symbol,e.securityType,e.expiry,e.strikePrice,e.optionType,e.side")
    Page<AverageFillQueryResult> findAverageFillPrice(Set<ExecutionType> inFillTypes,
                                                      Pageable inPageRequest);
}

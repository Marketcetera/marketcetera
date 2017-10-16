package org.marketcetera.trade.dao;

import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentExecutionReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionReportDao.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: ExecutionReportDao.java 17266 2017-04-28 14:58:00Z colin $")
public interface ExecutionReportDao
        extends PagingAndSortingRepository<PersistentExecutionReport,Long>,QueryDslPredicateExecutor<PersistentExecutionReport>
{
    /**
     * Finds the root orderID for the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value or <code>null</code>
     */
    @Query("select distinct rootOrderId from PersistentExecutionReport where orderId=?1")
    OrderID findRootIDForOrderID(OrderID inOrderID);
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
}

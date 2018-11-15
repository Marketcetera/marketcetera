package org.marketcetera.ors.dao;

import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


/* $License$ */

/**
 * Provides data store access to {@link ExecutionReportSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionReportDao.java 17497 2018-04-18 21:18:11Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: ExecutionReportDao.java 17497 2018-04-18 21:18:11Z colin $")
public interface ExecutionReportDao
        extends PagingAndSortingRepository<ExecutionReportSummary,Long>,QuerydslPredicateExecutor<ExecutionReportSummary>
{
    /**
     * Finds the root orderID for the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value or <code>null</code>
     */
    @Query("select distinct rootOrderId from ExecutionReportSummary where orderId=?1")
    OrderID findRootIDForOrderID(OrderID inOrderID);
    /**
     * Finds the report summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return an <code>ExecutionReportSummary</code> value or <code>null</code>
     */
    ExecutionReportSummary findByReportId(long inReportId);
    /**
     * Find the most recent execution report for the given root order id/order id tuple.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @return an <code>ExecutionReportSummary</code> or <code>null</code>
     */
    ExecutionReportSummary findFirst1ByRootOrderIdAndOrderIdOrderBySendingTimeDesc(OrderID inRootOrderId,
                                                                                   OrderID inOrderId);
}

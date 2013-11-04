package org.marketcetera.ors.dao;

import java.util.List;

import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link ExecutionReportSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ExecutionReportDao
        extends JpaRepository<ExecutionReportSummary,Long>,QueryDslPredicateExecutor<ExecutionReportSummary>
{
    /**
     * Finds the root orderID for the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value or <code>null</code>
     */
    OrderID findRootIDForOrderID(OrderID inOrderID);
    /**
     * Finds the open orders viewable by the given user.
     *
     * @param inIsSuperuser
     * @param inViewer
     * @return
     */
    List<ExecutionReportSummary> findOpenOrders(SimpleUser inViewer);
    @Modifying
    @Query("update ExecutionReportSummary e set e.isOpen=false where e.rootOrderId=?1 and e.id!=?2")
    int updateOpenOrders(OrderID inRootOrderID,
                         long inId);
}

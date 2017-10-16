package org.marketcetera.trade.dao;

import java.util.List;
import java.util.Set;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to {@link PersistentOrderSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummaryDao
        extends JpaRepository<PersistentOrderSummary,Long>,QueryDslPredicateExecutor<PersistentOrderSummary>
{
    /**
     * Find the order summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return a <code>PersistentOrderStatus</code> value or <code>null</code>
     */
    PersistentOrderSummary findByReportId(long inReportId);
    /**
     * Find the order summaries with the given root order id.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;PersistentOrderStatus&gt;</code> value
     */
    List<PersistentOrderSummary> findByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the order summary with the given root order id and individual order id.
     *
     * @param inRootID an <code>OrderID</code> value
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>PersistentOrderStatus</code>value
     */
    PersistentOrderSummary findByRootOrderIdAndOrderId(OrderID inRootID,
                                                      OrderID inOrderID);
    /**
     * Find the order status values with the given order id.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;PersistentOrderStatus&gt;</code> value
     */
    List<PersistentOrderSummary> findByOrderId(OrderID inOrderId);
    /**
     * Find the most recent member from each order chain where the most recent member is open.
     *
     * @param inOpenOrderStatuses a <code>Set&lt;OrderStatus&gt;</code> value
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>Page&lt;PersistentOrderStatus&gt;</code> value
     */
    @Query("select o1 from OrderStatus o1 where o1.orderStatus in ?1 and o1.id = (select max(o2.id) from OrderStatus o2 where o1.rootOrderId=o2.rootOrderId)")
    Page<PersistentOrderSummary> findOpenOrders(Set<OrderStatus> inOpenOrderStatuses,
                                                Pageable inPageRequest);
}

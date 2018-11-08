package com.marketcetera.ors.dao;

import java.util.List;
import java.util.Set;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.springframework.data.domain.Page;

import com.marketcetera.ors.history.OrderStatus;
import com.marketcetera.ors.history.PersistentOrderStatus;
import com.marketcetera.ors.history.PersistentReport;
import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * Provides order status services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderStatusService
{
    /**
     * Find the order summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return an <code>OrderStatus</code> value or <code>null</code>
     */
    OrderStatus findByReportId(long inReportId);
    /**
     * Find the order summaries with the given order id.
     * 
     * <p>Be aware that there is no FIX-requirement that the order ID be unique between venues
     * or days. Therefore, this method might not retrieve unique results.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;OrderStatus&gt;</code> value
     */
    List<OrderStatus> findByOrderId(OrderID inOrderId);
    /**
     * Find the order status of the most recent order in the given order chain.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderStatus findMostRecentByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the most recent execution by the given root order id.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus findMostRecentExecutionByRootOrderId(OrderID inRootOrderId);
    /**
     * Save the order status value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus save(OrderStatus inOrderStatus);
    /**
     * Delete the given order status value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    void delete(OrderStatus inOrderStatus);
    /**
     * Find the order status of the first order in the given order chain.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderStatus findFirstByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the reports that have an open order status.
     *
     * @param inViewer a <code>SimpleUser</code> value
     * @param inOrderStatusValues a <code>Set&lt;OrderStatus&gt;</code> value
     * @return a <code>List&lt;PersistentReport&gt;</code> value
     */
    List<PersistentReport> findReportByOrderStatusIn(SimpleUser inViewer,
                                                     Set<org.marketcetera.trade.OrderStatus> inOrderStatusValues);
    /**
     * Find the order summary with the given root order id and individual order id.
     *
     * @param inRootID an <code>OrderID</code> value
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderStatus findByRootOrderIdAndOrderId(OrderID inRootID,
                                            OrderID inOrderID);
    /**
     * Find the open orders using the given page attributes.
     *
     * @param inPageNumber an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>Page&lt;PersistentOrderStatus&gt;</code> value
     */
    Page<PersistentOrderStatus> findOpenOrders(int inPageNumber,
                                               int inPageSize);
    /**
     * Update the order status value with the given reports.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inReport a <code>PersistentReport</code> value
     * @param inReportBase a <code>ReportBase</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus update(OrderStatus inOrderStatus,
                       PersistentReport inReport,
                       ReportBase inReportBase);
}

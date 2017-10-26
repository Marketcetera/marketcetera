package org.marketcetera.trade.service;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;

/* $License$ */

/**
 * Provides order summary services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummaryService
{
    /**
     * Find the order summary with the given report id.
     *
     * @param inReportId a <code>long</code> value
     * @return an <code>OrderStatus</code> value or <code>null</code>
     */
    OrderSummary findByReportId(long inReportId);
    /**
     * Find the order summaries with the given order id.
     * 
     * <p>Be aware that there is no FIX-requirement that the order ID be unique between venues
     * or days. Therefore, this method might not retrieve unique results.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>List&lt;OrderStatus&gt;</code> value
     */
    List<OrderSummary> findByOrderId(OrderID inOrderId);
    /**
     * Find the order status of the most recent order in the given order chain.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderSummary findMostRecentByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the most recent execution by the given root order id.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderSummary findMostRecentExecutionByRootOrderId(OrderID inRootOrderId);
    /**
     * Save the order status value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderSummary save(OrderSummary inOrderStatus);
    /**
     * Delete the given order status value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    void delete(OrderSummary inOrderStatus);
    /**
     * Find the order status of the first order in the given order chain.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderSummary findFirstByRootOrderId(OrderID inRootOrderId);
    /**
     * Find the reports that have an open order status.
     *
     * @param inViewer a <code>SimpleUser</code> value
     * @param inOrderStatusValues a <code>Set&lt;OrderStatus&gt;</code> value
     * @return a <code>List&lt;Report&gt;</code> value
     */
    List<Report> findReportByOrderStatusIn(User inViewer,
                                           Set<OrderStatus> inOrderStatusValues);
    /**
     * Find the order summary with the given root order id and individual order id.
     *
     * @param inRootID an <code>OrderID</code> value
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderStatus</code>value or <code>null</code>
     */
    OrderSummary findByRootOrderIdAndOrderId(OrderID inRootID,
                                            OrderID inOrderID);
    /**
     * Find the open orders using the given page attributes.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;OrderSummary&gt;</code> value
     */
    CollectionPageResponse<? extends OrderSummary> findOpenOrders(PageRequest inPageRequest);
    /**
     * Update the order status value with the given reports.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inReport a <code>Report</code> value
     * @param inReportBase a <code>ReportBase</code> value
     * @return an <code>OrderStatus</code> value
     */
    OrderSummary update(OrderSummary inOrderStatus,
                       Report inReport,
                       ReportBase inReportBase);
}

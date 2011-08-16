package org.marketcetera.trade.utils;

import java.util.*;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.collections.UnmodifiableDeque;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages order history for multiple orders throughout the order lifecycle.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class OrderHistoryManager
{
    /**
     * Gets the latest <code>ReportBase</code> for the given <code>OrderID</code>.
     *
     * <p>The given <code>OrderID</code> may correspond to either the
     * actual order ID or order ID of a replaced order in the same order chain.
     * 
     * <p>The returned <code>ReportBase</code> is the most recent report at a static point in time only.
     * Changes to the underlying order history are not reflected in the returned <code>ReportBase</code>.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>ReportBase</code> or <code>null</code> if there is no known status for the given <code>OrderID</code>
     */
    public ReportBase getLatestReportFor(OrderID inOrderID)
    {
        SLF4JLoggerProxy.debug(OrderHistoryManager.class,
                               "Searching order tracker for {}", //$NON-NLS-1$
                               inOrderID);
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderID);
            if(history != null) {
                return history.getLatestReport();
            }
            SLF4JLoggerProxy.debug(OrderHistoryManager.class,
                                   "No history for {}", //$NON-NLS-1$
                                   inOrderID);
            return null;
        }
    }
    /**
     * Adds the given <code>ReportBase</code> to the order history.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public void add(ReportBase inReport)
    {
        synchronized(orders) {
            OrderID actualOrderID = inReport.getOrderID();
            OrderID originalOrderID = inReport.getOriginalOrderID();
            // find the order history for this report
            // first, look for a match of the actual order ID (simple, non-replace order case)
            OrderHistory history = orders.get(actualOrderID);
            if(history == null) {
                // ok, no order history for the actual order ID. this is caused by one of two things:
                //  1/ This is the first time we've seen anything in this chain
                //  2/ The report is a replace order and we should search using the originalOrderID
                history = orders.get(originalOrderID);
                if(history == null) {
                    // now we know this is case #1 from above: create a new order history and add it
                    history = new OrderHistory();
                    // index the new history using the actual order ID
                    orders.put(actualOrderID,
                               history);
                } else {
                    // case #2 from above: add an index reference for the new actual order ID
                    orders.put(actualOrderID,
                               history);
                }
            }
            // add the report to the order history
            history.add(inReport);
        }
    }
    /**
     * Gets the <code>ReportBase</code> values for the given <code>OrderID</code>.
     * 
     * <p>The <code>ReportBase</code> collection returned is sorted from newest to oldest.
     * 
     * <p>The returned <code>Deque</code> reflects changes to the underlying order history. 
     * 
     * <p>The given <code>OrderID</code> may be either an order ID or an original order ID. The reports
     * returned will be the same in either case. If no history exists for the given <code>OrderID<code>,
     * an empty <code>Deque</code> is returned.
     * 
     * <p>The underlying order history is populated by calls to {@link #add(ReportBase)}.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>Deque&lt;ReportBase&gt;</code> value which may be empty
     */
    public Deque<ReportBase> getReportHistoryFor(OrderID inOrderId)
    {
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderId);
            if(history == null) {
                return NO_HISTORY;
            }
            return history.getOrderHistory();
        }
    }
    /**
     * Gets all <code>OrderID</code> values for which history is known.
     * 
     * <p>The returned collection will be updated as new order history is received. The
     * sort order of the returned collection is unspecified. 
     *
     * @return a <code>Set&lt;OrderID&gt;</code> value
     */
    public Set<OrderID> getOrderIds()
    {
        synchronized(orders) {
            return Collections.unmodifiableSet(orders.keySet());
        }
    }
    /**
     * Clears all order history.
     */
    public void clear()
    {
        synchronized(orders) {
            for(OrderHistory history : orders.values()) {
                history.clear();
            }
            orders.clear();
        }
    }
    /**
     * Clears the order history values for the given <code>OrderID</code> if any.
     * 
     * <p>This method has no effect if there is no stored order history for the
     * given <code>OrderID</code>.
     * 
     * <p>This method will clear the order history for all orders in the order history
     * chain including replaced orders.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    public void clear(OrderID inOrderId)
    {
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderId);
            if(history != null) {
                for(OrderID orderID : history.getOrderIdChain()) {
                    SLF4JLoggerProxy.debug(OrderHistoryManager.class,
                                           "Clearing history for {}", //$NON-NLS-1$
                                           orderID);
                    orders.remove(orderID);
                }
                history.clear();
            }
        }
    }
    /**
     * Gets the chain of <code>OrderID</code> values that describe the evolution of the
     * given order.
     * 
     * <p>Changes to the underlying order history will be reflected in the
     * returned collection.
     * 
     * <p>OrderIDs are sorted from oldest to newest.
     * 
     * <p>If the given <code>OrderID</code> has no corresponding order history, the
     * returned collection will be empty.
     *
     * @return a <code>Set&lt;OrderID&gt;</code> value
     */
    public Set<OrderID> getOrderChain(OrderID inOrderId)
    {
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderId);
            if(history != null) {
                return history.getOrderIdChain();
            }
            return NO_ORDER_CHAIN;
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderHistoryManager with history for: ").append(orders.keySet()); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Tracks order history for a single order.
     * 
     * <p>Instantiate this object and add <code>ReportBase</code> objects for
     * this same order chain. No validation is done to make sure that incoming
     * <code>ReportBase</code> objects are truly part of the order chain: the
     * act of invoking <code>add</code> implicitly establishes this fact.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @NotThreadSafe
    @ClassVersion("$Id$")
    private static class OrderHistory
    {
        /**
         * Adds the given <code>ReportBase</code> to the order history.
         * 
         * @param inReport a <code>ReportBase</code> value
         */
        private void add(ReportBase inReport)
        {
            orderHistory.addFirst(inReport);
            orderIdChain.add(inReport.getOrderID());
            latestReport = inReport;
        }
        /**
         * Clears the order history object.
         */
        private void clear()
        {
            orderHistory.clear();
            orderIdChain.clear();
            latestReport = null;
        }
        /**
         * Gets the order history.
         * 
         * <p>The reports returned are sorted from newest to oldest.
         * 
         * <p>Changes to the underlying order history will be reflected in the
         * returned collection.
         *
         * @return a <code>Deque&lt;ReportBase&gt;</code> value
         */
        private Deque<ReportBase> getOrderHistory()
        {
            return new UnmodifiableDeque<ReportBase>(orderHistory);
        }
        /**
         * Get the latestReport value.
         *
         * @return a <code>ReportBase</code> value
         */
        private ReportBase getLatestReport()
        {
            return latestReport;
        }
        /**
         * Gets the chain of <code>OrderID</code> values that describe the evolution of this
         * order.
         * 
         * <p>Changes to the underlying order history will be reflected in the
         * returned collection.
         * 
         * <p>OrderIDs are sorted from oldest to newest.
         *
         * @return a <code>Set&lt;OrderID&gt;</code> value
         */
        private Set<OrderID> getOrderIdChain()
        {
            return Collections.unmodifiableSet(orderIdChain);
        }
        /**
         * order history sorted from newest to oldest
         */
        private final Deque<ReportBase> orderHistory = new LinkedList<ReportBase>();
        /**
         * order IDs in the order chain in the order they occurred
         */
        private final Set<OrderID> orderIdChain = new LinkedHashSet<OrderID>();
        /**
         * most recent <code>ExecutionReport</code>, may be <code>null</code>
         */
        private volatile ReportBase latestReport;
    }
    /**
     * order history objects indexed by actual order ID
     */
    @GuardedBy("orders")
    private final Map<OrderID,OrderHistory> orders = new HashMap<OrderID,OrderHistory>();
    /**
     * sentinel collection used to indicate there is no history available for an order
     */
    private static final Deque<ReportBase> NO_HISTORY = new UnmodifiableDeque<ReportBase>(new LinkedList<ReportBase>());
    /**
     * sentinel collection used to indicate there is no order chain for a given order ID
     */
    private static final Set<OrderID> NO_ORDER_CHAIN = Collections.emptySet();
}

package org.marketcetera.trade.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Messages;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.collections.UnmodifiableDeque;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.nocrala.tools.texttablefmt.*;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;

/* $License$ */

/**
 * Manages order history for multiple orders throughout the order lifecycle.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
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
        SLF4JLoggerProxy.debug(this,
                               "Searching order tracker for {}", //$NON-NLS-1$
                               inOrderID);
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderID);
            if(history != null) {
                ReportBase report = history.getLatestReport();
                SLF4JLoggerProxy.debug(this,
                                       "Retrieved {} for {}",
                                       report,
                                       inOrderID);
                return report;
            }
            SLF4JLoggerProxy.debug(this,
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
        if(inReport.getOrderStatus() == null ||
           inReport.getOrderID() == null) {
            Messages.SKIPPNG_MALFORMED_REPORT.warn(this,
                                                   inReport);
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Adding {} to order history",
                               inReport);
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
                    SLF4JLoggerProxy.debug(this,
                                           "Created new {} for actual order ID: {} because there was no order history for this actual order ID nor the original order ID: {}",
                                           history,
                                           actualOrderID,
                                           originalOrderID);
                } else {
                    // case #2 from above: add an index reference for the new actual order ID
                    orders.put(actualOrderID,
                               history);
                    SLF4JLoggerProxy.debug(this,
                                           "Using existing {} for actual order ID: {} because there was already history for original order ID: {}",
                                           history,
                                           actualOrderID,
                                           originalOrderID);
                }
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Selected order history {} based on actual orderID: {} from {}",
                                       history,
                                       actualOrderID,
                                       orders);
            }
            // add the report to the order history
            history.add(inReport);
            SLF4JLoggerProxy.debug(this,
                                   "Added {} to {}",
                                   inReport,
                                   history);
            // check to see if the report represents an open order
            if(inReport.getOrderStatus().isCancellable()) {
                // if a report is cancellable, at least by our current understanding, the report has to be an ExecutionReport (not an OrderCancelReject)
                if(inReport instanceof ExecutionReport) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} represents an open order ({}), updating live order list for {}", //$NON-NLS-1$
                                           inReport.getOrderID(),
                                           inReport.getOrderStatus(),
                                           history);
                    openOrders.put(inReport.getOrderID(),
                                   (ExecutionReport)inReport);
                }
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "{} represents a closed order ({}) updating live order list for {}", //$NON-NLS-1$
                                       inReport.getOrderID(),
                                       inReport.getOrderStatus(),
                                       history);
                openOrders.remove(inReport.getOrderID());
            }
            if(inReport.getOriginalOrderID() != null) {
                SLF4JLoggerProxy.debug(this,
                                       "{} replaces {}, updating live order list", //$NON-NLS-1$
                                       inReport.getOrderID(),
                                       inReport.getOriginalOrderID());
                openOrders.remove(inReport.getOriginalOrderID());
            }
            if(SLF4JLoggerProxy.isTraceEnabled(this)) {
                SLF4JLoggerProxy.trace(this,
                                       display());
            }
        }
        synchronized(this) {
            this.notifyAll();
        }
    }
    /**
     * Displays the current order history in a readable format.
     * 
     * <p>This operation may be very expensive.
     *
     * @return a <code>String</code> value
     */
    public String display()
    {
        synchronized(orders) {
            StringBuffer output = new StringBuffer();
            output.append(nl).append("Order History as of ").append(new Date()).append(nl); //$NON-NLS-1$
            Table latestReportTable = new Table(10,
                                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                                ShownBorders.ALL,
                                                false);
            latestReportTable.addCell("OrderID", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Status", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("SendingTime", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("OrderChain", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Side", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Quantity", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Symbol", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Type", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Price", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Text", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            Set<OrderID> handledOrders = new HashSet<OrderID>();
            for(OrderHistory order : orders.values()) {
                ReportBase report = order.getLatestReport();
                if(report != null &&
                   !handledOrders.contains(report.getOrderID())) {
                    latestReportTable.addCell(order.getLatestReport().getOrderID().getValue());
                    latestReportTable.addCell(order.getLatestReport().getOrderStatus().name());
                    latestReportTable.addCell(DateUtils.dateToString(order.getLatestReport().getSendingTime()));
                    latestReportTable.addCell(order.getOrderIdChain().toString());
                    latestReportTable.addCell(report instanceof ExecutionReport ? ((ExecutionReport)report).getSide().name() : none);
                    latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getOrderQuantity()) : none);
                    latestReportTable.addCell(report instanceof ExecutionReport ? ((ExecutionReport)report).getInstrument().getSymbol() : none);
                    latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getOrderType()) : none);
                    latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getPrice()) : none);
                    latestReportTable.addCell(order.getLatestReport().getText());
                    handledOrders.add(report.getOrderID());
                }
            }
            output.append(nl);
            for(String line : latestReportTable.renderAsStringArray()) {
                output.append(line).append(nl);
            }
            output.append(nl).append("Open Orders").append(nl); //$NON-NLS-1$
            latestReportTable = new Table(9,
                                          BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                          ShownBorders.ALL,
                                          false);
            latestReportTable.addCell("OrderID", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Status", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("SendingTime", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Side", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Quantity", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Symbol", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Type", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Price", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            latestReportTable.addCell("Text", //$NON-NLS-1$
                                      headerStyle,
                                      1);
            for(ReportBase report : openOrders.values()) {
                latestReportTable.addCell(report.getOrderID().getValue());
                latestReportTable.addCell(report.getOrderStatus().name());
                latestReportTable.addCell(DateUtils.dateToString(report.getSendingTime()));
                latestReportTable.addCell(report instanceof ExecutionReport ? ((ExecutionReport)report).getSide().name() : none);
                latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getOrderQuantity()) : none);
                latestReportTable.addCell(report instanceof ExecutionReport ? ((ExecutionReport)report).getInstrument().getSymbol() : none);
                latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getOrderType()) : none);
                latestReportTable.addCell(report instanceof ExecutionReport ? String.valueOf(((ExecutionReport)report).getPrice()) : none);
                latestReportTable.addCell(report.getText());
            }
            output.append(nl);
            for(String line : latestReportTable.renderAsStringArray()) {
                output.append(line).append(nl);
            }
            return output.toString();
        }
    }
    /**
     * Gets the <code>ReportBase</code> values for the given <code>OrderID</code>.
     * 
     * <p>The <code>ReportBase</code> collection returned is sorted from newest to oldest.
     * 
     * <p>The returned <code>Deque</code> does not change and will not reflect future changes.
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
        if(inOrderId == null) {
            throw new NullPointerException();
        }
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderId);
            if(history == null) {
                return NO_ORDER_HISTORY;
            }
            return history.getOrderHistory();
        }
    }
    /**
     * Gets the open orders.
     * 
     * <p>The collection returned by this operation will reflect changes to the underlying order history.
     * 
     * @return a <code>Map&lt;OrderID,ExecutionReport&gt;</code> value
     */
    public Map<OrderID,ExecutionReport> getOpenOrders()
    {
        return Collections.unmodifiableMap(openOrders);
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
            openOrders.clear();
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
                    SLF4JLoggerProxy.debug(this,
                                           "Clearing history for {}", //$NON-NLS-1$
                                           orderID);
                    orders.remove(orderID);
                }
                history.clear();
            }
            if(inOrderId != null) {
                openOrders.remove(inOrderId);
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
     * @since 2.1.4
     */
    @NotThreadSafe
    @ClassVersion("$Id$")
    private static class OrderHistory
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("OrderHistory [").append(latestReport == null ? "none" : latestReport.getOrderID()).append("]");
            return builder.toString();
        }
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
    private final Map<OrderID,OrderHistory> orders = new LinkedHashMap<OrderID,OrderHistory>();
    /**
     * collection containing only the open orders
     */
    @GuardedBy("orders")
    private final Map<OrderID,ExecutionReport> openOrders = new ConcurrentHashMap<OrderID,ExecutionReport>();
    /**
     * sentinel collection used to indicate there is no order chain for a given order ID
     */
    private static final Set<OrderID> NO_ORDER_CHAIN = Collections.emptySet();
    /**
     * sentinel collection used to indicate there is no order history for a given order ID
     */
    private static final Deque<ReportBase> NO_ORDER_HISTORY = new UnmodifiableDeque<ReportBase>(new LinkedList<ReportBase>());
    /**
     * constant used to separate lines in status display
     */
    private static final String nl = System.getProperty("line.separator"); //$NON-NLS-1$
    /**
     * the style used for display tables
     */
    private static final CellStyle headerStyle = new CellStyle(HorizontalAlign.center);
    /**
     * display constant used to represent the lack of data
     */
    private static final String none = "---"; //$NON-NLS-1$
}

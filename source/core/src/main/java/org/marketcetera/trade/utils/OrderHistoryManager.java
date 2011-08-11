package org.marketcetera.trade.utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderID;
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
     * Create a new OrderHistoryManager instance.
     */
    public OrderHistoryManager()
    {
        this(null);
    }
    /**
     * Create a new OrderHistoryManager instance.
     *
     * @param inReportComparator a <code>Comparator&lt;MetaReport&gt;</code> value or <code>null</code>
     */
    public OrderHistoryManager(Comparator<MetaReport> inReportComparator)
    {
        reportComparator = inReportComparator;
    }
    /**
     * Gets the latest <code>ExecutionReport</code> for the given <code>OrderID</code>.
     *
     * <p>The given <code>OrderID</code> may correspond to either the
     * actual order ID or order ID of a replaced order in the same order chain.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>ExecutionReport</code> or <code>null</code> if there is no known status for the given <code>OrderID</code>
     */
    public ExecutionReport getLatestReportFor(OrderID inOrderID)
    {
        SLF4JLoggerProxy.debug(OrderHistoryManager.class,
                               "Searching order tracker for {}",
                               inOrderID);
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderID);
            if(history != null) {
                return history.getLatestReport();
            }
            SLF4JLoggerProxy.debug(OrderHistoryManager.class,
                                   "No history for {}",
                                   inOrderID);
            return null;
        }
    }
    /**
     * Adds the given <code>ExecutionReport</code> to the order history.
     *
     * @param inReport an <code>ExecutionReport</code> value
     */
    public void add(ExecutionReport inReport)
    {
        synchronized(orders) {
            OrderID actualOrderID = inReport.getOrderID();
            OrderID originalOrderID = inReport.getOriginalOrderID();
            // find the order history for this ER
            // first, look for a match of the actual order ID (simple, non-replace order case)
            OrderHistory history = orders.get(actualOrderID);
            if(history == null) {
                // ok, no order history for the actual order ID. this is caused by one of two things:
                //  1/ This is the first time we've seen anything in this chain
                //  2/ The ER is a replace order and we should search using the originalOrderID
                history = orders.get(originalOrderID);
                if(history == null) {
                    // now we know this is case #1 from above: create a new order history and add it
                    history = new OrderHistory(reportComparator);
                    // index the new history using the actual order ID
                    orders.put(actualOrderID,
                               history);
                } else {
                    // case #2 from above: add an index reference for the new actual order ID
                    orders.put(actualOrderID,
                               history);
                }
            }
            // add the ER to the order history
            history.add(inReport);
        }
    }
    /**
     * Gets the <code>ExecutionReport</code> values for the given <code>OrderID</code>.
     * 
     * <p>The <code>ExecutionReport</code> collection returned will be sorted in the order
     * determined by the <code>Comparator</code> passed to this object upon construction. If no
     * <code>Comparator</code> was specified, the default sort will be used: newest to oldest.
     * 
     * <p>Modifying the returned collection will have no effect on the underlying order history. The
     * returned collection is a static snapshot at a particular point in time: it will not be updated
     * as the underlying order history changes.
     * 
     * <p>The given <code>OrderID</code> may be either an order ID or an original order ID. The reports
     * returned will be the same in either case.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>Collection&lt;ExecutionReport&gt;</code> value which may be empty
     */
    public Collection<ExecutionReport> getReportHistoryFor(OrderID inOrderId)
    {
        synchronized(orders) {
            OrderHistory history = orders.get(inOrderId);
            if(history == null) {
                return Collections.emptyList(); 
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
            orders.clear();
        }
    }
    /**
     * Clears the order history values for the given <code>OrderID</code> if any.
     * 
     * <p>This method has no effect if there is no stored order history for the
     * given <code>OrderID</code>.
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
                                           "Clearing history for {}",
                                           orderID);
                    orders.remove(orderID);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderHistoryManager with history for: ").append(orders.keySet());
        return builder.toString();
    }
    /**
     * Tracks an <code>ExecutionReport</code> and some meta data about it that provides
     * context about when the report was received.
     * 
     * <p>Note that the <a href="http://download.oracle.com/javase/tutorial/collections/interfaces/order.html">natural ordering</a>
     * of this object is inverted: newer <code>ExecutionReport</code> values are ranked lower than older values.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    public final static class MetaReport
            implements Comparable<MetaReport>
    {
        /**
         * Get the report value.
         *
         * @return an <code>ExecutionReport</code> value
         */
        public ExecutionReport getReport()
        {
            return report;
        }
        /**
         * Get the sequence value.
         *
         * @return a <code>Long</code> value
         */
        public Long getSequence()
        {
            return sequence;
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MetaReport inO)
        {
            // the contract of MetaReport is that the reports want to be in reverse order
            // all we have to do, then, is to reverse the comparison - compare this to the passed obj
            //  instead of the passed obj to this
            if(inO.getReport().getTransactTime() != null) {
                int result = inO.getReport().getTransactTime().compareTo(report.getTransactTime());
                if(result != 0) {
                    return result;
                }
            }
            return inO.getSequence().compareTo(sequence);
            
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MetaReport)) {
                return false;
            }
            MetaReport other = (MetaReport) obj;
            if (sequence == null) {
                if (other.sequence != null) {
                    return false;
                }
            } else if (!sequence.equals(other.sequence)) {
                return false;
            }
            return true;
        }
        /**
         * Create a new MetaReport instance.
         *
         * @param inReport an <code>ExecutionReport</code> value
         */
        private MetaReport(ExecutionReport inReport)
        {
            report = inReport;
        }
        /**
         * execution report value
         */
        private final ExecutionReport report;
        /**
         * sequence number indicating when this report was received in relative relation to others in the same process
         */
        private final Long sequence = counter.incrementAndGet();
        /**
         * counter used to track all execution reports in the same process
         */
        private static final AtomicLong counter = new AtomicLong(0);
    }
    /**
     * Tracks order history for a single order.
     * 
     * <p>Instantiate this object and add <code>ExecutionReport</code> objects for
     * this same order chain. No validation is done to make sure that incoming
     * <code>ExecutionReport</code> objects are truly part of the order chain: the
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
         * Create a new OrderHistory instance.
         *
         * @param inComparator a <code>Comparator&lt;MetaReport&gt;</code> value or <code>null</code>
         */
        private OrderHistory(Comparator<MetaReport> inComparator)
        {
            if(inComparator == null) {
                orderHistory = new TreeSet<MetaReport>();
            } else {
                orderHistory = new TreeSet<MetaReport>(inComparator);
            }
        }
        /**
         * Adds the given <code>ExecutionReport</code> to the order history.
         * 
         * @param inReport an <code>ExecutionReport</code> value
         */
        private void add(ExecutionReport inReport)
        {
            orderHistory.add(new MetaReport(inReport));
            orderIdChain.add(inReport.getOrderID());
            latestReport = inReport;
        }
        /**
         * Gets the order history.
         * 
         * <p>The reports returned are sorted using the <code>Comparator</code> established
         * for this object. The default sort order is newest to oldest.
         * 
         * <p>Modifying the returned collection will have no effect on the underlying order
         * history. Changes to the underlying order history will not be reflected in the
         * returned collection.
         *
         * @return a <code>Collection&lt;ExecutionReport&gt;</code> value
         */
        private Collection<ExecutionReport> getOrderHistory()
        {
            Collection<ExecutionReport> reports = new ArrayList<ExecutionReport>();
            for(MetaReport report : orderHistory) {
                reports.add(report.getReport());
            }
            return reports;
        }
        /**
         * Get the latestReport value.
         *
         * @return an <code>ExecutionReport</code> value
         */
        private ExecutionReport getLatestReport()
        {
            return latestReport;
        }
        /**
         * 
         *
         *
         * @return
         */
        private Set<OrderID> getOrderIdChain()
        {
            return Collections.unmodifiableSet(orderIdChain);
        }
        /**
         * sorted order history
         */
        private final Set<MetaReport> orderHistory;
        /**
         * order IDs in the order chain in the order they occurred
         */
        private final Set<OrderID> orderIdChain = new LinkedHashSet<OrderID>();
        /**
         * most recent <code>ExecutionReport</code>
         */
        private volatile ExecutionReport latestReport;
    }
    /**
     * comparator used to rank <code>MetaReport</code> objects in this order history, may be <code>null</code>
     */
    private final Comparator<MetaReport> reportComparator;
    /**
     * order history objects indexed by actual order ID
     */
    @GuardedBy("orders")
    private final Map<OrderID,OrderHistory> orders = new HashMap<OrderID,OrderHistory>();
}

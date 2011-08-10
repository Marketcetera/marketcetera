package org.marketcetera.trade.utils;

import java.util.*;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Tracks the status of orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class OrderTracker
{
    /**
     * Gets the latest <code>ExecutionReport</code> for the given <code>OrderID</code>.
     *
     * <p>The given <code>OrderID</code> may correspond to either the
     * actual order ID or the original order ID of an order.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>ExecutionReport</code> or <code>null</code> if there is no known status for the given <code>OrderID</code>
     */
    public ExecutionReport getLatestReportFor(OrderID inOrderID)
    {
        SLF4JLoggerProxy.debug(OrderTracker.class,
                               "Searching order tracker for {}",
                               inOrderID);
        synchronized(reportsByOrderId) {
            Deque<ExecutionReport> reports = reportsByOrderId.get(inOrderID);
            if(reports != null &&
               !reports.isEmpty()) {
                ExecutionReport report = reports.getLast();
                SLF4JLoggerProxy.debug(OrderTracker.class,
                                       "Found {} report(s) for {}, returning {}",
                                       reports.size(),
                                       inOrderID,
                                       report);
                return report;
            }
            SLF4JLoggerProxy.debug(OrderTracker.class,
                                   "No reports for {}",
                                   inOrderID);
            return null;
        }
    }
    /**
     * Adds the given <code>ExecutionReport</code> to this <code>OrderTracker</code>.
     *
     * @param inReport an <code>ExecutionReport</code> value
     */
    public void add(ExecutionReport inReport)
    {
        OrderID actualOrderID = inReport.getOrderID();
        OrderID originalOrderID = inReport.getOriginalOrderID();
        Deque<ExecutionReport> reports;
        synchronized(reportsByOrderId) {
            if(actualOrderID == null) {
                SLF4JLoggerProxy.debug(OrderTracker.class,
                                       "Cowardly refusing to process a null actual order ID on {}",
                                       inReport);
            } else {
                reports = reportsByOrderId.get(actualOrderID);
                if(reports == null) {
                    reports = new LinkedList<ExecutionReport>();
                    reportsByOrderId.put(actualOrderID,
                                         reports);
                }
                reports.addLast(inReport);
            }
            if(originalOrderID == null) {
                SLF4JLoggerProxy.debug(OrderTracker.class,
                                       "Cowardly refusing to process a null original order ID on {}",
                                       inReport);
            } else {
                reports = reportsByOrderId.get(originalOrderID);
                if(reports == null) {
                    reports = new LinkedList<ExecutionReport>();
                    reportsByOrderId.put(originalOrderID,
                                         reports);
                }
                reports.addLast(inReport);
            }
        }
    }
    /**
     * Gets the <code>ExecutionReport</code> values for the given <code>OrderID</code> in the
     * order they were received.
     * 
     * <p>The given <code>OrderID</code> may be either an order ID or an original order ID. The reports
     * returned will be the same in either case.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return a <code>Queue&lt;ExecutionReport&gt;</code> value which may be empty
     */
    public Queue<ExecutionReport> getReportHistoryFor(OrderID inOrderId)
    {
        synchronized(reportsByOrderId) {
            Deque<ExecutionReport> reports = reportsByOrderId.get(inOrderId);
            if(reports != null) {
                return Collections.asLifoQueue(reports);
            }
            return new LinkedList<ExecutionReport>();
        }
    }
    /**
     * Gets all <code>OrderID</code> values for which a status exists.
     *
     * @return a <code>Set&lt;OrderID&gt;</code> value
     */
    public Set<OrderID> getOrderIds()
    {
        synchronized(reportsByOrderId) {
            return Collections.unmodifiableSet(reportsByOrderId.keySet());
        }
    }
    /**
     * Clears all stored <code>ExecutionReport</code> values.
     */
    public void clear()
    {
        synchronized(reportsByOrderId) {
            reportsByOrderId.clear();
        }
    }
    /**
     * Clears the stored <code>ExecutionReport</code> values for the given <code>OrderID</code> if any.
     * 
     * <p>This method has no effect if there are no stored <code>ExecutionReport</code> values for the
     * given <code>OrderID</code>.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    public void clear(OrderID inOrderId)
    {
        synchronized(reportsByOrderId) {
            reportsByOrderId.remove(inOrderId);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderTracker: [").append(reportsByOrderId.size()).append(" reports]");
        return builder.toString();
    }
    /**
     * stores <code>ExecutionReport</code> values by <code>OrderID</code>
     */
    @GuardedBy("reportsByOrderId")
    private final Map<OrderID,Deque<ExecutionReport>> reportsByOrderId = new HashMap<OrderID,Deque<ExecutionReport>>();
}

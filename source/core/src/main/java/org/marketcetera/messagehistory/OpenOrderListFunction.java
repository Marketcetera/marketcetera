package org.marketcetera.messagehistory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.FunctionList.Function;

/* $License$ */

/**
 * Function that chooses the report holder representing the status of an open order. Its input is a
 * list of the entire order chain for a given order id.
 * 
 * The result is typically the latest execution report, but there are some edge cases. For example,
 * broker reports are favored over server acks. Also, obsolete orders in the chain are not
 * considered, e.g. if a cancel-replace was rejected.
 * 
 * A null report will be returned if the order chain is completely obsolete (canceled, filled,
 * etc.).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
final class OpenOrderListFunction implements Function<List<ReportHolder>, ReportHolder> {

    private static final Comparator<ReportHolder> sComparator = new Comparator<ReportHolder>() {

        @Override
        public int compare(ReportHolder o1, ReportHolder o2) {
            ReportBase r1 = o1.getReport();
            ReportBase r2 = o2.getReport();
            // sort by descending report id (sequence number)
            return new CompareToBuilder().append(r2.getReportID().longValue(),
                    r1.getReportID().longValue()).toComparison();
        }

    };

    @Override
    public ReportHolder evaluate(List<ReportHolder> sourceValue) {
        Set<OrderID> obsolete = new HashSet<OrderID>();
        // out is a placeholder for a server ack if it is found
        ReportHolder out = null;
        ReportHolder[] reversedHolders = sourceValue.toArray(new ReportHolder[sourceValue.size()]);
        // sort the list to guarantee the result is correct
        // NOTE: it may be possible to accomplish the same thing without sorting
        // for better performance
        Arrays.sort(reversedHolders, sComparator);
        // this is worst case O(n) if there are tons of OrderCancelRejects, or the latest execution
        // report is a server ack, e.g. PENDING_NEW.
        for (int i = 0; i < reversedHolders.length; i++) {
            ReportHolder reportHolder = reversedHolders[i];
            ReportBase report = reportHolder.getReport();
            OrderID orderId = report.getOrderID();
            // can't work with a report without an id
            if (orderId == null) {
                continue;
            }
            if (report instanceof OrderCancelReject) {
                // the cancel or cancel-replace was rejected
                obsolete.add(orderId);
            } else if (report instanceof ExecutionReport) {
                ExecutionReport ereport = (ExecutionReport) report;
                if (ereport.getOrderStatus() == OrderStatus.Filled
                        || ereport.getOrderStatus() == OrderStatus.Canceled
                        || ereport.getOrderStatus() == OrderStatus.Rejected) {
                    // order has been filled, canceled, or rejected, whole chain is obsolete
                    return null;
                } else if (ereport.isCancelable()) {
                    if (out != null) {
                        // we have a placeholder already, only override it if we find a
                        // broker report with the same order id, e.g. the NEW for a PENDING_NEW
                        if (ereport.getOriginator() == Originator.Broker
                                && out.getReport().getOrderID().equals(orderId)) {
                            return reportHolder;
                        }
                    } else if (!obsolete.contains(orderId)) {
                        // this is the latest non-obsolete execution report, return it if it
                        // is from the broker, otherwise set the placeholder and keep iterating
                        // to find a broker report if one exists
                        if (ereport.getOriginator() == Originator.Broker) {
                            return reportHolder;
                        } else {
                            out = reportHolder;
                        }
                    }
                }
            }
        }
        // out can be null, in which case no suitable report was found.
        // If it is non null, it means the latest report was a server ack and no broker
        // report of the same id was found.
        return out;
    }
}

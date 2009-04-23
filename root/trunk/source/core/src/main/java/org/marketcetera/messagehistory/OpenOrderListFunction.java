package org.marketcetera.messagehistory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
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
public class OpenOrderListFunction implements Function<List<ReportHolder>, ReportHolder> {

    private static final EnumMap<Originator, Integer> sOriginatorOrder;

    static {
        sOriginatorOrder = new EnumMap<Originator, Integer>(Originator.class);
        sOriginatorOrder.put(Originator.Broker, 0);
        sOriginatorOrder.put(Originator.Server, 1);
    }

    private final Comparator<ReportHolder> mComparator = new Comparator<ReportHolder>() {

        @Override
        public int compare(ReportHolder o1, ReportHolder o2) {
            ReportBase r1 = o1.getReport();
            ReportBase r2 = o2.getReport();
            // handle null order ids
            OrderID id1 = r1.getOrderID();
            OrderID id2 = r2.getOrderID();
            // sort by descending order id, broker before server, then descending report id
            // (sequence number)
            return new CompareToBuilder().append(id2 == null ? null : id2.getValue(),
                    id1 == null ? null : id1.getValue()).append(
                    sOriginatorOrder.get(r1.getOriginator()),
                    sOriginatorOrder.get(r2.getOriginator())).append(r2.getReportID().longValue(),
                    r1.getReportID().longValue()).toComparison();
        }

    };

    @Override
    public ReportHolder evaluate(List<ReportHolder> sourceValue) {
        Set<OrderID> obsolete = new HashSet<OrderID>();
        ReportHolder[] reversedHolders = sourceValue.toArray(new ReportHolder[sourceValue.size()]);
        // sort the list to guarantee the result is correct
        // NOTE: it may be possible to accomplish the same thing without sorting for better performance
        Arrays.sort(reversedHolders, mComparator);
        // this is worst case O(n) if there are tons of OrderCancelRejects, but typically will find
        // the right report in the first few iterations
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
                } else if (ereport.isCancelable() && !obsolete.contains(orderId)) {
                    return reportHolder;
                }
            }
        }
        return null;
    }
}

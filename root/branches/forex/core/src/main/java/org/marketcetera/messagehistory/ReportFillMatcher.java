package org.marketcetera.messagehistory;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * Matches Fill Reports that have more than zero shares traded.
 * 
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class ReportFillMatcher implements Matcher<ReportHolder> {
    public boolean matches(ReportHolder holder) {
        ReportBase report = holder.getReport();
        if (report instanceof ExecutionReport) {
            ExecutionReport exec = (ExecutionReport) report;
            // disregard ERs that come from the ORS
            if(exec.getOriginator() == Originator.Server) {
                return false;
            }
            // disregard ERs that don't have a lastQuantity value
            if(exec.getLastQuantity() == null) {
                return false;
            }
            // disregard ERs that have a zero lastQuantity (negatives are ok for combo orders, for example)
            if(exec.getLastQuantity().equals(BigDecimal.ZERO)) {
                return false;
            }
            // we now know that the ER came from the broker and it has a non-zero lastQuantity
            // next, check the status (method varies by FIX version)
            boolean isFill = false;
            ExecutionType execType = exec.getExecutionType();
            if(execType == null) {
                // this is true for FIX 4.0, use order status instead
                OrderStatus orderStatus = report.getOrderStatus();
                // note that this could be null if the ER doesn't have an orderStatus value which would
                //  make this report be skipped, which is fine
                isFill = ORD_STATUS_FILLS.contains(orderStatus);
            } else {
                isFill = EXEC_TYPE_FILLS.contains(execType);
            }
            return isFill;
        }
        return false;
    }
    /**
     * execution type status values that indicate a fill
     */
    private static final Set<ExecutionType> EXEC_TYPE_FILLS = EnumSet.of(ExecutionType.Fill,ExecutionType.PartialFill);
    /**
     * order status values that indicate a fill
     */
    private static final Set<OrderStatus> ORD_STATUS_FILLS = EnumSet.of(OrderStatus.Filled,OrderStatus.PartiallyFilled);
}
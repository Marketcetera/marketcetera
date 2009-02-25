package org.marketcetera.messagehistory;

import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.matchers.Matcher;

import java.math.BigDecimal;

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
        OrderStatus orderStatus = report.getOrderStatus();
        if (report instanceof ExecutionReport) {
            ExecutionReport exec = (ExecutionReport) report;
            return ((orderStatus == OrderStatus.PartiallyFilled
                    || orderStatus == OrderStatus.Filled || orderStatus == OrderStatus.PendingCancel)
                    && exec.getLastQuantity() != null && exec.getLastQuantity()
                    .compareTo(BigDecimal.ZERO) > 0);
        }
        return false;
    }
}
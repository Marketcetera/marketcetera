package org.marketcetera.core.messagehistory;

import java.math.BigDecimal;

import ca.odell.glazedlists.matchers.Matcher;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.trade.ExecutionReport;
import org.marketcetera.core.trade.OrderStatus;
import org.marketcetera.core.trade.ReportBase;

/* $License$ */

/**
 * Matches Fill Reports that have more than zero shares traded.
 * 
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ReportFillMatcher.java 82326 2012-04-10 16:27:07Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ReportFillMatcher.java 82326 2012-04-10 16:27:07Z colin $")
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
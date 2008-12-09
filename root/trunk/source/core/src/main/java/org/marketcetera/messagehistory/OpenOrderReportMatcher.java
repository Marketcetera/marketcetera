package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ReportBase;

import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */
/**
 * Matches reports that represent open orders.
 * 
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OpenOrderReportMatcher implements Matcher<ReportHolder> {

    public boolean matches(ReportHolder item) {
        ReportBase report = ((ReportHolder) item).getReport();
        if (report instanceof ExecutionReport) {
            return ((ExecutionReport) report).isCancelable();
        }
        return false;
    }

}
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
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OpenOrderReportMatcher implements Matcher<ReportHolder> {

	public boolean matches(ReportHolder item) {
		if (item instanceof ReportHolder) {
			ReportBase report = ((ReportHolder) item).getReport();
			if (report instanceof ExecutionReport){
                return ((ExecutionReport) report).isCancelable();
			}
		}
		return false;
	}

}
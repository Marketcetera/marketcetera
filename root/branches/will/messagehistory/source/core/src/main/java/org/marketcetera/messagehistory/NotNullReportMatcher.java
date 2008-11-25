/**
 * 
 */
package org.marketcetera.messagehistory;

import ca.odell.glazedlists.matchers.Matcher;
/* $License$ */
/**
 * Keeps track of Trading Report History for photon.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@org.marketcetera.util.misc.ClassVersion("$Id$") //$NON-NLS-1$
public final class NotNullReportMatcher implements Matcher<ReportHolder> {
	public boolean matches(ReportHolder arg0) {
		return arg0 != null;
	}
}
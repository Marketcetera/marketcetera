/**
 * 
 */
package org.marketcetera.core.messagehistory;

import ca.odell.glazedlists.matchers.Matcher;
/* $License$ */
/**
 * Matches all non-null {@link ReportHolder} objects.
 *
 * @version $Id: NotNullReportMatcher.java 82326 2012-04-10 16:27:07Z colin $
 * @since 1.0.0
 */
public final class NotNullReportMatcher implements Matcher<ReportHolder> {
    public boolean matches(ReportHolder arg0) {
        return arg0 != null;
    }
}
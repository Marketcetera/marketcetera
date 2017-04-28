package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides reports to listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportPublisher
{
    /**
     * Adds a report listener.
     * 
     * <p>The report listener receives all the reports sent out by the server.</p>
     * 
     * <p>If the same listener is added more than once, it will receive notifications as many times as it's been added.</p>
     * 
     * <p>The listeners are notified in the reverse order of their addition.</p>
     *
     * @param inListener a <code>ReportListener</code> value holding the listener instance that should be supplied the reports.
     */
    public void addReportListener(ReportListener inListener);
    /**
     * Removes a report listener that was previously added via {@link #addReportListener(ReportListener)}.
     * 
     * <p>If the listener was added more than once, only its most recently added occurrence will be removed. 
     *
     * @param inListener a <code>ReportListener</code> containing the listener instance that should no longer be receiving the reports.
     */
    public void removeReportListener(ReportListener inListener);
}

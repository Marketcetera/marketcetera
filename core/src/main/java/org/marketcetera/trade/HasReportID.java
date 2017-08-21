package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementer has a {@link ReportID}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasReportID
{
    /**
     * Get the Report ID value.
     *
     * @return a <code>ReportID</code> value
     */
    ReportID getReportID();
}

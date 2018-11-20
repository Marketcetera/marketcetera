package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementer has a {@link ReportID} value that can be changed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMutableReportID
        extends HasReportID
{
    /**
     * Set the Report ID value.
     *
     * @param inReportId a <code>ReportID</code> value
     */
    void setReportID(ReportID inReportId);
}

package org.marketcetera.systemmodel.persistence;

import org.marketcetera.systemmodel.Report;
import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportDao
{
    /**
     * 
     *
     *
     * @param inReport
     */
    public void write(Report inReport);
    /**
     * 
     *
     *
     * @param inReportSummary
     */
    public void write(ReportSummary inReportSummary);
}

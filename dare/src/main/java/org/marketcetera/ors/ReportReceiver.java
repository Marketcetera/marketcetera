package org.marketcetera.ors;

import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Receives reports and processes them according to the implementer's nature.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
public interface ReportReceiver
{
    /**
     * Adds the given <code>ReportBase</code> object.
     *
     * @param inReport a <code>ReportBase</code> value
     * @throws RuntimeException if the report could not be added 
     */
    public void addReport(ReportBase inReport);
    /**
     * Deletes the given <code>ReportBase</code> object.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public void deleteReport(ReportBase inReport);
}

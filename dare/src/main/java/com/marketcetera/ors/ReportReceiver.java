package com.marketcetera.ors;

import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Receives reports and processes them according to the implementer's nature.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReportReceiver.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: ReportReceiver.java 16522 2014-12-31 16:33:08Z colin $")
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

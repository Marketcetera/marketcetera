package org.marketcetera.ors;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Receives reports and processes them according to the implementer's nature.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportReceiver
{
    /**
     * Adds the given <code>ExecutionReport</code> object.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @throws RuntimeException if the report could not be added 
     */
    public void addReport(ExecutionReport inReport);
    /**
     * Deletes the given <code>ExecutionReport</code> object.
     *
     * @param inReport an <code>ExecutionReport</code> value
     */
    public void deleteReport(ExecutionReport inReport);
}

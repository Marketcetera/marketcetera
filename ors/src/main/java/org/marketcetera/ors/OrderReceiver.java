package org.marketcetera.ors;

import org.marketcetera.trade.ExecutionReport;
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
public interface OrderReceiver
{
    /**
     * 
     *
     *
     * @param inReport
     */
    public void addReport(ExecutionReport inReport);
    /**
     * 
     *
     *
     * @param inReport
     */
    public void deleteReport(ExecutionReport inReport);
}

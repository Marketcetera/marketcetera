package org.marketcetera.client;

import org.marketcetera.trade.FIXResponse;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ExtendedReportListener
        extends ReportListener
{
    /**
     * 
     *
     *
     * @param inReport
     */
    void receiveFixResponse(FIXResponse inReport);
}

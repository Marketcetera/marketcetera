package org.marketcetera.marketdata.yahoo;

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
interface YahooFeedServices
{
    /**
     * 
     *
     *
     * @param inHandle
     * @param inData
     */
    void doDataReceived(String inHandle,
                        Object inData);
    /**
     * 
     *
     *
     * @return
     */
    int getRefreshInterval();
}

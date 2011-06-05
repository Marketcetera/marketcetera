package org.marketcetera.marketdata.yahoo;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
interface YahooClientFactory
{
    /**
     * 
     *
     *
     * @return
     */
    YahooClient getClient(YahooFeedServices inFeedServices);
}

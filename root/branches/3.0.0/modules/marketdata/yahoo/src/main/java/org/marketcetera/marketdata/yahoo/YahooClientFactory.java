package org.marketcetera.marketdata.yahoo;

/* $License$ */

/**
 * Constructs {@link YahooClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooClientFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
interface YahooClientFactory
{
    /**
     * Constructs a <code>YahooClient</code> object. 
     *
     * @return a <code>YahooClient</code> value
     */
    YahooClient getClient(YahooFeedServices inFeedServices);
}

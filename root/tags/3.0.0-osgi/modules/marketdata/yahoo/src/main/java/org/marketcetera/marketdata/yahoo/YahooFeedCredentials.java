package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedURLCredentials;
import org.marketcetera.core.marketdata.FeedException;

/* $License$ */

/**
 * Credentials for the Yahoo market data feed.
 *
 * @version $Id: YahooFeedCredentials.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
public class YahooFeedCredentials
        extends AbstractMarketDataFeedURLCredentials
{
    /**
     * Create a new YahooFeedCredentials instance.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException if the credentials cannot be constructed
     */
    YahooFeedCredentials(String inURL)
            throws FeedException
    {
        super(inURL);
    }
}

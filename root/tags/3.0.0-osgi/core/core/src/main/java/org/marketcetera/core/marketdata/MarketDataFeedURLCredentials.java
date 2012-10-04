package org.marketcetera.core.marketdata;

/* $License$ */

/**
 * Credentials for establishing a connection to a {@link MarketDataFeed} that requires a URL.
 *
 * @version $Id: MarketDataFeedURLCredentials.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.1.0
 */
public interface MarketDataFeedURLCredentials
        extends MarketDataFeedCredentials
{
    /**
     * Returns a URI describing how to connect to a data feed.
     *
     * @return a <code>String</code> value
     */
    public String getURL();
}

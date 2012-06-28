package org.marketcetera.marketdata;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Credentials for establishing a connection to a {@link MarketDataFeed} that requires a URL.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataFeedURLCredentials.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.1.0
 */
@ClassVersion("$Id: MarketDataFeedURLCredentials.java 16063 2012-01-31 18:21:55Z colin $")
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

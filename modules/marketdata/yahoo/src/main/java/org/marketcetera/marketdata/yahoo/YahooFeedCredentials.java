package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedURLCredentials;
import org.marketcetera.core.marketdata.FeedException;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Credentials for the Yahoo market data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooFeedCredentials.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
@ClassVersion("$Id: YahooFeedCredentials.java 16063 2012-01-31 18:21:55Z colin $")
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

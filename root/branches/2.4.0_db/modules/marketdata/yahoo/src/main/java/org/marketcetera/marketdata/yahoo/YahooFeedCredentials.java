package org.marketcetera.marketdata.yahoo;

import org.marketcetera.marketdata.AbstractMarketDataFeedURLCredentials;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Credentials for the Yahoo market data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
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

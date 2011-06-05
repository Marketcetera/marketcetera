package org.marketcetera.marketdata.yahoo;

import org.marketcetera.marketdata.AbstractMarketDataFeedURLCredentials;
import org.marketcetera.marketdata.FeedException;
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
public class YahooFeedCredentials
        extends AbstractMarketDataFeedURLCredentials
{
    /**
     * Create a new YahooFeedCredentials instance.
     *
     * @param inURL a <code>String</code> value
     * @throws FeedException
     */
    YahooFeedCredentials(String inURL)
            throws FeedException
    {
        super(inURL);
    }
}

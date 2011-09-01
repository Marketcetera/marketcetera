package org.marketcetera.marketdata.yahoo;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Token which represents a market data request to the Yahoo market data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class YahooFeedToken
        extends AbstractMarketDataFeedToken<YahooFeed>
{
    /**
     * Create a new YahooFeedToken instance.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec</code> value
     * @param inFeed a <code>YahooFeed</code> value
     */
    YahooFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                   YahooFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
}

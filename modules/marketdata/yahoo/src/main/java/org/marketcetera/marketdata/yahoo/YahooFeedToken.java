package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.core.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 * Token which represents a market data request to the Yahoo market data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooFeedToken.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
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

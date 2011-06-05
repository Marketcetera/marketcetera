package org.marketcetera.marketdata.yahoo;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class YahooFeedToken
        extends AbstractMarketDataFeedToken<YahooFeed>
{

    /**
     * Create a new YahooFeedToken instance.
     *
     * @param inTokenSpec
     * @param inFeed
     */
    YahooFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                   YahooFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
}

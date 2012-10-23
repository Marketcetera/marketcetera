package org.marketcetera.marketdata.marketcetera;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.core.marketdata.MarketDataFeedToken;
import org.marketcetera.core.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 * {@link MarketDataFeedToken} implementation for {@link MarketceteraFeed}.
 *
 * @version $Id: MarketceteraFeedToken.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class MarketceteraFeedToken
    extends AbstractMarketDataFeedToken<MarketceteraFeed>
{
    /**
     * Gets a <code>MarketceteraFeedToken</code> value.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;MarketceteraFeedCredentials&gt;</code> value
     * @param inFeed a <code>MarketceteraFeed</code> value
     * @return a <code>MarketceteraFeedToken</code> value
     */
    static MarketceteraFeedToken getToken(MarketDataFeedTokenSpec inTokenSpec,
                                          MarketceteraFeed inFeed) 
    {
        return new MarketceteraFeedToken(inTokenSpec,
                                         inFeed);
    }
    /**
     * Create a new MarketceteraFeedToken instance.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;MarketceteraFeedCredentials&gt;</code> value
     * @param inFeed a <code>MarketceteraFeed</code> value
     */
    private MarketceteraFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                                  MarketceteraFeed inFeed)
    {
        super(inTokenSpec, 
              inFeed);
    }
}

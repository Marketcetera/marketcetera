package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.core.marketdata.MarketDataFeedTokenSpec;

/**
 * Token for {@link BogusFeed}.
 *
 * @version $Id: BogusFeedToken.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
public class BogusFeedToken
        extends AbstractMarketDataFeedToken<BogusFeed>
{
    static BogusFeedToken getToken(MarketDataFeedTokenSpec inTokenSpec,
                                   BogusFeed inFeed) 
    {
        return new BogusFeedToken(inTokenSpec,
                                  inFeed);
    }   
    /**
     * Create a new BogusFeedToken instance.
     */
    private BogusFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                           BogusFeed inFeed) 
    {
        super(inTokenSpec,
              inFeed);
    }
    public String toString()
    {
        return String.format("BogusFeedToken(%s)", //$NON-NLS-1$
                             getStatus());
    }
}

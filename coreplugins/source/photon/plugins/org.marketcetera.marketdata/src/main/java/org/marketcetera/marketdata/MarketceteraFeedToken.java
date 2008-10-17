/**
 * 
 */
package org.marketcetera.marketdata;

/**
 * @author colin
 *
 */
public class MarketceteraFeedToken
    extends AbstractMarketDataFeedToken<MarketceteraFeed,MarketceteraFeedCredentials>
{
    static MarketceteraFeedToken getToken(MarketDataFeedTokenSpec<MarketceteraFeedCredentials> inTokenSpec,
                                          MarketceteraFeed inFeed) 
    {
        return new MarketceteraFeedToken(inTokenSpec,
                                         inFeed);
    }   
    /**
     * 
     * @param inTokenSpec
     * @param inFeed
     */
    private MarketceteraFeedToken(MarketDataFeedTokenSpec<MarketceteraFeedCredentials> inTokenSpec,
                                  MarketceteraFeed inFeed)
    {
        super(inTokenSpec, 
              inFeed);
    }
}

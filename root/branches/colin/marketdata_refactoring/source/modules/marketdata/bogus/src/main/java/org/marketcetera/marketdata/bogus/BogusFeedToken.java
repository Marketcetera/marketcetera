package org.marketcetera.marketdata.bogus;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedToken.java 9456 2008-07-31 22:28:30Z klim $
 * @since 0.43-SNAPSHOT
 */
public class BogusFeedToken
        extends AbstractMarketDataFeedToken<BogusFeed,BogusFeedCredentials>
{
    static BogusFeedToken getToken(MarketDataFeedTokenSpec<BogusFeedCredentials> inTokenSpec,
                                   BogusFeed inFeed) 
    {
        return new BogusFeedToken(inTokenSpec,
                                  inFeed);
    }   
    /**
     * Create a new BogusFeedToken instance.
     */
    private BogusFeedToken(MarketDataFeedTokenSpec<BogusFeedCredentials> inTokenSpec,
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

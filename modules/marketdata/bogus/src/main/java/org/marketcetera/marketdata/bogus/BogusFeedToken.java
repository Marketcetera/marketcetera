package org.marketcetera.marketdata.bogus;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * Token for {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedToken.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusFeedToken.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
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

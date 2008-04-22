package org.marketcetera.marketdata;

/**
 * Credentials instance for <code>MarketceteraFeed</code>.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public class MarketceteraFeedCredentials
    extends AbstractMarketDataFeedCredentials
{
    /**
     * @param inURL
     * @throws FeedException
     */
    public MarketceteraFeedCredentials(String inURL) 
        throws FeedException
    {
        super(inURL);
    }
}

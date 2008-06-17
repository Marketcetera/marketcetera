package org.marketcetera.bogusfeed;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;

public class BogusFeedFactory 
    extends AbstractMarketDataFeedFactory<BogusFeed,BogusFeedCredentials> 
{
    private final static BogusFeedFactory sInstance = new BogusFeedFactory();
    public static BogusFeedFactory getInstance()
    {
        return sInstance;
    }
	public String getProviderName()
	{
		return "Marketcetera (Bogus)";
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    public BogusFeed getMarketDataFeed()
            throws MarketceteraException
    {
        return getMarketDataFeed(null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    public BogusFeed getMarketDataFeed(BogusFeedCredentials inCredentials)
            throws MarketceteraException
    {
        try {
            return BogusFeed.getInstance(getProviderName(),
                                         inCredentials);
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        }
    }
}

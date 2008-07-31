package org.marketcetera.bogusfeed;

import org.marketcetera.core.CoreException;
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
		return "Marketcetera (Bogus)"; //$NON-NLS-1$
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    public BogusFeed getMarketDataFeed()
            throws CoreException
    {
        return getMarketDataFeed(null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    public BogusFeed getMarketDataFeed(BogusFeedCredentials inCredentials)
            throws CoreException
    {
        try {
            return BogusFeed.getInstance(getProviderName(),
                                         inCredentials);
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        }
    }
}

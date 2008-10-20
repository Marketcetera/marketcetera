package org.marketcetera.marketdata.marketcetera;

import java.net.URISyntaxException;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;

public class MarketceteraFeedFactory 
    extends AbstractMarketDataFeedFactory<MarketceteraFeed,MarketceteraFeedCredentials> 
{
    private final static MarketceteraFeedFactory sInstance = new MarketceteraFeedFactory();
    public static MarketceteraFeedFactory getInstance()
    {
        return sInstance;
    }
	public String getProviderName() 
	{
		return "Marketcetera"; //$NON-NLS-1$
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    public MarketceteraFeed getMarketDataFeed() 
        throws CoreException
    {
        return getMarketDataFeed(null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    public MarketceteraFeed getMarketDataFeed(MarketceteraFeedCredentials inCredentials) 
        throws CoreException
    {
        try {
            return MarketceteraFeed.getInstance(getProviderName(),
                                                inCredentials);
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        } catch (URISyntaxException e) {
            throw new FeedException(e);
        }
    }
}

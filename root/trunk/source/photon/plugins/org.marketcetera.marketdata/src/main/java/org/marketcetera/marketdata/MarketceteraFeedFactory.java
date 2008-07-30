package org.marketcetera.marketdata;

import java.net.URISyntaxException;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;

public class MarketceteraFeedFactory 
    extends AbstractMarketDataFeedFactory<MarketceteraFeed,MarketceteraFeedCredentials> 
{
	public String[] getAllowedPropertyKeys() 
	{
		return new String [] {MarketceteraFeed.SETTING_SENDER_COMP_ID, MarketceteraFeed.SETTING_TARGET_COMP_ID };
	}
	public String getProviderName() 
	{
		return "Marketcetera"; //$NON-NLS-1$
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    public MarketceteraFeed getMarketDataFeed() 
        throws MarketceteraException
    {
        return getMarketDataFeed(null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    public MarketceteraFeed getMarketDataFeed(MarketceteraFeedCredentials inCredentials) 
        throws MarketceteraException
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

package org.marketcetera.marketdata.marketcetera;

import java.net.URISyntaxException;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.marketdata.FeedException;

/* $License$ */

/**
 * {@link MarketceteraFeed} constructor factory.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
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
    @Override
    public MarketceteraFeed getMarketDataFeed() 
        throws CoreException
    {
        try {
            return MarketceteraFeed.getInstance(getProviderName());
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        } catch (URISyntaxException e) {
            throw new FeedException(e);
        }
    }
}

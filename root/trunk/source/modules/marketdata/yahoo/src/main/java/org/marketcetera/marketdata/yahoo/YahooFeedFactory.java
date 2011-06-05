package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class YahooFeedFactory
        extends AbstractMarketDataFeedFactory<YahooFeed,YahooFeedCredentials>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    @Override
    public synchronized YahooFeed getMarketDataFeed()
            throws CoreException
    {
        if(feed == null) {
            feed = new YahooFeed(getProviderName(),
                                 new YahooClientFactory() {
                                    @Override
                                    public YahooClient getClient(YahooFeedServices inFeedServices)
                                    {
                                        return new YahooClientImpl(inFeedServices);
                                    }
            });
        }
        return feed;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getProviderName()
     */
    @Override
    public String getProviderName()
    {
        return PROVIDER_NAME;
    }
    /**
     * 
     */
    private static YahooFeed feed;
    /**
     * 
     */
    static final String PROVIDER_NAME = "yahoo";
}

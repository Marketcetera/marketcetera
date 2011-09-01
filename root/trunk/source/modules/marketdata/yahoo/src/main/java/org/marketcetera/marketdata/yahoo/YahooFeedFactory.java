package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link YahooFeed} objects.
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
     * feed instance
     */
    private static YahooFeed feed;
    /**
     * name of the yahoo provider
     */
    static final String PROVIDER_NAME = "yahoo"; //$NON-NLS-1$
}

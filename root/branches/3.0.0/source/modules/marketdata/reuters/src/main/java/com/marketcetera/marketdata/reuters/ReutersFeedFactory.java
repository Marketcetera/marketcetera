package com.marketcetera.marketdata.reuters;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataFeedFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedFactory.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedFactory.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersFeedFactory
        extends AbstractMarketDataFeedFactory<ReutersFeed,ReutersFeedCredentials>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed()
     */
    @Override
    public ReutersFeed getMarketDataFeed()
            throws CoreException
    {
        synchronized(ReutersFeedFactory.class) {
            if(feedInstance == null) {
                feedInstance = new ReutersFeed();
            }
        }
        return feedInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getProviderName()
     */
    @Override
    public String getProviderName()
    {
        return ReutersFeedModuleFactory.IDENTIFIER;
    }
    /**
     * feed instance
     */
    private volatile static ReutersFeed feedInstance;
}

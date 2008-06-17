package org.marketcetera.marketdata;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.IFeedComponent.FeedType;

/**
 * Test implementation of {@link IMarketDataFeedFactory}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFactory
        implements IMarketDataFeedFactory<TestMarketDataFeed,TestMarketDataFeedCredentials>
{
    private AbstractMarketDataFeedFactory<TestMarketDataFeed,TestMarketDataFeedCredentials> mInnerFactory = 
            new AbstractMarketDataFeedFactory<TestMarketDataFeed,TestMarketDataFeedCredentials>() {
        private static final String PROVIDER = "TEST";

        public TestMarketDataFeed getMarketDataFeed()
            throws MarketceteraException
        {
            return getMarketDataFeed(null);            
        }

        public String getProviderName()
        {
            return PROVIDER;
        }

        public TestMarketDataFeed getMarketDataFeed(TestMarketDataFeedCredentials inCredentials)
                throws MarketceteraException
        {
            return new TestMarketDataFeed(FeedType.SIMULATED,
                                          inCredentials);
        }                
    };

    public TestMarketDataFeed getMarketDataFeed()
        throws MarketceteraException
    {
        return mInnerFactory.getMarketDataFeed();
    }

    public String getProviderName()
    {
        return mInnerFactory.getProviderName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedFactory#getMarketDataFeed(org.marketcetera.marketdata.IMarketDataFeedCredentials)
     */
    public TestMarketDataFeed getMarketDataFeed(TestMarketDataFeedCredentials inCredentials)
            throws MarketceteraException
    {
        return mInnerFactory.getMarketDataFeed(inCredentials);
    }            
}

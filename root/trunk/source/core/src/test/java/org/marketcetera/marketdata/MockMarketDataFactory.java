package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.IFeedComponent.FeedType;

/**
 * Test implementation of {@link IMarketDataFeedFactory}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MockMarketDataFactory
        implements IMarketDataFeedFactory<MockMarketDataFeed, MockMarketDataFeedCredentials>
{
    private AbstractMarketDataFeedFactory<MockMarketDataFeed, MockMarketDataFeedCredentials> mInnerFactory =
            new AbstractMarketDataFeedFactory<MockMarketDataFeed, MockMarketDataFeedCredentials>() {
        private static final String PROVIDER = "TEST"; //$NON-NLS-1$

        public MockMarketDataFeed getMarketDataFeed()
            throws CoreException
        {
            return getMarketDataFeed(null);            
        }

        public String getProviderName()
        {
            return PROVIDER;
        }

        public MockMarketDataFeed getMarketDataFeed(MockMarketDataFeedCredentials inCredentials)
                throws CoreException
        {
            return new MockMarketDataFeed(FeedType.SIMULATED,
                                          inCredentials);
        }                
    };

    public MockMarketDataFeed getMarketDataFeed()
        throws CoreException
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
    public MockMarketDataFeed getMarketDataFeed(MockMarketDataFeedCredentials inCredentials)
            throws CoreException
    {
        return mInnerFactory.getMarketDataFeed(inCredentials);
    }            
}

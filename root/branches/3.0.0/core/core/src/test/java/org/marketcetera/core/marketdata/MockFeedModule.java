package org.marketcetera.core.marketdata;

import org.marketcetera.core.CoreException;

/* $License$ */

/**
 * Module implementation for {@link MockMarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MockFeedModule.java 82329 2012-04-10 16:28:13Z colin $
 * @since 2.1.0
 */
public class MockFeedModule
        extends AbstractMarketDataModule<MockMarketDataFeedToken,
                                                 MockMarketDataFeedCredentials>
{
    /**
     * Create a new MockFeedModule instance.
     *
     * @throws CoreException if the module could not be constructed 
     */
    MockFeedModule()
            throws CoreException
    {
        super(MockMarketDataFeedModuleFactory.INSTANCE_URN,
              MockMarketDataFactory.INSTANCE.getMarketDataFeed());
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected MockMarketDataFeedCredentials getCredentials()
            throws CoreException
    {
        return new MockMarketDataFeedCredentials();
    }
}

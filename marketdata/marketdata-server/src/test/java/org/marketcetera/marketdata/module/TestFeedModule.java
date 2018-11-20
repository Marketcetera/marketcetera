package org.marketcetera.marketdata.module;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;

/* $License$ */

/**
 * Provides a module implementation for {@link TestFeed}.
 * <p>
 * Module Features
 * <table>
 * <tr><th>Factory:</th><td>{@link TestFeedModuleFactory}</td></tr>
 * <tr><th colspan="2">See {@link AbstractMarketDataModule parent} for module features.</th></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestFeedModule
        extends AbstractMarketDataModule<TestFeedToken,
                                         TestFeedCredentials>
{
    /**
     * Create a new TestFeedModule instance.
     */
    TestFeedModule()
    {
        super(TestFeedModuleFactory.INSTANCE_URN,
              new TestFeed());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected TestFeedCredentials getCredentials()
            throws CoreException
    {
        return credentials;
    }
    /**
     * credentials instance to use for module activation
     */
    private static final TestFeedCredentials credentials = new TestFeedCredentials();
}

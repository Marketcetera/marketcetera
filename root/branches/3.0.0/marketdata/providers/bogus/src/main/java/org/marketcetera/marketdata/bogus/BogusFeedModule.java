package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.marketdata.AbstractMarketDataModule;

/* $License$ */

/**
 * StrategyAgent module for {@link BogusFeed}.
 * <p>
 * Module Features
 * <table>
 * <tr><th>Factory:</th><td>{@link BogusFeedModuleFactory}</td></tr>
 * <tr><th colspan="2">See {@link AbstractMarketDataModule parent} for module features.</th></tr>
 * </table>
 *
 * @version $Id$
 * @since 1.0.0
 */
public final class BogusFeedModule
        extends AbstractMarketDataModule<BogusFeedToken,
                                         BogusFeedCredentials>
{
    /**
     * Create a new BogusFeedEmitter instance.
     * 
     * @throws CoreException 
     */
    BogusFeedModule()
        throws CoreException
    {
        super(BogusFeedModuleFactory.INSTANCE_URN,
              BogusFeedFactory.getInstance().getMarketDataFeed());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected BogusFeedCredentials getCredentials()
        throws CoreException
    {
        return BogusFeedCredentials.getInstance();
    }
}

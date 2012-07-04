package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.marketdata.AbstractMarketDataModule;
import org.marketcetera.core.attributes.ClassVersion;

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
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedModule.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: BogusFeedModule.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
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

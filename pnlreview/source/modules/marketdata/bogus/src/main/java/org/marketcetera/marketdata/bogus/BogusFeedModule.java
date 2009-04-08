package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * StrategyAgent module for {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
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

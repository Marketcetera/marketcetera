package org.marketcetera.marketdata.cache;

import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Creates {@link MarketDataCacheModule} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataCacheModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new MarketDataCacheModuleFactory instance.
     */
    public MarketDataCacheModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              false,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new MarketDataCacheModule(INSTANCE_URN);
    }
    /**
     * provider name value
     */
    public static final String PROVIDER_NAME = "cache";  //$NON-NLS-1$
    /**
     * unique provider URN for the market data cache provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + PROVIDER_NAME);  //$NON-NLS-1$
    /**
     * instance URN for the market data cache instance
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}

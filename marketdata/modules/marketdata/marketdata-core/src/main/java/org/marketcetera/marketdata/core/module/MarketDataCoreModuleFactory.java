package org.marketcetera.marketdata.core.module;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataCoreModuleFactory
        extends ModuleFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        try {
            return new MarketDataCoreModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    /**
     * Create a new MarketDataCoreModuleFactory instance.
     */
    public MarketDataCoreModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              false,
              true);
    }
    public static final String IDENTIFIER = "core";  //$NON-NLS-1$
    /**
     * unique provider URN for the market data core module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the market data core module
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}

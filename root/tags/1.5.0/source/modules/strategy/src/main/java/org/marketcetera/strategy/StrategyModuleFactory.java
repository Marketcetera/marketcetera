package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.PROVIDER_DESCRIPTION;

import java.io.File;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Strategy Agent module factory implementation for the strategy module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class StrategyModuleFactory
        extends ModuleFactory
{
    /**
     * use this provider URN to start a strategy
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:strategy:system");  //$NON-NLS-1$
    /**
     * Create a new MarketceteraFeedModuleFactory instance.
     */
    public StrategyModuleFactory()
    {
        super(PROVIDER_URN,
              PROVIDER_DESCRIPTION,
              true,
              false,
              String.class,
              String.class,
              Object.class,
              File.class,
              Properties.class,
              Boolean.class,
              ModuleURN.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public StrategyModule create(Object... inParameters)
            throws ModuleCreationException
    {
        return StrategyModule.getStrategyModule(inParameters);
    }
}

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
 * <p>
 * The factory accepts the following parameters when creating new instances of the strategy
 * <ol>
 * <li>String: strategy instance name</li>
 * <li>String: strategy class name</li>
 * <li>Object: strategy language, one of {@link Language}</li>
 * <li>File: strategy script file</li>
 * <li>Properties: strategy properties</li>
 * <li>Boolean: if strategy should route orders to the server</li>
 * <li>ModuleURN: URN of the module to which all the output should be sent.
 * Typically the sink module.</li>
 * </ol>
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:strategy:system</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td><code>String, String, Object, File, Properties, Boolean, ModuleURN</code>: See above for details.</td></tr>
 * <tr><th>Module Type:</th><td>{@link StrategyModule}</td></tr>
 * </table>
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

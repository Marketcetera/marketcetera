package org.marketcetera.marketdata.core.manager;

import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * The provider / factory that creates the Market Data Module instance.
 * 
 * <p>The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:mdata:manager</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>InstanceURN:</th><td><code>metc:mdata:manager:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>None</td></tr>
 * <tr><th>Module Type</th><td>{@link MarketDataManagerModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new MarketDataManagerModuleFactory instance.
     */
    public MarketDataManagerModuleFactory()
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
        return new MarketDataManagerModule(INSTANCE_URN);
    }
    /**
     * provider name value
     */
    public static final String PROVIDER_NAME = "manager";  //$NON-NLS-1$
    /**
     * unique provider URN for the market data manager provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + PROVIDER_NAME);  //$NON-NLS-1$
    /**
     * instance URN for the market data manager instance
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}

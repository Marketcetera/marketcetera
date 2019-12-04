package org.marketcetera.marketdata.service;

import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for the <code>MarketDataEventModuleConnector</code> market data connector.
 * <p>
 * The factory has the following characteristics.
 * <table summary="MarketDataEventModuleConnectorFactory characteristics">
 * <tr><th>Provider URN:</th><td><code>metc:mdata:connector</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:mdata:connector:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type:</th><td>{@link MarketDataEventModuleConnector}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataEventModuleConnectorFactory
        extends ModuleFactory
{
    /**
     * Create a new MarketDataEventModuleConnectorFactory instance.
     */
    public MarketDataEventModuleConnectorFactory()
    {
        super(PROVIDER_URN,
              Messages.EVENT_MODULE_CONNECTOR_PROVIDER_DESCRIPTION,
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
        return new MarketDataEventModuleConnector(INSTANCE_URN);
    }
    /**
     * market data provider identifier
     */
    public static final String IDENTIFIER = "connector";  //$NON-NLS-1$
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN value
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}

package org.marketcetera.marketdata.core.manager;

import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Provides a common module for market data flows.
 *
 * <p>
 * Module Features
 * <table>
 * <caption>MarketDataManagerModuleFactory Capabilities</caption>
 * <tr><th>Capabilities</th><td>Data Requester</td></tr>
 * <tr><th>Stops data flows</th><td>Yes</td></tr>
 * <tr><th>Start Operation</th><td>None</td></tr>
 * <tr><th>Stop Operation</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>&nbsp;</td></tr>
 * <tr><th>MX Notification</th><td>&nbsp;</td></tr>
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

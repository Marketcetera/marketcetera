package org.marketcetera.server;

import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.strategyagent.StrategyAgent;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides Marketcetera server-space services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ServerApp
{
    /**
     * Gets the <code>Server</code> instance.
     *
     * @return a <code>Server</code> instance
     * @throws IllegalStateException if the <code>Server</code> has not been initialized
     */
    public static ServerApp getInstance()
    {
        if(instance == null) {
            throw new IllegalStateException("The server has not been initialized");
        }
        return instance;
    }
    /**
     *
     *
     * @param inArgs
     */
    public static void main(String[] inArgs)
    {
        OrderRoutingSystem.main(inArgs);
        StrategyAgent.main(inArgs);
    }
    /**
     * Create a new ServerApp instance.
     *
     * @param inOrsApp
     * @param inSaApp
     */
    private ServerApp(OrderRoutingSystem inOrsApp,
                      StrategyAgent inSaApp)
    {
        orsApp = inOrsApp;
        saApp = inSaApp;
    }
    /**
     * 
     */
    private final OrderRoutingSystem orsApp;
    /**
     * 
     */
    private final StrategyAgent saApp;
    /**
     * the singleton instance of the server
     */
    private static volatile ServerApp instance;
}

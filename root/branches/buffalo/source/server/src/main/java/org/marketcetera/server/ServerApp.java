package org.marketcetera.server;

import org.marketcetera.core.ApplicationBase;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.strategyagent.StrategyAgent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
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
        extends ApplicationBase
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
    public static void main(final String[] inArgs)
    {
        // TODO register shutdown hook?
        orsThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                SLF4JLoggerProxy.info(ServerApp.class,
                                      "Starting ORS Node");
                OrderRoutingSystem.main(inArgs);
            }
        },
                               "Server ORS Thread");
        orsThread.start();
        saThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                StrategyAgent.main(inArgs);
            }
        },
                              "Server SA Thread");
//        saThread.start();
    }
    /**
     * 
     */
    private static final String APP_CONTEXT_CFG_BASE= "file:" + CONF_DIR + "properties.xml";
    /**
     * 
     */
    private static Thread orsThread;
    /**
     * 
     */
    private static Thread saThread;
    /**
     * the singleton instance of the server
     */
    private static volatile ServerApp instance;
}

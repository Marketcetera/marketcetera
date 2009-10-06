package org.marketcetera.photon.strategy.engine.strategyagent;

import java.util.concurrent.ExecutorService;

import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.InternalStrategyAgentEngine;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to this bundle's functionality.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentEngines {

    /**
     * The symbolic name of this plugin.
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.strategyagent"; //$NON-NLS-1$

    /**
     * Creates a {@link StrategyAgentEngine} that will connect to remote
     * strategy agents.
     * <p>
     * Implementation Notes:
     * <ol>
     * <li>This method creates and returns a model object that is not thread
     * safe. It should therefore be called in the the same thread that will
     * display the object (i.e. the UI thread).</li>
     * <li>Neither the engine nor its connection are thread safe, so they should
     * not be accessed concurrently.</li>
     * <li>All updates to the model are performed synchronously using the
     * guiExecutor.</li>
     * </ol>
     * @param engine
     *            the desired engine configuration
     * @param guiExecutor
     *            the executor to run tasks that change the model state
     * @param credentialsService
     *            the service to use to authenticate connections
     * @param logoutService
     *            the service used to disconnect remote connections on logout
     * 
     * @return the ready-to-use strategy agent engine
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static StrategyAgentEngine createStrategyAgentEngine(
            StrategyAgentEngine engine,
            ExecutorService guiExecutor,
            ICredentialsService credentialsService, ILogoutService logoutService) {
        return new InternalStrategyAgentEngine(engine, guiExecutor,
                credentialsService, logoutService, SAClientFactory
                        .getInstance(), ModuleSupport.getSinkDataManager());
    }
}

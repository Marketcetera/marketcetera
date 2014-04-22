package org.marketcetera.photon.strategy.engine.embedded;

import java.util.concurrent.ExecutorService;

import org.eclipse.core.runtime.Platform;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.internal.strategy.engine.embedded.EmbeddedConnection;
import org.marketcetera.photon.internal.strategy.engine.embedded.EmbeddedEngineImpl;
import org.marketcetera.photon.internal.strategy.engine.embedded.PersistenceService;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to the embedded strategy engine functionality.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EmbeddedEngine {

    /**
     * The symbolic name of this plugin.
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.embedded"; //$NON-NLS-1$

    /**
     * Name of file where the embedded engine state is persisted.
     */
    private static final String STRATEGIES_FILENAME = "strategies.xml"; //$NON-NLS-1$

    /**
     * Returns the UI model object for the embedded strategy engine.
     * <p>
     * Implementation Notes:
     * <ol>
     * <li>This method creates and returns a model object that is not thread
     * safe. It should therefore be called in the the same thread that will
     * display the object (i.e. the UI thread).</li>
     * <li>The embedded connection is not thread safe and should not be accessed
     * concurrently.</li>
     * <li>All updates to the model are performed synchronously using the
     * guiExecutor.</li>
     * <li>If restore is true, this method will synchronously read the saved
     * state and deploy any discovered strategies, which may take some time.</li>
     * </ol>
     * 
     * @param guiExecutor
     *            the executor to run tasks that change the model state
     * @param restore
     *            whether to attempt to restore saved engine state from the
     *            plug-in state location
     * @return the engine model object
     * @throws IllegalArgumentException
     *             if guiExecutor is null
     */
    public static StrategyEngine createEngine(ExecutorService guiExecutor,
            boolean restore) {
        Validate.notNull(guiExecutor, "guiExecutor"); //$NON-NLS-1$
        StrategyEngine engine = new EmbeddedEngineImpl();
        EmbeddedConnection connection = new EmbeddedConnection(engine,
                guiExecutor, restore ? new PersistenceService(Platform
                        .getStateLocation(
                                Platform.getBundle(EmbeddedEngine.PLUGIN_ID))
                        .append(STRATEGIES_FILENAME).toFile()) : null);
        connection.initialize();
        return engine;
    }

    private EmbeddedEngine() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}

package org.marketcetera.photon.internal.strategy.engine.embedded;

import java.io.IOException;
import java.util.Collection;

import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Internal interface used by {@link EmbeddedConnection} for persistence.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface IPersistenceService {

    /**
     * Restores saved state, using the provided connection to deploy strategies.
     * 
     * @param connection
     *            the connection to use
     * @throws IllegalArgumentException
     *             if connection is null
     */
    void restore(StrategyEngineConnection connection);

    /**
     * Saves the provided strategies in a way that can later be restored using
     * {@link #restore(StrategyEngineConnection)}.
     * <p>
     * This is a no-op if a restore is currently in progress.
     * 
     * @param strategies
     *            the strategies to save
     * @throws IOException
     *             if the save fails
     * @throws IllegalArgumentException
     *             if strategies is null
     */
    void save(Collection<? extends Strategy> strategies) throws IOException;

}
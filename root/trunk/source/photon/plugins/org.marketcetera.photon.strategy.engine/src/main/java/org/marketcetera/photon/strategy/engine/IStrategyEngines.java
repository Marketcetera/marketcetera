package org.marketcetera.photon.strategy.engine;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for a provider of an observable list of strategy engines. The
 * strategy engine model is not thread safe and must be updated on a single
 * thread, typically the UI event queue thread. Since this interface provides
 * access to the root of of a strategy engine model tree, all methods must be
 * called on this same thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public interface IStrategyEngines {

    /**
     * Returns an unmodifiable observable list of {@link StrategyEngine}
     * objects.
     * 
     * @return the strategy engines
     */
    IObservableList/* <StrategyEngine> */getStrategyEngines();

    /**
     * Adds the given engine to the managed list.
     * 
     * @param engine
     *            the new engine
     * @return the new engine that was actually added, not necessarily the same
     *          as the one passed in
     * @throws IllegalArgumentException
     *             if engine is null
     */
    StrategyEngine addEngine(StrategyEngine engine);

    /**
     * Removes the given engine to the managed list.
     * 
     * @param engine
     *            the engine to remove
     * @throws IllegalArgumentException
     *             if engine is null
     */
    void removeEngine(StrategyEngine engine);

}

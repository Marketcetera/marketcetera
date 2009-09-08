package org.marketcetera.photon.strategy.engine;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for a provider of an observable list of strategy engines.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface IStrategyEngines {

    /**
     * Returns an observable list of {@link StrategyEngine} objects.
     * 
     * @return the strategy engines
     */
    IObservableList/*<StrategyEngine>*/ getStrategyEngines();

}

package org.marketcetera.strategy;

import org.marketcetera.strategy.events.StrategyEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface StrategyEventListener
{
    /**
     * 
     *
     *
     * @param inEvent
     */
    default void receiveStrategyEvent(StrategyEvent inEvent) {}
}

package org.marketcetera.strategy;

/* $License$ */

/**
 * Provides a strategy instance value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface StrategyInstanceHolder
{
    /**
     * Get the strategy instance value.
     *
     * @return a <code>StrategyInstance</code> value
     */
    StrategyInstance getStrategyInstance();
}

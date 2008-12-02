package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Executes a strategy.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface Executor
{
    /**
     * Starts execution of the {@link Strategy}.
     *
     * @return a <code>RunningStrategy</code> value
     * @throws StrategyException if an error occurs
     */
    RunningStrategy start()
        throws StrategyException;
    /**
     * Stops execution of a {@link Strategy}.
     *
     * @throws StrategyException if an error occurs
     */
    void stop()
        throws StrategyException;
}

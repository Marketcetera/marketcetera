package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Executes a strategy script.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
interface ExecutionEngine
{
    /**
     * Prepares the <code>ExecutionEngine</code> to execute the given <code>Strategy</code>.
     *
     * @param inStrategy a <code>Strategy</code> value
     * @param inProcessedScript a <code>String</code> value containing the actual text of the strategy to execute
     * @throws StrategyException if an error occurs
     */
    void prepare(Strategy inStrategy,
                 String inProcessedScript)
        throws StrategyException;
    /**
     * Starts the execution of the {@link Strategy}.
     *
     * @return an <code>Object</code> containing a reference to the {@link Strategy} being executed
     * @throws StrategyException if an error occurs
     */
    Object start()
        throws StrategyException;
    /**
     * Stops the execution of the {@link Strategy}. 
     *
     * @throws StrategyException if an error occurs
     */
    void stop()
        throws StrategyException;
}

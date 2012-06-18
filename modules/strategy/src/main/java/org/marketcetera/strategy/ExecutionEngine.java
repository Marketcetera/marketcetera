package org.marketcetera.strategy;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Executes a strategy script.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionEngine.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ExecutionEngine.java 16063 2012-01-31 18:21:55Z colin $")
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

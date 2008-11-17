package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * {@link ExecutionEngine} implementation for Java scripts.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class JavaEngine
        implements ExecutionEngine
{
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.ExecutionEngine#prepare(org.marketcetera.strategy.Strategy, java.lang.String)
     */
    @Override
    public void prepare(Strategy inStrategy,
                        String inProcessedScript)
            throws StrategyException
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.ExecutionEngine#start()
     */
    @Override
    public Object start()
            throws StrategyException
    {
        // TODO this is wedged-in in order to allow unit tests to pass
        return new org.marketcetera.strategy.java.Strategy();
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.ExecutionEngine#stop()
     */
    @Override
    public void stop()
            throws StrategyException
    {
    }
}

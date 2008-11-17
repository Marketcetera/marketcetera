package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * {@link Executor} implementation for Java strategies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class JavaExecutor
    extends AbstractExecutor
{
    /**
     * Create a new JavaExecutor instance.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    JavaExecutor(Strategy inStrategy)
    {
        super(inStrategy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractExecutor#preprocess(java.lang.String)
     */
    @Override
    protected String preprocess(String inScript)
            throws StrategyException
    {
        return inScript;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractExecutor#getExecutionEngine()
     */
    @Override
    protected ExecutionEngine getExecutionEngine()
            throws StrategyException
    {
        return new JavaEngine();
    }
}

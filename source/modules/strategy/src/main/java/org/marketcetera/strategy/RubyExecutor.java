package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * {@link Executor} implementation for Ruby strategies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class RubyExecutor
    extends AbstractExecutor
{
    /**
     * Create a new RubyExecutor instance.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    RubyExecutor(Strategy inStrategy)
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
        // in order to return a RunningStrategy object, it is necessary to tack on a "new" call to the end of the script
        // this is why we need to know the name of the class that the user intends to be the main class of the strategy
        StringBuilder fullScript = new StringBuilder();
        fullScript.append(inScript);
        fullScript.append("\n").append(getStrategy().getName()).append(".new\n"); //$NON-NLS-1$  //$NON-NLS-2$
        String processedScript = fullScript.toString();
        return processedScript;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractExecutor#getExecutionEngine()
     */
    @Override
    protected ExecutionEngine getExecutionEngine()
            throws StrategyException
    {
        return new BeanScriptingFrameworkEngine();
    }
}

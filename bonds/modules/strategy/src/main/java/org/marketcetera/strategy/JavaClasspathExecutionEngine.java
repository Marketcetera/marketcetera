package org.marketcetera.strategy;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Executes a Java strategy using the default classpath.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class JavaClasspathExecutionEngine
        implements ExecutionEngine
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#prepare(org.marketcetera.strategy.Strategy, java.lang.String)
     */
    @Override
    public void prepare(Strategy inStrategy,
                        String inProcessedScript)
            throws StrategyException
    {
        strategy = inStrategy;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#start()
     */
    @Override
    public Object start()
            throws StrategyException
    {
        try {
            return getClass().getClassLoader().loadClass(strategy.getName()).newInstance();
        } catch (ClassNotFoundException e) {
            throw new StrategyException(e);
        } catch (InstantiationException e) {
            throw new StrategyException(e);
        } catch (IllegalAccessException e) {
            throw new StrategyException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#stop()
     */
    @Override
    public void stop()
            throws StrategyException
    {
        // nothing to do
    }
    /**
     * strategy to be executed
     */
    private Strategy strategy;
}

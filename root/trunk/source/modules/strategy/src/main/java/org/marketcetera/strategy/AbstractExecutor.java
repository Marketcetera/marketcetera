package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.NO_STRATEGY_CLASS;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Executes a given {@link Strategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
abstract class AbstractExecutor
        implements Executor
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#start()
     */
    @Override
    public final RunningStrategy start()
        throws StrategyException
    {
        String script = getStrategy().getScript();
        String processedScript = preprocess(script);
        engine = getExecutionEngine();
        engine.prepare(getStrategy(),
                       processedScript);
        Object objectReturned = engine.start();
        if(objectReturned == null) {
            // TODO throw no object returned error
            throw new NullPointerException();
        }
        if(objectReturned instanceof RunningStrategy) {
            RunningStrategy runningStrategy = (RunningStrategy)objectReturned;
            if(runningStrategy instanceof AbstractRunningStrategy) {
                AbstractRunningStrategy abstractRunningStrategy = (AbstractRunningStrategy)runningStrategy;
                // make the parameters available to the strategy 
                abstractRunningStrategy.setParameters(getStrategy().getParameters());
            } else {
                // TODO warn that no parameters will be available to this strategy
            }
            this.runningStrategy = runningStrategy;
            // TODO put into executorservice
            runningStrategy.onStart();
            return runningStrategy;
        } else {
            throw new StrategyException(NO_STRATEGY_CLASS);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#stop()
     */
    @Override
    public final void stop()
        throws StrategyException
    {
        // TODO put into executorservice - should this time out?
        if(runningStrategy != null) {
            runningStrategy.onStop();
        } else {
            // TODO warn about null running strategy
        }
        engine.stop();
    }
    /**
     * Get the strategy value.
     *
     * @return a <code>Strategy</code> value
     */
    final Strategy getStrategy()
    {
        return strategy;
    }
    /**
     * Prepares the script to be executed. 
     *
     * <p>Any work that needs to be done on the script before it can be executed is done here.
     *
     * @param inScript
     * @return TODO
     * @throws StrategyException
     */
    protected abstract String preprocess(String inScript)
        throws StrategyException;
    /**
     * Gets the <code>ExecutionEngine</code> used to execute the strategy script.
     *
     * @return an <code>ExecutionEngine</code> value
     * @throws StrategyException if an <code>ExecutionEngine</code> cannot be created
     */
    protected abstract ExecutionEngine getExecutionEngine()
        throws StrategyException;
    /**
     * Create a new ExecutorBase instance.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    protected AbstractExecutor(Strategy inStrategy)
    {
        strategy = inStrategy;
    }
    /**
     * the underlying strategy object
     */
    private final Strategy strategy;
    /**
     * the object that executes the processed strategy
     */
    private ExecutionEngine engine;
    /**
     * the object returned from the execution engine that represents that running strategy
     */
    private RunningStrategy runningStrategy;
}

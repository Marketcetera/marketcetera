package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.NO_STRATEGY_CLASS;
import static org.marketcetera.strategy.Messages.RUNTIME_ERROR;
import static org.marketcetera.strategy.Messages.STRATEGY_COMPILATION_NULL_RESULT;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;

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
        throws Exception
    {
        String script = getStrategy().getScript();
        String processedScript = preprocess(script);
        engine = getExecutionEngine();
        engine.prepare(getStrategy(),
                       processedScript);
        Object objectReturned = engine.start();
        if(objectReturned == null) {
            STRATEGY_COMPILATION_NULL_RESULT.error(Strategy.STRATEGY_MESSAGES,
                                                   getStrategy());
            throw new StrategyException(new I18NBoundMessage1P(STRATEGY_COMPILATION_NULL_RESULT,
                                                               getStrategy().toString()));
        }
        if(objectReturned instanceof RunningStrategy) {
            RunningStrategy runningStrategy = (RunningStrategy)objectReturned;
            // assertions can be disabled, in which case, a CCE will be thrown on the following line.  In either case,
            //  this error is a development-time error and should not happen in the wild.  This error is, in effect,
            //  a shout-out that the design of strategy needs to change.  If, in the future, someone decides to create
            //  a strategy subclass that is not an instance of AbstractRunningStrategy, the whole approach needs to be
            //  re-thought.
            assert(runningStrategy instanceof AbstractRunningStrategy);
            AbstractRunningStrategy abstractRunningStrategy = (AbstractRunningStrategy)runningStrategy;
            // make the parameters available to the strategy 
            abstractRunningStrategy.setStrategy(getStrategy());
            this.runningStrategy = runningStrategy;
            try {
                runningStrategy.onStart();
            } catch (Exception e) {
                RUNTIME_ERROR.error(Strategy.STRATEGY_MESSAGES,
                                    getStrategy(),
                                    translateMethodName("onStart"), //$NON-NLS-1$
                                    interpretRuntimeException(e));
                throw e;
            }
            return runningStrategy;
        } else {
            NO_STRATEGY_CLASS.error(Strategy.STRATEGY_MESSAGES);
            throw new StrategyException(NO_STRATEGY_CLASS);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#stop()
     */
    @Override
    public final void stop()
        throws Exception
    {
        assert(runningStrategy != null);
        assert(runningStrategy instanceof AbstractRunningStrategy);
        AbstractRunningStrategy abstractRunningStrategy = (AbstractRunningStrategy)runningStrategy;
        abstractRunningStrategy.stop();
        try {
            runningStrategy.onStop();
        } catch (Exception e) {
            RUNTIME_ERROR.error(Strategy.STRATEGY_MESSAGES,
                                getStrategy(),
                                translateMethodName("onStop"), //$NON-NLS-1$
                                interpretRuntimeException(e));
            throw e;
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
     * @param inScript a <code>String</code> value containing the strategy script
     * @return a <code>String</code> value containing the processed strategy script
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

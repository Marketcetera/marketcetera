package org.marketcetera.strategy;

import static org.marketcetera.strategy.Status.RUNNING;
import static org.marketcetera.strategy.Status.STARTING;
import static org.marketcetera.strategy.Status.STOPPED;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Executes a given {@link Strategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
abstract class AbstractExecutor
        implements Executor, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#start()
     */
    @Override
    public final synchronized RunningStrategy start()
        throws Exception
    {
        String script = getStrategy().getScript();
        String processedScript = preprocess(script);
        engine = getExecutionEngine();
        engine.prepare(getStrategy(),
                       processedScript);
        Object objectReturned = engine.start();
        if(objectReturned == null) {
            StrategyModule.log(LogEventBuilder.error().withMessage(STRATEGY_COMPILATION_NULL_RESULT,
                                                                   String.valueOf(getStrategy())).create(),                               
                               getStrategy());                               
            throw new StrategyException(new I18NBoundMessage1P(STRATEGY_COMPILATION_NULL_RESULT,
                                                               getStrategy().toString()));
        }
        if(objectReturned instanceof RunningStrategy) {
            final RunningStrategy runningStrategy = (RunningStrategy)objectReturned;
            // assertions can be disabled, in which case, a CCE will be thrown on the following line.  In either case,
            //  this error is a development-time error and should not happen in the wild.  This error is, in effect,
            //  a shout-out that the design of strategy needs to change.  If, in the future, someone decides to create
            //  a strategy subclass that is not an instance of AbstractRunningStrategy, the whole approach needs to be
            //  re-thought.
            assert(runningStrategy instanceof AbstractRunningStrategy);
            assert(getStrategy() instanceof StrategyImpl);
            AbstractRunningStrategy abstractRunningStrategy = (AbstractRunningStrategy)runningStrategy;
            final StrategyImpl enclosingStrategy = (StrategyImpl)getStrategy();
            // make the parameters available to the strategy 
            abstractRunningStrategy.setStrategy(getStrategy());
            this.runningStrategy = runningStrategy;
            enclosingStrategy.setRunningStrategy(runningStrategy);
            // activate the strategy by causing its "onStart" method to be invoked.
            // note that any status changes that result from execution of the start method need to be marked within
            //  the following loop.
            try {
                SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                       "{} start job beginning", //$NON-NLS-1$
                                       getStrategy());
                enclosingStrategy.setStatus(STARTING);
                abstractRunningStrategy.start();
                runningStrategy.onStart();
                enclosingStrategy.setStatus(RUNNING);
                return runningStrategy;
            } catch (Exception e) {
                // this means that the "onStart" method was never completed so the strategy never started
                // this will cause a moduleCreationError in StrategyModule, which is what we want
                StrategyModule.log(LogEventBuilder.error().withMessage(RUNTIME_ERROR,
                                                                       getStrategy().toString(),
                                                                       translateMethodName("onStart"), //$NON-NLS-1$
                                                                       interpretRuntimeException(e))
                                                          .withException(e).create(),
                                   getStrategy());
                throw e;
            }
        } else {
            StrategyModule.log(LogEventBuilder.error().withMessage(NO_STRATEGY_CLASS).create(),
                               getStrategy());
            throw new StrategyException(NO_STRATEGY_CLASS);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#stop()
     */
    @Override
    public final synchronized void stop()
        throws Exception
    {
        assert(runningStrategy != null);
        assert(runningStrategy instanceof AbstractRunningStrategy);
        assert(getStrategy() instanceof StrategyImpl);
        AbstractRunningStrategy abstractRunningStrategy = (AbstractRunningStrategy)runningStrategy;
        abstractRunningStrategy.stop();
        final StrategyImpl enclosingStrategy = (StrategyImpl)getStrategy();
        try {
                SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                       "{} stop job beginning", //$NON-NLS-1$
                                       getStrategy());
                runningStrategy.onStop();
                enclosingStrategy.setStatus(STOPPED);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.error().withMessage(RUNTIME_ERROR,
                                                                   String.valueOf(getStrategy()),
                                                                   translateMethodName("onStop"), //$NON-NLS-1$
                                                                   interpretRuntimeException(e))
                                                     .withException(e).create(),
                               getStrategy());
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

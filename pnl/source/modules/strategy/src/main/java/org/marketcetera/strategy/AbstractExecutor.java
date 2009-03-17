package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.CANCELING_START_JOB;
import static org.marketcetera.strategy.Messages.CANCELING_STOP_JOB;
import static org.marketcetera.strategy.Messages.CANNOT_STOP;
import static org.marketcetera.strategy.Messages.ERROR_WAITING_FOR_STOP;
import static org.marketcetera.strategy.Messages.INTERRUPT_COMPLETE;
import static org.marketcetera.strategy.Messages.INTERRUPT_START_ERROR;
import static org.marketcetera.strategy.Messages.INTERRUPT_STOP_ERROR;
import static org.marketcetera.strategy.Messages.NO_STRATEGY_CLASS;
import static org.marketcetera.strategy.Messages.RUNTIME_ERROR;
import static org.marketcetera.strategy.Messages.STRATEGY_COMPILATION_NULL_RESULT;
import static org.marketcetera.strategy.Status.FAILED;
import static org.marketcetera.strategy.Status.RUNNING;
import static org.marketcetera.strategy.Status.STARTING;
import static org.marketcetera.strategy.Status.STOPPED;
import static org.marketcetera.strategy.Status.STOPPING;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.LogEvent;
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
        implements Executor
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
            StrategyModule.log(LogEvent.error(STRATEGY_COMPILATION_NULL_RESULT,
                                              String.valueOf(getStrategy())),                               
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
            // activate the strategy by causing its "onStart" method to be invoked.  note that this happens in another
            //  thread which allows "onStart" to take as long as it wants.  the thread will stay alive until "onStart"
            //  completes at which point it will be recycled for use for other strategies.
            // note that any status changes that result from execution of the start thread need to be marked within
            //  the following loop.  normally, the caller can decide how to set the status of the strategy, but, since
            //  the following loop happens asynchronously, the caller has no context to make these decisions.
            try {
                SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                       "{} submitting start job", //$NON-NLS-1$
                                       getStrategy());
                startJob = executors.submit(new Callable<RunningStrategy>(){
                    @Override
                    public RunningStrategy call()
                        throws Exception
                    {
                        SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                               "{} start job beginning", //$NON-NLS-1$
                                               getStrategy());
                        try {
                            enclosingStrategy.setStatus(STARTING);
                            runningStrategy.onStart();
                            enclosingStrategy.setStatus(RUNNING);
                        } catch (Exception e) {
                            // this means that a runtime error occurred during "onStart"
                            // this does not cause a module creation error, but the strategy is broken-by-fiat
                            enclosingStrategy.setStatus(FAILED);
                            StrategyModule.log(LogEvent.error(RUNTIME_ERROR,
                                                              e,
                                                              String.valueOf(getStrategy()),
                                                              translateMethodName("onStart"), //$NON-NLS-1$
                                                              interpretRuntimeException(e)),
                                               getStrategy());
                        } finally {
                            SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                                   "{} start job completed, now at {}", //$NON-NLS-1$
                                                   getStrategy(),
                                                   getStrategy().getStatus());
                        }
                        return runningStrategy;
                    }
                });
            } catch (Exception e) {
                // this means that the "onStart" method was never executed and the strategy never started
                // this will cause a moduleCreationError in StrategyModule, which is what we want
                StrategyModule.log(LogEvent.error(RUNTIME_ERROR,
                                                  e,
                                                  getStrategy().toString(),
                                                  translateMethodName("onStart"), //$NON-NLS-1$
                                                  interpretRuntimeException(e)),
                                   getStrategy());
                throw e;
            }
            return runningStrategy;
        } else {
            StrategyModule.log(LogEvent.error(NO_STRATEGY_CLASS),
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
                                   "{} submitting stop job, state is {}", //$NON-NLS-1$
                                   getStrategy(),
                                   getStrategy().getStatus());
            stopJob = executors.submit(new Callable<RunningStrategy>() {
                @Override
                public RunningStrategy call()
                        throws Exception
                {
                    try {
                        SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                               "{} stop job beginning", //$NON-NLS-1$
                                               getStrategy());
                        runningStrategy.onStop();
                        enclosingStrategy.setStatus(STOPPED);
                    } catch (Exception e) {
                        enclosingStrategy.setStatus(FAILED);
                        StrategyModule.log(LogEvent.error(RUNTIME_ERROR,
                                                          e,
                                                          String.valueOf(getStrategy()),
                                                          translateMethodName("onStop"), //$NON-NLS-1$
                                                          interpretRuntimeException(e)),
                                           getStrategy());
                    } finally {
                        SLF4JLoggerProxy.debug(AbstractExecutor.class,
                                               "{} stop job completed, now at {}", //$NON-NLS-1$
                                               getStrategy(),
                                               getStrategy().getStatus());
                    }
                    return runningStrategy;
                }
            });
        } catch (Exception e) {
            StrategyModule.log(LogEvent.error(RUNTIME_ERROR,
                                              e,
                                              String.valueOf(getStrategy()),
                                              translateMethodName("onStop"), //$NON-NLS-1$
                                              interpretRuntimeException(e)),
                               getStrategy());
            throw e;
        }
        engine.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#interrupt()
     */
    @Override
    public synchronized void interrupt()
    {
        // interrupt the start job, if necessary
        interruptStartJob();
        // we have tried to stop the start loop
        // cancel all data requests to make sure more data isn't coming in
        getStrategy().getOutboundServicesProvider().cancelAllDataRequests();
        // the next step is to try to stop the strategy normally
        // make sure the strategy is in a state where the strategy can be stopped
        if(!getStrategy().getStatus().canChangeStatusTo(STOPPING)) {
            StrategyModule.log(LogEvent.error(CANNOT_STOP,
                                              String.valueOf(getStrategy()),
                                              getStrategy().getStatus()),
                               getStrategy());
            return;
        }
        // now stop the strategy
        doStopAndWait();
        // the strategy has now stopped normally or has timed out waiting for it to stop 
        // interrupt the stop job, if necessary
        interruptStopJob();
        // best effort to interrupt the strategy is done
        StrategyModule.log(LogEvent.debug(INTERRUPT_COMPLETE,
                                          getStrategy().toString(),
                                          (startJob == null ? 1 : (startJob.isDone() ? 1 : 0)),
                                          (stopJob == null ? 1 : (stopJob.isDone() ? 1 : 0)),
                                          getStrategy().getStatus()),
                           getStrategy());
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
     * Interrupts a long-running strategy start method if necessary.
     *
     * <p>This method determines if the strategy start method is still running and
     * will try to interrupt the thread which is executing the start method.  This
     * method will then wait a reasonable amount of time for the interrupt to complete and
     * then return.  The caller should check the strategy status when this method completes.
     */
    private void interruptStartJob()
    {
        // check to see if there is an outstanding start job to complete, i.e., if the strategy is in STARTING
        if(startJob != null &&
           !startJob.isDone()) {
            // indicates that there is an outstanding start job, try to interrupt the start job and
            //  move the strategy to RUNNING status
            try {
                StrategyModule.log(LogEvent.debug(CANCELING_START_JOB,
                                                  String.valueOf(getStrategy())),
                                   getStrategy());
                startJob.cancel(true);
            } catch (Exception e) {
                StrategyModule.log(LogEvent.warn(INTERRUPT_START_ERROR,
                                                 e,
                                                 String.valueOf(getStrategy())),
                                   getStrategy());
                // continue interrupt process
            }
            // job has been interrupted to the best of our ability
            // wait until the strategy moves to RUNNING or FAILED (indicating the start loop is complete) or
            //  the given wait period has elapsed
            try {
                long startTime = System.currentTimeMillis();
                while(!(getStrategy().getStatus().equals(RUNNING) ||
                        getStrategy().getStatus().equals(FAILED)) &&
                       System.currentTimeMillis() - startTime <= (30 * 1000)) {
                    Thread.sleep(250);
                }
            } catch (Exception e) {
                StrategyModule.log(LogEvent.warn(INTERRUPT_START_ERROR,
                                                 e,
                                                 String.valueOf(getStrategy())),
                                   getStrategy());
                // might not have been able to stop the start loop, plug ahead and see if we
                //  can still get out of this
            }
        }
    }
    /**
     * Interrupts a long-running strategy stop method if necessary.
     *
     * <p>This method determines if the strategy stop method is still running and
     * will try to interrupt the thread which is executing the stop method.  This
     * method will then wait a reasonable amount of time for the interrupt to complete and
     * then return.  The caller should check the strategy status when this method completes.
     */
    private void interruptStopJob()
    {
        if(stopJob != null &&
           !stopJob.isDone()) {
            // strategy should be in STOPPING mode
            try {
                StrategyModule.log(LogEvent.debug(CANCELING_STOP_JOB,
                                                  String.valueOf(getStrategy())),
                                   getStrategy());
                // try to kill the stop job
                stopJob.cancel(true);
            } catch (Exception e) {
                StrategyModule.log(LogEvent.warn(INTERRUPT_STOP_ERROR,
                                                 e),
                                   getStrategy());
                // do not necessarily quit just yet, wait and see if the strategy stops
            }
        }
        try {
            // wait until the status is at STOPPED or FAILED (or 30s elapses)
            long startTime = System.currentTimeMillis();
            while(!(getStrategy().getStatus().equals(STOPPED) ||
                    getStrategy().getStatus().equals(FAILED)) &&
                   System.currentTimeMillis() - startTime <= (30 * 1000)) {
                Thread.sleep(250);
            }
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(INTERRUPT_STOP_ERROR,
                                             e,
                                             String.valueOf(getStrategy())),
                               getStrategy());
        }
    }
    /**
     * Issues a stop request to the strategy and waits for it to stop.
     *
     * <p>If the strategy does not stop in a reasonable amount of time, this method will
     * return without stopping the strategy.  No special effort is made to make sure the
     * strategy stops.  The caller should check the strategy status after this method
     * returns.
     */
    private void doStopAndWait()
    {
        try {
            // strategy should be in RUNNING, STOPPING, STOPPED, or FAILED mode
            // try to stop the strategy normally
            getStrategy().stop();
            long startTime = System.currentTimeMillis();
            // courteously wait 30 seconds for the stop loop to complete, then kill it, too
            while(!(getStrategy().getStatus().equals(STOPPED) ||
                    getStrategy().getStatus().equals(FAILED)) &&
                    System.currentTimeMillis() - startTime <= (30 * 1000)) {
                Thread.sleep(250);
            }
            // strategy is at STOPPED or FAILED, or at STOPPING and 30s have elapsed
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(ERROR_WAITING_FOR_STOP,
                                             e,
                                             String.valueOf(getStrategy())),
                               getStrategy());
            // may not have been able to stop, keep going
        }
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
    private Future<RunningStrategy> startJob;
    private Future<RunningStrategy> stopJob;
    /**
     * executor service used to execute strategies asynchronously
     */
    private static final ExecutorService executors = Executors.newCachedThreadPool();
}

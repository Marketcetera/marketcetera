package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.CANNOT_CHANGE_STATE;
import static org.marketcetera.strategy.Messages.INVALID_STATUS_TO_RECEIVE_DATA;
import static org.marketcetera.strategy.Messages.RUNTIME_ERROR;
import static org.marketcetera.strategy.Messages.STRATEGY_STILL_RUNNING;
import static org.marketcetera.strategy.Status.COMPILING;
import static org.marketcetera.strategy.Status.FAILED;
import static org.marketcetera.strategy.Status.STOPPED;
import static org.marketcetera.strategy.Status.STOPPING;
import static org.marketcetera.strategy.Status.UNSTARTED;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.util.log.I18NBoundMessage2P;

/* $License$ */

/**
 * Representation of a particular strategy.
 * 
 * <p>This class is responsible for tracking the lifecycle and managing the execution
 * of a strategy.  The existence of this object in scope represents the existence
 * of an actual strategy.  If this object is <em>running</em> as indicated by
 * {@link #isRunning()}, then the embedded strategy is running.
 * 
 * <p>The embedded strategy will not begin executing until {@link #start()} is invoked.
 * The strategy will continue to execute until stopped or an error occurs.
 * 
 * <p>To make the embedded strategy stop, invoke {@link #stop()} (preferable) or allow
 * this object to go out-of-scope.  If the object is allowed to go out-of-scope without
 * invoking {@link #stop()}, the embedded strategy will not be warned it is stopping.
 * Additionally, the strategy will keep executing until the next garbage-collection,
 * which is not deterministic.  It is good practice to call {@link #stop()} on each
 * strategy at the appropriate time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class StrategyImpl
        implements Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#start()
     */
    @Override
    public final void start()
        throws StrategyException
    {
        // start and stop need to be protected in case someone tries to start a strategy that
        //  is already started or stop one that is already stopping - other state changes are
        //  internally dictated so, for example, the user could not direct a change from 
        //  COMPILING to STARTING except through start
        if(!getStatus().canChangeStatusTo(COMPILING)) {
            StrategyModule.log(LogEvent.warn(CANNOT_CHANGE_STATE,
                                             String.valueOf(this),
                                             getStatus(),
                                             COMPILING),
                               this);
            return;
        }
        try {
            setExecutor(getLanguage().getExecutor(this));
            setStatus(COMPILING);
            setRunningStrategy(getExecutor().start());
            // intentionally not setting status to "RUNNING" because the
            //  "onStart" method, successful completion of which is required
            //  to be in "RUNNING" status, is being executed asynchronously.
            //  it is the responsibility of the "onStart" executor to determine
            //  the status of the strategy - except in the case where an exception
            //  is thrown initializing the execution of "onStart" - this is caught
            //  below
        } catch (Exception e) {
            setStatus(FAILED);
            throw new StrategyException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#stop()
     */
    @Override
    public final void stop()
        throws Exception
    {
        // start and stop need to be protected in case someone tries to start a strategy that
        //  is already started or stop one that is already stopping or stopped - other state changes are
        //  internally dictated so, for example, the user could not direct a change from 
        //  COMPILING to STARTING except through start
        if(!getStatus().canChangeStatusTo(STOPPING)) {
            StrategyModule.log(LogEvent.warn(CANNOT_CHANGE_STATE,
                                             String.valueOf(this),
                                             getStatus(),
                                             STOPPING),
                               this);
            throw new ModuleStateException(new I18NBoundMessage2P(STRATEGY_STILL_RUNNING,
                                                                  this.toString(),
                                                                  getStatus()));
        }
        // if the strategy is at FAILED or STOPPED, this is not an error case to now try to stop it, but nothing
        //  more needs (or is allowed) to be done (and the status should not change)
        if(getStatus().equals(FAILED) ||
           getStatus().equals(STOPPED)) {
            return;
        }
        try {
            setStatus(STOPPING);
            getExecutor().stop();
            // intentionally not setting status to "STOPPED" because the
            //  "onStop" method, successful completion of which is required
            //  to be in "STOPPED" status, is being executed asynchronously.
            //  it is the responsibility of the "onStop" executor to determine
            //  the status of the strategy - except in the case where an exception
            //  is thrown initializing the execution of "onStop" - this is caught
            //  below
        } catch (Exception e) {
            setStatus(FAILED);
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#dataReceived(java.lang.Object)
     */
    @Override
    public final void dataReceived(Object inData)
    {
        // make sure that the strategy is in a state to receive incoming data
        if(!getStatus().canReceiveData()) {
            StrategyModule.log(LogEvent.warn(INVALID_STATUS_TO_RECEIVE_DATA,
                                             String.valueOf(this),
                                             String.valueOf(inData),
                                             getStatus()),
                               this);
            return;
        }
        String method = "onOther"; //$NON-NLS-1$
        try {
            RunningStrategy runningStrategy = getRunningStrategy();
            if(inData instanceof AskEvent) {
                method = "onAsk"; //$NON-NLS-1$
                runningStrategy.onAsk((AskEvent)inData);
                return;
            }
            if(inData instanceof BidEvent) {
                method = "onBid"; //$NON-NLS-1$
                runningStrategy.onBid((BidEvent)inData);
                return;
            }
            if(inData instanceof MarketstatEvent) {
                method = "onMarketstat"; //$NON-NLS-1$
                runningStrategy.onMarketstat((MarketstatEvent)inData);
                return;
            }
            if(inData instanceof OrderCancelReject) {
                method = "onCancelReject"; //$NON-NLS-1$
                runningStrategy.onCancelReject((OrderCancelReject)inData);
                return;
            }
            if(inData instanceof ExecutionReport) {
                method = "onExecutionReport"; //$NON-NLS-1$
                if(runningStrategy instanceof AbstractRunningStrategy) {
                    ((AbstractRunningStrategy)runningStrategy).onExecutionReportRedirected((ExecutionReport)inData);
                } else {
                    runningStrategy.onExecutionReport((ExecutionReport)inData);
                }
                return;
            }
            if(inData instanceof TradeEvent) {
                method = "onTrade"; //$NON-NLS-1$
                runningStrategy.onTrade((TradeEvent)inData);
                return;
            }
            // catch-all for every other type of data
            runningStrategy.onOther(inData);
        } catch (Exception e) {
            Executor executor = getExecutor();
            String methodName = method;
            String exceptionTranslation = e.toString();
            if(executor != null) {
                methodName = getExecutor().translateMethodName(method);
                exceptionTranslation = getExecutor().interpretRuntimeException(e);
            }
            StrategyModule.log(LogEvent.warn(RUNTIME_ERROR,
                                             String.valueOf(this),
                                             methodName,
                                             exceptionTranslation),
                               this);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getCode()
     */
    @Override
    public final String getScript()
    {
        return code;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getLanguage()
     */
    @Override
    public final Language getLanguage()
    {
        return language;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getName()
     */
    @Override
    public final String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getParameters()
     */
    @Override
    public final Properties getParameters()
    {
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getStatus()
     */
    @Override
    public final Status getStatus()
    {
        return status;
    }
    /**
     * Get the outboundServicesProvider value.
     *
     * @return a <code>OutboundServices</code> value
     */
    @Override
    public final OutboundServicesProvider getOutboundServicesProvider()
    {
        return outboundServicesProvider;
    }
    /**
     * Get the inboundServicesProvider value.
     *
     * @return a <code>StrategyImpl</code> value
     */
    public final InboundServicesProvider getInboundServicesProvider()
    {
        return inboundServicesProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getDefaultNamespace()
     */
    @Override
    public final String getDefaultNamespace()
    {
        return defaultNamespace;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#getExecutor()
     */
    @Override
    public final Executor getExecutor()
    {
        return executor;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        if(description == null) {
            description = String.format("%s Strategy %s(%s)", //$NON-NLS-1$
                                        getLanguage().toString(),
                                        getName(),
                                        getUniqueIdentifier());
        }
        return description;
    }
    /**
     * Create a new StrategyImpl instance.
     * 
     * @param inName a <code>String</code> value
     * @param inUniqueIdentifier a <code>String</code> value
     * @param inType a <code>Language</code> value
     * @param inSource a <code>File</code> value
     * @param inParameters a <code>Properties</code> value
     * @param inNamespace a <code>String</code> value 
     * @param inOutboundServicesProvider an <code>OutboundServices</code> value
     * @param inInboundServicesProvider an <code>InboundServices</code> value
     * @throws IOException if the given <code>File</code> could not be resolved
     */
    StrategyImpl(String inName,
                 String inUniqueIdentifier,
                 Language inType,
                 File inSource,
                 Properties inParameters,
                 String inNamespace,
                 OutboundServicesProvider inOutboundServicesProvider,
                 InboundServicesProvider inInboundServicesProvider)
        throws IOException
    {
        status = UNSTARTED;
        name = inName;
        uniqueIdentifier = inUniqueIdentifier;
        language = inType;
        source = inSource;
        if(inParameters == null) {
            parameters = new Properties();
        } else {
            parameters = new Properties(inParameters);
        }
        outboundServicesProvider = inOutboundServicesProvider;
        inboundServicesProvider = inInboundServicesProvider;
        code = fileToString(getSource());
        defaultNamespace = inNamespace;
    }
    /**
     * Get the uniqueIdentifier value.
     *
     * @return a <code>String</code> value
     */
    final String getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }
    /**
     * Get the source value.
     *
     * @return a <code>File</code> value
     */
    final File getSource()
    {
        return source;
    }
    /**
     * Get the runningStrategy value.
     *
     * @return a <code>RunningStrategy</code> value
     */
    final RunningStrategy getRunningStrategy()
    {
        return runningStrategy;
    }
    /**
     * Returns all currently running strategies. 
     *
     * @return a <code>Set&lt;StrategyImpl&gt;</code> value
     */
    static Set<StrategyImpl> getRunningStrategies()
    {
        synchronized(runningStrategies) {
            return new HashSet<StrategyImpl>(runningStrategies);
        }
    }
    /**
     * Sets the status of the strategy.
     *
     * @param inStatus a <code>Status</code> value
     */
    final void setStatus(Status inStatus)
    {
        assert(status.canChangeStatusTo(inStatus));
        Status oldStatus = status;
        status = inStatus;
        // update the running strategy collection
        if(status.isRunning()) {
            synchronized(runningStrategies) {
                runningStrategies.add(this);
            }
        } else {
            synchronized(runningStrategies) {
                runningStrategies.remove(this);
            }
        }
        // notify that the status has changed
        getOutboundServicesProvider().statusChanged(oldStatus,
                                                    inStatus);
    }
    /**
     * Sets the runningStrategy value.
     *
     * @param a <code>RunningStrategy</code> value
     */
    private void setRunningStrategy(RunningStrategy inRunningStrategy)
    {
        runningStrategy = inRunningStrategy;
    }
    /**
     * Sets the executor value.
     *
     * @param an <code>Executor</code> value
     */
    private void setExecutor(Executor inExecutor)
    {
        executor = inExecutor;
    }
    /**
     * Reads the given <code>File</code> and renders its contents as a <code>String</code>.
     *
     * @param inFile a <code>File</code> value
     * @return a <code>String</code> value
     * @throws IOException if the <code>File</code> can not be read
     */
    private String fileToString(File inFile)
        throws IOException
    {
        return FileUtils.readFileToString(inFile);
    }
    /**
     * all strategies that are in RUNNING state 
     */
    private static final Set<StrategyImpl> runningStrategies = new HashSet<StrategyImpl>();
    /**
     * the user-applied name of the strategy.  this name has no strict correlation to any artifact declared by the embedded strategy itself.
     */
    final String name;
    /**
     * the type of the strategy being executed
     */
    private final Language language;
    /**
     * a reference to the actual code of the strategy
     */
    private final File source;
    /**
     * the actual code of the strategy
     */
    private final String code;
    /**
     * the set of parameters to pass to the strategy.  some of the values contained within may be meta-data that is relevant to the strategy manager (this object) rather than the strategy itself.
     */
    private final Properties parameters;
    /**
     * the provider of services for outgoing data via the strategy agent framework
     */
    private final OutboundServicesProvider outboundServicesProvider;
    /**
     * the provider of services for incoming data
     */
    private final InboundServicesProvider inboundServicesProvider;
    /**
     * the default namespace for this strategy
     */
    private final String defaultNamespace;
    /**
     * the value that uniquely identifies this strategy to the system within the scope of this JVM execution
     */
    private final String uniqueIdentifier;
    /**
     * the executor responsible for execution of this strategy
     */
    private Executor executor;
    /**
     * interface to the embedded running strategy object - this object is created by the execution engine
     */
    private RunningStrategy runningStrategy;
    /**
     * the strategy status
     */
    private Status status;
    /**
     * description of this object initialized when needed
     */
    private String description;
}

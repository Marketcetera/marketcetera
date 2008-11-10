package org.marketcetera.strategy;

import java.io.File;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;

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
 * @since $Release$
 */
@ClassVersion("$Id$")
class StrategyImpl
        implements Strategy
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public final boolean isRunning()
    {
        return running;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        // TODO retrieve strategy source
        // TODO locate strategy executor
        // TODO execute strategy
        running = true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            // halt strategy executor
        } finally {
            running = false;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Strategy#dataReceived(java.lang.Object)
     */
    @Override
    public void dataReceived(Object inData)
    {
    }
    /**
     * Create a new StrategyImpl instance.
     *
     * @param inName a <code>String</code> value
     * @param inUniqueIdentifier a <code>String</code> value
     * @param inType a <code>Language</code> value
     * @param inSource a <code>File</code> value
     * @param inParameters a <code>Properties</code> value
     * @param inOutboundServicesProvider an <code>OutboundServices</code> value
     */
    StrategyImpl(String inName,
                 String inUniqueIdentifier,
                 Language inType,
                 File inSource,
                 Properties inParameters,
                 OutboundServices inOutboundServicesProvider)
    {
        name = inName;
        uniqueIdentifier = inUniqueIdentifier;
        language = inType;
        source = inSource;
        parameters = inParameters;
        outboundServicesProvider = inOutboundServicesProvider;
        running = false;
    }
    /**
     * Get the name value.
     *
     * @return a <code>StrategyImpl</code> value
     */
    String getName()
    {
        return name;
    }
    /**
     * Get the uniqueIdentifier value.
     *
     * @return a <code>String</code> value
     */
    String getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }
    /**
     * Get the language value.
     *
     * @return a <code>String</code> value
     */
    Language getLanguage()
    {
        return language;
    }
    /**
     * Get the source value.
     *
     * @return a <code>File</code> value
     */
    File getSource()
    {
        return source;
    }
    /**
     * Get the parameters value.
     *
     * @return a <code>Properties</code> value
     */
    Properties getParameters()
    {
        return parameters;
    }
    /**
     * Get the outboundServicesProvider value.
     *
     * @return a <code>OutboundServices</code> value
     */
    OutboundServices getOutboundServicesProvider()
    {
        return outboundServicesProvider;
    }
    /**
     * the user-applied name of the strategy.  this name has no strict correlation to any artifact declared by the embedded strategy itself.
     */
    private final String name;
    /**
     * the type of the strategy being executed
     */
    private final Language language;
    /**
     * the actual code of the strategy
     */
    private final File source;
    /**
     * the set of parameters to pass to the strategy.  some of the values contained within may be meta-data that is relevant to the strategy manager (this object) rather than the strategy itself.
     */
    private final Properties parameters;
    /**
     * the provider of services for outgoing data via the strategy agent framework
     */
    private final OutboundServices outboundServicesProvider;
    /**
     * the value that uniquely identifies this strategy to the system within the scope of this JVM execution
     */
    private final String uniqueIdentifier;
    /**
     * the state of the embedded strategy
     */
    private boolean running;
}

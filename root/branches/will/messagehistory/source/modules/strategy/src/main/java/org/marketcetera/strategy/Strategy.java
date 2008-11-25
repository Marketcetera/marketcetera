package org.marketcetera.strategy;

import java.util.Properties;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * A <code>Strategy</code> object to be executed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface Strategy
{
    /**
     * Sends data received from an external source to a strategy.
     *
     * @param inData an <code>Object</code> value
     */
    public void dataReceived(Object inData);
    /**
     * Gets the script to execute.
     *
     * @return a <code>String</code> value
     */
    public String getScript();
    /**
     * Gets the language in which to interpret the strategy script.
     *
     * @return a <code>Language</code> value
     */
    public Language getLanguage();
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * Gets the parameters to pass to the strategy script.
     *
     * @return a <code>Properties</code> value
     */
    public Properties getParameters();
    /**
     * Gets the classpath to pass to the strategy script compiler.
     *
     * @return a <code>String[]</code> value
     */
    public String[] getClasspath();
    /**
     * Starts the execution of the strategy.
     *
     * @throws StrategyException if the strategy cannot start
     */
    public void start()
        throws StrategyException;
    /**
     * Stops the execution of the strategy.
     *
     * @throws StrategyException if an error occurred while stopping the strategy
     */
    public void stop()
        throws StrategyException;
    /**
     * Gets the strategy status.
     *
     * @return a <code>Status</code> value
     */
    public Status getStatus();
    /**
     * Returns the services provider for this <code>Strategy</code> to use.
     *
     * @return an <code>OutboundServicesProvider</code> value
     */
    public OutboundServicesProvider getServicesProvider();
}

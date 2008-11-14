package org.marketcetera.strategy;

import java.util.Properties;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.DataRequest;

/* $License$ */

/**
 * Base class for running strategies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractRunningStrategy
        implements RunningStrategy
{
    /**
     * Gets the shared properties store.
     * 
     * <p>All running strategies have access to this properties store.  Changes
     * made to the object returned from this method will effect the original
     * object.
     *
     * @return a <code>Properties</code> value
     */
    static final Properties getProperties()
    {
        return properties;
    }
    /**
     * 
     *
     *
     * @param inStrategy
     */
    final void setStrategy(Strategy inStrategy)
    {
        strategy = inStrategy;
    }
    /**
     * Sets the given key to the given value.
     *
     * <p>All running strategies have access to this properties store.
     * 
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    protected static void setProperty(String inKey,
                                      String inValue)
    {
        if(inKey == null) {
            // TODO warn against null keys
            return;
        }
        if(inValue == null) {
            properties.remove(inKey);
            return;
        }
        properties.setProperty(inKey,
                               inValue);
    }
    /**
     * Gets the parameter associated with the given name.
     *
     * @param inName a <code>String</code> value containing the key of a parameter key/value value
     * @return a <code>String</code> value or null if no parameter is associated with the given name
     */
    protected final String getParameter(String inName)
    {
        Properties parameters = strategy.getParameters();
        if(parameters == null) {
            // TODO warn that no parameters are available
            return null;
        }
        return parameters.getProperty(inName);
    }
    /**
     * Requests market data as detailed in the given <code>DataRequest</code> from the given source. 
     *
     * @param inRequest a <code>DataRequest</code> value
     * @param inSource a <code>String</code> value
     * @return a <code>long</code> value indicating the identifier of the data request
     */
    protected final long requestMarketData(DataRequest inRequest,
                                           String inSource)
    {
        return strategy.getServicesProvider().requestMarketData(inRequest,
                                                                inSource);
    }
    /**
     * Cancels the given market data request.
     *
     * @param inRequestID a <code>long</code> value containing the identifier of the data request to cancel
     */
    protected final void cancelMarketDataRequest(long inRequestID)
    {
        strategy.getServicesProvider().cancelMarketDataRequest(inRequestID);
    }
    /**
     * Cancels all market data requests from this strategy.
     */
    protected final void cancelAllMarketDataRequests()
    {
        strategy.getServicesProvider().cancelAllMarketDataRequests();
    }
    /**
     * common properties store shared among all strategies
     */
    private static final Properties properties = new Properties();
    /**
     * static strategy object of which this object is a running representation
     */
    private Strategy strategy;
}

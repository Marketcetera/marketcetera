package org.marketcetera.strategy;

import java.util.Properties;

import org.marketcetera.core.ClassVersion;

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
     * common properties store shared among all strategies
     */
    private static final Properties properties = new Properties();
    /**
     * the parameters passed to the strategy
     */
    private Properties parameters;
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
     * Gets the parameter associated with the given name.
     *
     * @param inName a <code>String</code> value containing the key of a parameter key/value value
     * @return a <code>String</code> value or null if no parameter is associated with the given name
     */
    protected final String getParameter(String inName)
    {
        if(parameters == null) {
            // TODO warn that no parameters are available
            return null;
        }
        return parameters.getProperty(inName);
    }
    /**
     * Sets the start-up parameters to make available to the strategy.
     *
     * @param inProperties a <code>Properties</code> value
     */
    void setParameters(Properties inProperties)
    {
        parameters = inProperties;
    }
}

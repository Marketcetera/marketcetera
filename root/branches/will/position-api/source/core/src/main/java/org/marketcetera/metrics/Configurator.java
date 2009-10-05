package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import java.util.Properties;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;

/* $License$ */
/**
 * Provides static mechanisms to configure the metrics instrumentation.
 * By default, this mechanism looks for a file named
 * {@link #METRICS_PROPERTIES_FILE_NAME metc_metrics.properties} in the
 * classpath and loads the property values from it.
 * <p>
 * If desired, a custom mechanism can be provided to resolve property
 * values by subclassing this class and setting that instance value via
 * {@link #setInstance(Configurator)}.
 * <p>
 * The property {@link #PROPERTY_METRICS_ENABLE metc.metrics.enable} has
 * special significance in that it's used to enable / disable instrumentation.
 * <p>
 * Note that these property values are only read once. These values are
 * not expected to change after they have been initialized.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class Configurator {

    /**
     * The property name used to enable / disable metrics.
     */
    public static final String PROPERTY_METRICS_ENABLE =
            "metc.metrics.enable";  //$NON-NLS-1$

    /**
     * Returns the value for the supplied property name.
     *
     * @param inName the property name.
     * @param inDefaultValue the default value for the property, if the
     * property value is not available or if a configurator instance is not
     * configured.
     *
     * @return the property value.
     */
    public static String getProperty(String inName, String inDefaultValue) {
        String value = null;
        Configurator instance = getInstance();
        if (instance != null && inName != null) {
            value = instance.getPropertyValue(inName);
        }
        value = value == null
                ? inDefaultValue
                : value;
        SLF4JLoggerProxy.debug(Configurator.class,
                "Property Name {}, Value {}",   //$NON-NLS-1$
                inName, value);
        
        sReportedValues.put(inName, value);
        return value;
    }

    /**
     * Returns the set of properties and their values as have been returned
     * by invocations to {@link #getProperty(String, String)}.
     * <p>
     * Only the reported values can be returned as the Configurator
     * doesn't provide means to list all the available properties.
     * <p>
     * The goal of this API is to enable runtime verification of the
     * property values. So that anybody running performance tests can
     * be sure how the instrumentation was configured when running
     * the performance tests.
     *
     * @return the set of properties and their reported values.
     */
    public static Map<String,String> getReportedValues() {
        synchronized (sReportedValues) {
            return new HashMap<String,String>(sReportedValues);
        }
    }

    /**
     * Sets the configurator instance that should be used for resolving
     * property values.
     *
     * @param inInstance the configurator instnace. If set to null, all
     * property values resolve to a null value.s
     */
    public synchronized static void setInstance(Configurator inInstance) {
        sInstance = inInstance;
        sInitialized = true;
    }

    /**
     * Clears the reported values. This method has been added for testing.
     * Its not meant for use by the clients of this class.
     */
    static void clearReportedValues() {
        sReportedValues.clear();
    }

    /**
     * Fetches the current configurator instance that should be used for
     * resolving property values.
     * <p>
     * If the configurator has not been initialized, a property file based
     * configurator, that resolves property values based on a file named
     * {@link #METRICS_PROPERTIES_FILE_NAME} in the classpath, is initialized.
     *
     * @return the current configurator instance.
     */
    private synchronized static Configurator getInstance() {
        if(!sInitialized) {
            setInstance(new PropertyFileConfigurator(Configurator.class.
                    getResourceAsStream(METRICS_PROPERTIES_FILE_NAME)));
        }
        return sInstance;
    }

    /**
     * The current configurator instance.
     */
    private static Configurator sInstance;
    /**
     * If the configurator has been initialized.
     */
    private static boolean sInitialized = false;
    /**
     * The property key value pairs that have been queried via
     * {@link #getProperty(String, String)}.  
     */
    private static final Map<String,String> sReportedValues =
            Collections.synchronizedMap(new HashMap<String,String>());

    /**
     * This method is implemented by the subclasses to fetch the property
     * value for the supplied property name.
     *
     * @param inName the property name.
     *
     * @return property value if available, null otherwise.
     */
    protected abstract String getPropertyValue(String inName);

    /**
     * A configurator that initializes itself from a stream that contains
     * properties as specified by {@link Properties#load(java.io.InputStream)}.
     */
    private static class PropertyFileConfigurator extends Configurator {
        /**
         * Creates an instance.
         *
         * @param inStream the input stream that contains the property
         * key value pairs. If null, the configurator return null values
         * for all property names.
         */
        public PropertyFileConfigurator(InputStream inStream) {
            mProperties = new Properties();
            if(inStream == null) {
                return;
            }
            try {
                try {
                    mProperties.load(inStream);
                } finally {
                    inStream.close();
                }
            } catch (IOException e) {
                Messages.LOG_ERR_LOADING_PROPERTIES.warn(this, e, inStream);
            }
        }

        @Override
        protected String getPropertyValue(String inName) {
            return mProperties.getProperty(inName);
        }
        private final Properties mProperties;
    }

    /**
     * The name of the properties file that is used to resolve property values.
     * This properties file is looked for in the application's classpath.
     */
    public static final String METRICS_PROPERTIES_FILE_NAME =
            "/metc_metrics.properties";  //$NON-NLS-1$
}

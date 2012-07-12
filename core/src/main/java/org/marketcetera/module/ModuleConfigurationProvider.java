package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Instances of this classes provide means to provide default
 * property values for configuration attributes of module
 * factories and module instances.
 * <p>
 * Whenever a new instance of a module factory or a module
 * instance is created, if the factory or the module exports
 * an MXBean interface, the module framework, will figure out
 * all the writable attributes (of java primitive and string types)
 * on that interface and query the module configuration provider for
 * any default configuration values for each one of them.
 * <p>
 * If the module configuration provider provides a non-null value
 * for an attribute, the module framework converts that value from
 * string to the actual attribute type, if it supports conversion
 * for that particular type, and sets the converted value on the MX bean
 * for the factory or the module instance by invoking the appropriate
 * property setter.
 * <p>
 * The default configurations are applied on the factories right
 * after they are created but before they are invoked to create
 * any instances.
 * <p>
 * The default configurations are applied on module instances,
 * right after they are created but before they are started.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public interface ModuleConfigurationProvider {
    /**
     * Returns the default value, if available, for the specified
     * attribute of the supplied module provider or module instance URN.
     * This method returns null, if no default value is available.
     *
     * @param inURN the module provider or instance URN.
     * @param inAttribute the writable attribute name as reported
     * by the MBeanInfo.
     *
     * @return the default value of the attribute, if available,
     * null otherwise.
     *
     * @throws ModuleException if there was a failure fetching
     * the default value.
     */
    public String getDefaultFor(ModuleURN inURN, String inAttribute)
            throws ModuleException;

    /**
     * Refreshes the module configuration provider. When invoked, the
     * configuration provider should re-read all of its configuration
     * from persistent store discarding any cached state if it has any.
     *
     * This method is invoked by the module framework when its asked
     * to refresh itself.
     *
     * @throws ModuleException if the configuration ran into errors 
     * refreshing its state.
     *
     * @see ModuleManager#refresh() 
     */
    public void refresh() throws ModuleException;
}

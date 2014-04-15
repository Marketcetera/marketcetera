package org.marketcetera.saclient.rpc;

import java.util.List;
import java.util.Map;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides {@link SAService} services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface SAServiceAdapter
{
    /**
     * Gets the list of providers.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    List<ModuleURN> getProviders();
    /**
     * Gets the list of instances for the given provider.
     *
     * @param inProvider a <code>ModuleURN</code> value
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    List<ModuleURN> getInstances(ModuleURN inProvider);
    /**
     * Gets the module info for the given instance.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>ModuleInfo</code> value
     */
    ModuleInfo getModuleInfo(ModuleURN inInstance);
    /**
     * Starts the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     */
    void start(ModuleURN inInstance);
    /**
     * Stops the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     */
    void stop(ModuleURN inInstance);
    /**
     * Deletes the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     */
    void delete(ModuleURN inInstance);
    /**
     * Gets the properties of the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>Map&lt;String,Object&gt;</code> value
     */
    Map<String,Object> getProperties(ModuleURN inInstance);
    /**
     * Sets the properties of the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @param inProperties a <code>Map&lt;String,Object&gt;</code> value
     * @return a <code>Map&lt;String,Object&gt;</code> value
     */
    Map<String,Object> setProperties(ModuleURN inInstance,
                                     Map<String,Object> inProperties);
    /**
     * Creates a strategy with the given parameters.
     *
     * @param inParameters a <code>CreateStrategyParameters</code> value
     * @return a <code>ModuleURN</code> value
     */
    ModuleURN createStrategy(CreateStrategyParameters inParameters);
    /**
     * Gets the strategy parameters of the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>CreateStrategyParameters</code> value
     */
    CreateStrategyParameters getStrategyCreateParms(ModuleURN inInstance);
    /**
     * Sends the data to the SA for distribution.
     *
     * @param inData an <code>Object</code> value
     */
    void sendData(Object inData);
}

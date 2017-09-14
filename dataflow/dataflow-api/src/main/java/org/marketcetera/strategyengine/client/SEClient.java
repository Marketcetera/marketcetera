package org.marketcetera.strategyengine.client;

import java.util.List;

import org.marketcetera.core.BaseClient;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * The interface to the set of remote services available from a remote strategy engine.
 *
 * @author anshul@marketcetera.com
 * @author colin@marketcetera.com
 * @version $Id: SEClient.java 17242 2016-09-02 16:46:48Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: SEClient.java 17242 2016-09-02 16:46:48Z colin $")
public interface SEClient
        extends BaseClient,ConnectionStatusPublisher,DataPublisher
{
    /**
     * Returns the list of providers available.
     * 
     * <p>If no providers are available this list is empty.
     * 
     * @return the list of providers available.
     * @throws ConnectionException if there were errors completing the operation.
     */
    List<ModuleURN> getProviders();
    /**
     * Returns the list of module instances available.
     *
     * <p>If no instances are found, an empty list is returned.
     *
     * @param inProviderURN the URN of the provider whose instances should be returned. If null, all available instances are returned.
     * @return the list of module instances available.
     */
    List<ModuleURN> getInstances(ModuleURN inProviderURN);
    /**
     * Returns the module information for the module instance with the supplied URN.
     *
     * @param inURN the module instance URN. Cannot be null.
     * @return the module info.
     */
    ModuleInfo getModuleInfo(ModuleURN inURN);
    /**
     * Starts the module instance having the supplied URN.
     * 
     * <p>Only strategy module instances can be started. Attempts to start
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be started. Cannot be null.
     */
    void start(ModuleURN inURN);
    /**
     * Stops the module instance having the supplied URN.
     * 
     * <p>Only strategy module instances can be stopped. Attempts to stop
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be stopped. Cannot be null.
     */
    void stop(ModuleURN inURN);
    /**
     * Deletes the module instance having the supplied URN.
     * 
     * <p>Only strategy module instances can be deleted. Attempts to delete
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be deleted. Cannot be null.
     */
    void delete(ModuleURN inURN);
    /**
     * Sends the given object to the Strategy Agent where registered listeners will receive it.
     *
     * @param inData an <code>Object</code> value
     */
    void sendData(Object inData);
}

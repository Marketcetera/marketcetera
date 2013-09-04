package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ModuleInfo;

import java.util.List;
import java.util.Map;

/* $License$ */
/**
 * The interface to the set of remote services available from a remote
 * strategy agent.
 * <p>
 * When one is done communicating with the remote strategy agent,
 * {@link #close()} should be invoked to close the connection to the remote
 * strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface SAClient {
    /**
     * Returns the list of providers available.
     * <p>
     * If no providers are available this list is empty.
     * 
     * @return the list of providers available.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public List<ModuleURN> getProviders() throws ConnectionException;

    /**
     * Returns the list of module instances available.
     *
     * @param inProviderURN the URN of the provider whose instances
     * should be returned. If null, all available instances are returned.
     * <p>
     * If no instances are found, an empty list is returned.
     *
     * @return the list of module instances available.
     *
     * @throws ConnectionException if there were errors completing the operation.
     *
     */
    public List<ModuleURN> getInstances(ModuleURN inProviderURN)
            throws ConnectionException;

    /**
     * Returns the module information for the module instance with the
     * supplied URN.
     *
     * @param inURN the module instance URN. Cannot be null.
     *
     * @return the module info.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public ModuleInfo getModuleInfo(ModuleURN inURN) throws ConnectionException;

    /**
     * Starts the module instance having the supplied URN.
     * <p>
     * Only strategy module instances can be started. Attempts to start
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be started. Cannot be null.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public void start(ModuleURN inURN) throws ConnectionException;

    /**
     * Stops the module instance having the supplied URN.
     * <p>
     * Only strategy module instances can be stopped. Attempts to stop
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be stopped. Cannot be null.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public void stop(ModuleURN inURN) throws ConnectionException;
    /**
     * Deletes the module instance having the supplied URN.
     * <p>
     * Only strategy module instances can be deleted. Attempts to delete
     * modules that are not strategy modules will fail.
     *
     * @param inURN the URN of the module that needs to be deleted. Cannot be null.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public void delete(ModuleURN inURN) throws ConnectionException;

    /**
     * Fetches all the available properties of the module with the supplied URN.
     * <p>
     * Note that the property values may not be in the same type as is
     * declared on the module's MXBean. The return types are open data types.
     * Refer to the type mapping rules in {@link javax.management.MXBean}
     * documentation to figure out the return type for a particular property
     * type. For example, enum property types are returned as string values.
     *
     * @param inURN the module URN. Cannot be null.
     *
     * @return All the available properties of that module.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public Map<String,Object> getProperties(ModuleURN inURN)
            throws ConnectionException;

    /**
     * Sets the supplied properties of the module.
     * <p>
     * Only the following properties of strategy module can be set. Attempts to set
     * the properties of a module that is not a strategy module or a strategy
     * module property that is not in this list will fail.
     * <ul>
     * <li>Parameters</li>
     * <li>RoutingOrdersToORS</li>
     * </ul>
     * <b>Note:</b> Be sure to verify the values in the returned map to
     * be sure that each property was set successfully. If there's a failure
     * setting a property, the method doesn't fail. Instead, the value of
     * the property that could not be set is a
     * {@link org.marketcetera.util.ws.wrappers.RemoteProperties} instance that has
     * details on the error encountered when setting the property.
     *
     * @param inURN the URN of the module whose properties are being set. Cannot be null.
     *
     * @param inProperties the new property values for the module. Cannot be null.
     *
     * @return the map of properties that were succesfully updated. In case
     * a particular property could not be updated, the value of that
     * property will contain a {@link org.marketcetera.util.ws.wrappers.RemoteProperties}
     * instance that has details on the failure.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public Map<String, Object> setProperties(ModuleURN inURN,
                                             Map<String,Object> inProperties)
            throws ConnectionException;

    /**
     * Creates a new strategy module using the given strategy creation parameters.
     *
     * @param inParameters the strategy creation parameters. Cannot be null.
     *
     * @return the created module's URN.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public ModuleURN createStrategy(CreateStrategyParameters inParameters)
            throws ConnectionException;

    /**
     * Fetches the strategy creation parameters (including the strategy script)
     * for the strategy with the supplied URN.
     * <p>
     * This method is made available to enable a client to export strategy
     * script contents. It's recommended that {@link #getProperties(ModuleURN)}
     * be used for fetching the current values of strategy properties as
     * {@link #getStrategyCreateParms(org.marketcetera.module.ModuleURN)}
     * may yield stale values for certain strategy attributes like
     * parameters. 
     *
     * @param inURN the strategy module instnace URN.  Cannot be null.
     *
     * @return the strategy creation parameters.
     *
     * @throws ConnectionException if there were errors completing the operation.
     */
    public CreateStrategyParameters getStrategyCreateParms(ModuleURN inURN)
            throws ConnectionException;
    /**
     * Sends the given object to the Strategy Agent where registered listeners will receive it.
     *
     * @param inData an <code>Object</code> value
     * @throws ConnectionException if there were errors completing the operation.
     */
    public void sendData(Object inData)
            throws ConnectionException;
    /**
     * Adds a data receiver so that it can receive all the data received
     * from the remote source that this client is connected to.
     * <p>
     * If the same receiver is added more than once, it will receive
     * data as many times as it's been added.
     * <p>
     * The receivers are notified in the reverse order of their addition.
     *
     * @param inReceiver the receiver to add. Cannot be null.
     */
    public void addDataReceiver(DataReceiver inReceiver);

    /**
     * Removes a data receiver that was previously added so that it no longer
     * receives data from the remote source.
     * <p>
     * If the receiver was added more than once, only its most
     * recently added occurrence will be removed.
     *
     * @param inReceiver the receiver to remove. Cannot be null.
     */
    public void removeDataReciever(DataReceiver inReceiver);

    /**
     * Adds a connection status listener so that it can receive connection
     * status notifications.
     * <p>
     * If the same listener is added more than once, it will receive
     * notifications as many times as it's been added.
     * <p>
     * The listeners are notified in the reverse order of their addition.
     *
     * @param inListener the listener to add. Cannot be null.
     */
    public void addConnectionStatusListener(ConnectionStatusListener inListener);

    /**
     * Removes a connection status listener that was added previously so that
     * it no longer receives connection status notifications.
     * <p>
     * If the listener was added more than once, only its most
     * recently added occurrence will be removed.
     *
     * @param inListener the listener to remove. Cannot be null.
     */
    public void removeConnectionStatusListener(ConnectionStatusListener inListener);

    /**
     * The parameters that were specified when connecting to the remote
     * strategy agent.
     *
     * @return the connection parameters.
     */
    public SAClientParameters getParameters();
    /**
     * Closes the connection to the remote strategy agent. The behavior
     * of the client after this method is invoked is undefined. If one
     * needs to reconnect to the server, a new instance of the client
     * should be created.
     * <p>
     * This operation does not throw any exceptions. If any failures
     * are encountered when closing the connection, they are logged.
     */
    public void close();
}

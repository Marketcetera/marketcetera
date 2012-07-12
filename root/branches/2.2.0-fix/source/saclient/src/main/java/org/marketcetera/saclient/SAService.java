package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ModuleInfo;

import javax.jws.WebService;
import javax.jws.WebParam;
import java.util.List;

/* $License$ */
/**
 * The web service interface to the remote services available from a
 * strategy agent.
 * <p>
 * This interface is not meant to be used by the clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
@WebService(targetNamespace = "http://marketcetera.org/services")
public interface SAService extends ServiceBase {
    /**
     * Returns the list of providers available at the strategy agent.
     *
     * @param inCtx the context
     *
     * @return the list of providers.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     *
     */
    public List<ModuleURN> getProviders(
            @WebParam(name = "context") ClientContext inCtx)
            throws RemoteException;

    /**
     * Returns the list of module instances available at the strategy agent.
     *
     * @param inCtx the context
     * @param inProviderURN the provider URN. If provided, only the instances
     * from the specified provider are returned. If null, all the available
     * instances are returned.
     *
     * @return the list of module instances.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     *
     */
    public List<ModuleURN> getInstances(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inProviderURN)
            throws RemoteException;

    /**
     * Gets the module info for the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     *
     * @return the module info.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public ModuleInfo getModuleInfo(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;

    /**
     * Starts the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public void start(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;

    /**
     * Stops the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public void stop(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;

    /**
     * Deletes the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public void delete(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;

    /**
     * Fetches the (MXBean) properties of the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     *
     * @return the property values of the module with the specified URN.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public MapWrapper<String,Object> getProperties(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;

    /**
     * Sets the properties of the module with the specified URN.
     *
     * @param inCtx the context.
     * @param inURN the module URN.
     * @param inProperties the module properties that need to be set.
     *
     * @return the map of the properties that were successfully updated.
     * If a particular could not be updated, its value will contain
     * the exception with the details on the failure.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public MapWrapper<String, Object> setProperties(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "urn") ModuleURN inURN,
            @WebParam(name = "properties") MapWrapper<String, Object> inProperties)
            throws RemoteException;

    /**
     * Creates a strategy.
     *
     * @param inCtx the context.
     * @param inParameters the strategy creation parameters.
     *
     * @return the URN of the strategy module created.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public ModuleURN createStrategy(
            @WebParam(name = "context") ClientContext inCtx,
            @WebParam(name = "parameters") CreateStrategyParameters inParameters)
            throws RemoteException;

    /**
     * Returns the parameters used to create a strategy.
     *
     * @param inServiceContext the context.
     * @param inURN the strategy module's URN.
     *
     * @return the parameters supplied when creating the strategy.
     *
     * @throws RemoteException if there were errors communicating with the
     * remote strategy agent.
     */
    public CreateStrategyParameters getStrategyCreateParms(
            @WebParam(name = "context") ClientContext inServiceContext,
            @WebParam(name = "urn") ModuleURN inURN)
            throws RemoteException;
    /**
     * Sends the given data to the strategy agent.
     * 
     * <p>Note that if the type of data sent is not a Java basic type, context for that type must
     * be added to both the client and server.
     *
     * @param inServiceContext a <code>ClientContext</code> value
     * @param inData an <code>Object</code> value
     * @throws RemoteException if there were errors communicating with the remote strategy agent.
     */
    public void sendData(@WebParam(name = "context") ClientContext inServiceContext,
                         @WebParam(name = "data") Object inData)
            throws RemoteException;
}

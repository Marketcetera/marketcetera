package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.NoMoreIDsException;

import javax.management.*;
import java.util.*;
import java.lang.management.ManagementFactory;
import java.io.IOException;

/* $License$ */
/**
 * The main class for the module framework. This class provides the
 * API to the module framework.
 * <p> 
 * The {@link #init()} method should be invoked to get the module manager
 * to discover and instantiate providers and any module instances, after
 * its created. If a {@link #getConfigurationProvider()} has been setup
 * before the <code>init()</code> method is invoked, it will be used
 * to provide default property values to various module providers and
 * instances as they are created.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class ModuleManager {
    /**
     * Creates an instance that uses the same classloader as this class
     * to load module providers.
     */
    public ModuleManager() {
        mLoader = ServiceLoader.load(ModuleFactory.class,
                getClass().getClassLoader());
    }

    /**
     * Creates an instance that uses the supplied classloader to
     * load module providers.
     *
     * @param inClassLoader the classloader to use for loading module
     * providers.
     */
    public ModuleManager(ClassLoader inClassLoader) {
        mLoader = ServiceLoader.load(ModuleFactory.class,
                inClassLoader);
    }

    /**
     * Returns a list of URNs of available module providers.
     *
     * @return the list of URNs of available module providers.
     */
    public List<ModuleURN> getProviders() {
        ArrayList<ModuleURN> list = new ArrayList<ModuleURN>();
        synchronized (mOperationsLock) {
            for(ModuleURN u: mModuleFactories.keySet()) {
                list.add(u);
            }
        }
        return list;
    }

    /**
     * Returns detailed information on a provider, given its URN.
     *
     * @param inProviderURN the provider URN
     *
     * @return the provider details.
     *
     * @throws ProviderNotFoundException if a provider with the
     * supplied URN does not exist.
     * @throws InvalidURNException if the supplied provider URN
     * is not a valid URN.
     */
    public ProviderInfo getProviderInfo(ModuleURN inProviderURN)
            throws ProviderNotFoundException, InvalidURNException {
        URNUtils.validateProviderURN(inProviderURN);
        ModuleFactory factory = getModuleFactory(inProviderURN);
        return new ProviderInfo(
                factory.getProviderURN(),
                factory.getParameterTypes(),
                factory.isMultipleInstances(),
                factory.isAutoInstantiate(),
                factory.getProviderDescription().
                        getText(ActiveLocale.getLocale()));
    }

    /**
     * Returns the URNs of available module instances.
     * If a module provider URN is provided, only the module instances
     * from that provider are returned, otherwise, all available module
     * instance URNs are returned back.
     *
     * The provider URN is validated to ensure that it's a valid provider URN.
     * If no provider with the supplied URN exists, no modules will be returned.
     *
     * @param inProviderURN the providerURN whose module
     * instances are requested. If null, all module instances
     * are returned.
     *
     * @return the list or URNs for all the modules
     *
     * @throws InvalidURNException if the supplied provider URN
     * is not a valid URN.
     */
    public List<ModuleURN> getModuleInstances(ModuleURN inProviderURN)
            throws InvalidURNException {
        if (inProviderURN != null) {
            URNUtils.validateProviderURN(inProviderURN);
        }
        ArrayList<ModuleURN> urns = new ArrayList<ModuleURN>();
        synchronized (mOperationsLock) {
            for(ModuleURN moduleURI: mModules.getAllURNs()) {
                if(inProviderURN == null || inProviderURN.parentOf(moduleURI)) {
                    urns.add(moduleURI);
                }
            }
        }
        return urns;
    }

    /**
     * Creates a module instance. An attempt to create a module instance
     * for a provider that only supports singleton instances will fail, if
     * a singleton instance already exists.
     *
     * @param inProviderURN The provider URN. The value supplied should match
     * the value returned by a module factory's
     * {@link ModuleFactory#getProviderURN()} that
     * is available in the system.
     * @param inParameters the parameters that are needed to instantiate
     * the module. The parameter types are verified against the types
     * that are advertised by the Module Factory via
     * {@link ModuleFactory#getParameterTypes()}
     *
     * @return the instantiated module's URN
     *
     * @throws ModuleException if there were errors creating the module
     *
     * @see #getProviderInfo(ModuleURN)
     */
    public ModuleURN createModule(ModuleURN inProviderURN, Object... inParameters)
            throws ModuleException {
        return createModuleImpl(inProviderURN, inParameters).getURN();
    }

    /**
     * Deletes the module identified by the supplied module URN.
     * The module is stopped if its already running.
     * Singleton instances of a module cannot be deleted.
     *
     * @param inModuleURN the module URN, that uniquely identifies
     * the module being deleted.
     *
     * @throws ModuleException if a module with the
     * supplied URN cannot be deleted
     * @throws InvalidURNException if the supplied module URN is
     * not a valid URN.
     * @throws ModuleNotFoundException if the module matching
     * the URN was not found.
     */
    public void deleteModule(ModuleURN inModuleURN)
            throws ModuleException {
        synchronized (mOperationsLock) {
            // URN validation already done in getModule()
            Module module = getModule(inModuleURN);
            ModuleURN providerURN = module.getURN().parent();
            assert providerURN != null;
            if(!getModuleFactory(providerURN).isMultipleInstances()) {
                throw new ModuleException(new I18NBoundMessage1P(
                        Messages.CANNOT_DELETE_SINGLETON,
                        inModuleURN.toString()));
            }
            if(module.getState().isStarted()) {
                stopModule(module);
            }
            ObjectName objectName = inModuleURN.toObjectName();
            try {
                if(getMBeanServer().isRegistered(objectName)) {
                    getMBeanServer().unregisterMBean(objectName);
                }
            } catch (JMException e) {
                throw new BeanRegistrationException(e,
                        new I18NBoundMessage1P(Messages.MODULE_DELETE_ERROR_MXBEAN_UNREG,
                                inModuleURN.getValue()));
            }
            mModules.remove(module.getURN());
            Messages.LOG_MODULE_DELETED.info(this, inModuleURN);
        }
    }

    /**
     * Returns detailed information on the module given its URN.
     *
     * @param inModuleURN the module URN
     *
     * @return the detailed module information.
     *
     * @throws ModuleNotFoundException if a module with the supplied
     * URN was not found
     * @throws InvalidURNException if the supplied URN was invalid.
     */
    public ModuleInfo getModuleInfo(ModuleURN inModuleURN)
            throws ModuleNotFoundException,
            InvalidURNException {
        synchronized (mOperationsLock) {
            Module module = getModule(inModuleURN);

            Set<DataFlowID> initiatedFlows =
                    mDataFlows.getInitiatedFlows(inModuleURN);
            Set<DataFlowID> participatingFlows =
                    mDataFlows.getFlowsParticipating(inModuleURN);

            return new ModuleInfo(
                    inModuleURN,
                    module.getState(),
                    initiatedFlows == null
                            ? null
                            : initiatedFlows.toArray(
                            new DataFlowID[initiatedFlows.size()]),
                    participatingFlows == null
                            ? null
                            : participatingFlows.toArray(
                            new DataFlowID[participatingFlows.size()]),
                    module.getCreated(),
                    module.getStarted(),
                    module.getStopped(),
                    module.isAutoStart(),
                    module.isAutoCreated(),
                    module instanceof DataReceiver,
                    module instanceof DataEmitter,
                    module instanceof DataFlowRequester,
                    module.getLastStartFailure(),
                    module.getLastStopFailure());
        }
    }

    /**
     * Starts the module instance
     *
     * @param inModuleURN the module instance URN uniquely identifying
     * the module that needs to be started.
     *
     * @throws ModuleException if there were errors starting the module.
     */
    public void start(ModuleURN inModuleURN) throws ModuleException {
        synchronized (mOperationsLock) {
            Module m = getModule(inModuleURN);
            startModule(m);
        }
    }

    /**
     * Stops a module instance. Do note that stopping a module stops
     * all the data flows that this module initiated.
     *
     * However, if the module is participating in data flows that it
     * didn't initiate, the module cannot be stopped. The user has
     * to manually cancel other data flows that the module is participating
     * in, in order to be able to stop it.
     *
     * @param inModuleURN the module instance URN uniquely identifying
     * the module that needs to be stopped.
     *
     * @throws ModuleNotFoundException if a module with the supplied
     * URN was not found
     * @throws InvalidURNException if the supplied module URN is invalid.
     */
    public void stop(ModuleURN inModuleURN) throws ModuleException {
        synchronized (mOperationsLock) {
            Module m = getModule(inModuleURN);
            stopModule(m);
        }
    }

    /**
     * Creates a data flow between the modules identified by
     * the supplied requests. Each data request should uniquely identify
     * a module via its URN attribute. Its an error if none or
     * multiple modules match the URN.
     *
     * For each matched module, a request is initiated supplying it the
     * data request.
     *
     * The system will automatically append the sink module to the
     * data flow if the last module identified by the request is
     * capable of emitting data and if the sink has not been already
     * specified as the last module in the pipeline.
     *
     * Invoking this method is the same as invoking
     * <code>createDataFlow(requests,true);</code>
     *
     * @param inRequests the request instances
     *
     * @return the ID identifying the data flow.
     *
     * @throws ModuleException if any of the requested modules could
     * not be found, or instantiated or configured. Or if any of the
     * modules were not capable of emitting or receiving data as
     * requested. Or if any of the modules didn't understand the
     * request parameters or were unable to emit data as requested.
     *
     */
    public DataFlowID createDataFlow(DataRequest[] inRequests) throws ModuleException {
        return createDataFlow(inRequests,true);
    }

    /**
     * Creates a requested connection between the modules identified by
     * the supplied requests. Each data request should uniquely identify
     * a module via its URN attribute. Its an error if none or
     * multiple modules match the URN.
     *
     * For each matched module, a request is initiated supplying it the
     * data request.
     *
     * Each of the modules specified in the request should already be
     * started for the request to succeed. This request will fail
     * if any of the modules specified in the request are not started
     * and are not
     * {@link org.marketcetera.module.Module#isAutoStart() auto-start}.
     *
     * @param inRequests the request instances
     * @param inAppendSink if the sink module should be automatically
     * appended to the tail end of the data flow.
     *
     * @return the ID identifying the data flow.
     *
     * @throws ModuleException if any of the requested modules could
     * not be found, or instantiated or configured. Or if any of the
     * modules were not capable of emitting or receiving data as
     * requested. Or if any of the modules didn't understand the
     * request parameters or were unable to emit data as requested.
     *
     */
    public DataFlowID createDataFlow(DataRequest[] inRequests,
                                     boolean inAppendSink)
            throws ModuleException {
        return createDataFlow(inRequests,inAppendSink, null);
    }

    /**
     * Cancels the data flow identified by the supplied data flow ID.
     * Do note that data flows that have been initiated by
     * {@link #createDataFlow(DataRequest[])} can be canceled by this
     * method.
     *
     * Specifically, data flows created by modules via
     * {@link org.marketcetera.module.DataFlowSupport#createDataFlow(DataRequest[])}
     * cannot be canceled by this method. They can only be canceled by the
     * module that initiated the data flow request.
     *
     * @param inFlowID the data flow ID.
     *
     * @throws DataFlowNotFoundException if the data flow, specified by
     * the ID, could not be found.
     * @throws ModuleStateException If the requesting module is not in the
     * correct state to be requesting cancellation of data flows.
     * @throws ModuleException if there were errors deleting auto-created
     * modules that are no longer participating in any data flows.
     */
    public void cancel(DataFlowID inFlowID)
            throws ModuleException {
        cancel(inFlowID, null);
    }

    /**
     * Returns all active the data flows.
     *
     * @param inIncludeModuleCreated if the data flows created by
     * the module should be included in the returned list.
     *
     * @return the list of IDs of all data flows in the system.
     */
    public List<DataFlowID> getDataFlows(boolean inIncludeModuleCreated) {
        synchronized (mOperationsLock) {
            return mDataFlows.getDataFlows(inIncludeModuleCreated);
        }
    }

    /**
     * Returns details of data flow given the data flow ID.
     * Only active data flows can be queried via this mechanism.
     *
     * @param inFlowID the data flow ID
     *
     * @return the data flow details
     *
     * @throws DataFlowNotFoundException if the data flow, specified by
     * the ID, could not be found.
     */
    public DataFlowInfo getDataFlowInfo(DataFlowID inFlowID)
            throws DataFlowNotFoundException {
        DataFlow flow;
        synchronized (mOperationsLock) {
            flow = mDataFlows.get(inFlowID);
        }
        if (flow != null) {
            return flow.toDataFlowInfo();
        } else {
            throw new DataFlowNotFoundException(new I18NBoundMessage1P(
                    Messages.DATA_FLOW_NOT_FOUND, inFlowID.getValue()));
        }
    }

    /**
     * Returns the historical record of data flows that are not active
     * anymore. The number of records returned are determined by the
     * current value returned by {@link #getMaxFlowHistory()}.
     *
     * @return historical record of data flows that are not active
     * anymore.
     */
    public List<DataFlowInfo> getDataFlowHistory() {
        synchronized (mOperationsLock) {
            return new ArrayList<DataFlowInfo>(mFlowHistory);
        }
    }

    /**
     * Returns the module configuration provider instance being used
     * for providing default values for the module factory / instance
     * configuration attributes.
     *
     * @return the module configuration provider instance.
     */
    public ModuleConfigurationProvider getConfigurationProvider() {
        return mConfigurationProvider;
    }

    /**
     * Discovers all the module implementations and instantiates
     * all the singleton instances.
     *
     * @throws ModuleException If there were errors initializing
     * the module framework.
     */
    public void init() throws ModuleException {
        //Register itself with the platform MBean server
        synchronized (mOperationsLock) {
            try {
                for(ModuleFactory factory: mLoader) {
                    initialize(factory);
                }
                SLF4JLoggerProxy.info(this, mModules.toString());
                // Supply this reference to the Sink module for sink listening to work
                ((SinkModule)getModule(SinkModuleFactory.INSTANCE_URN)).setManager(this);
                getMBeanServer().registerMBean(new ModuleManagerMXBeanImpl(this),
                        new ObjectName(MODULE_MBEAN_NAME));
            } catch (ServiceConfigurationError e) {
                throw new ModuleException(e, Messages.MODULE_CONFIGURATION_ERROR);
            } catch (JMException e) {
                throw new BeanRegistrationException(e,new I18NBoundMessage1P(
                        Messages.BEAN_REGISTRATION_ERROR,MODULE_MBEAN_NAME));
            }
        }
    }

    /**
     * Refreshes the set of module providers. Any new module provider
     * jars that have been made available in the class path will be
     * discovered and processed as a result.
     *
     * The existing module providers will remain unchanged.
     *
     * Also {@link #refresh() refreshes} the module configuration
     * provider, if one has been provided.
     *
     * @throws ModuleException if there were errors initializing
     * newly discovered factories.
     */
    public void refresh() throws ModuleException {
        try {
            synchronized (mOperationsLock) {
                boolean doRefresh = true;
                if(mRefreshListener != null) {
                    doRefresh = mRefreshListener.refresh();
                }
                if (doRefresh) {
                    mLoader.reload();
                    //Refresh the configuration provider
                    if(getConfigurationProvider() != null) {
                        getConfigurationProvider().refresh();
                    }
                    for(ModuleFactory factory: mLoader) {
                        initialize(factory);
                    }
                }
            }
        } catch (ServiceConfigurationError e) {
            throw new ModuleException(e, Messages.MODULE_CONFIGURATION_ERROR);
        } catch (IOException e) {
            throw new ModuleException(e, Messages.ERROR_REFRESH);
        }
    }

    /**
     * Sets the refresh listener that intercepts the {@link #refresh()}
     * invocations. Only one refresh listener can be setup. If a
     * refresh listener is already setup when this method is invoked,
     * an exception is thrown.
     * 
     * @param inRefreshListener the refresh listener instance
     *
     * @throws ModuleException if a refresh listener is already setup.
     */
    public void setRefreshListener(RefreshListener inRefreshListener)
            throws ModuleException {
        synchronized (mOperationsLock) {
            if (mRefreshListener == null) {
                mRefreshListener = inRefreshListener;
            } else {
                throw new ModuleException(new I18NBoundMessage1P(
                        Messages.REFRESH_LISTENER_ALREADY_SETUP,
                        mRefreshListener.getClass().getName()));
            }
        }
    }

    /**
     * Adds a sink listener.
     *
     * @param inListener the sink listener
     */
    public void addSinkListener(SinkDataListener inListener) {
        synchronized (mSinkListenerLock) {
            HashSet<SinkDataListener> listeners = new HashSet<SinkDataListener>(
                    Arrays.asList(mSinkListeners));
            listeners.add(inListener);
            mSinkListeners = listeners.toArray(
                    new SinkDataListener[listeners.size()]);
        }
    }

    /**
     * Removes a sink listener.
     *
     * @param inListener the sink listener
     *
     * @return true if the listener was found in the list of listeners.
     */
    public boolean removeSinkListener(SinkDataListener inListener) {
        synchronized (mSinkListenerLock) {
            HashSet<SinkDataListener> listeners = new HashSet<SinkDataListener>(
                    Arrays.asList(mSinkListeners));
            boolean wasFound = listeners.remove(inListener);
            mSinkListeners = listeners.toArray(
                    new SinkDataListener[listeners.size()]);
            return wasFound;
        }
    }

    /**
     * Initializes the module configuration provider for the module framework.
     *
     * @param inConfigurationProvider the module configuration provider instance
     * to use for this framework
     */
    public void setConfigurationProvider(
            ModuleConfigurationProvider inConfigurationProvider) {
        synchronized (mOperationsLock) {
            mConfigurationProvider = inConfigurationProvider;
        }
    }

    /**
     * Stops the module manager and all the data flow activities.
     * This method, stops all the non-module initiated data flows first,
     * it then stops all the modules that have initiated data flows,
     * finally it stops all the running modules.
     *
     * @throws ModuleException if there errors stopping the module manager.
     */
    public void stop() throws ModuleException {
        Messages.LOG_STOPPING_MODULE_MANAGER.info(this);
        try {
            synchronized(mOperationsLock) {
                ModuleURN moduleURN;
                //find out all the data flows that are not initiated by
                //modules
                List<DataFlowID> flows = mDataFlows.getDataFlows(false);
                for(DataFlowID flowID: flows) {
                    cancel(flowID);
                }
                //Now find out all the data flows that are initiated by modules
                //and stop the initiating modules
                flows = mDataFlows.getDataFlows(true);
                //All the returned flows should be module initiated as the
                //non-module initiated flows have been cancelled.
                for(DataFlowID flowID: flows) {
                    DataFlow flow = mDataFlows.get(flowID);
                    //We might not get a data flow for an ID if a module
                    //that spawned multiple flows is stopped in a previous
                    //iteration.
                    if(flow == null) {
                        continue;
                    }
                    //Do note that this may fail if the requesting module
                    //is participating in data flows that it didn't initiate.
                    //If that happens, this method will fail, the user will
                    //need to resolve the dependency manually and retry stop.
                    moduleURN = flow.getRequesterURN();
                    Module m = getModule(moduleURN);
                    if(m.getState().isStarted()) {
                        stop(moduleURN);
                    }
                }
                //Get all the modules and stop them, if they are not stopped,
                //stop them.
                ModuleURN [] urns = mModules.getAllURNs();
                for(ModuleURN urn: urns) {
                    Module m = mModules.get(urn);
                    if(m.getState().isStarted() &&
                            //Skip the sink module as it cannot be stopped.
                            !SinkModuleFactory.INSTANCE_URN.equals(urn)) {
                        stop(urn);
                    }
                }
                //Clean up all the mbeans.
                ObjectName name = new ObjectName(MODULE_MBEAN_NAME);
                //unregister the module manager
                unregister(name);
                //unregister all instances
                for(ModuleURN urn: getModuleInstances(null)) {
                    unregister(urn);
                }
                //unregister all providers
                for(ModuleURN urn: getProviders()) {
                    unregister(urn);
                }
            }
        } catch (ModuleException e) {
            Messages.LOG_STOP_FAILURE.error(this,e);
            throw e;
        } catch (JMException e) {
            throw new ModuleException(e,
                    Messages.BEAN_UNREGISTRATION_ERROR);
        }
    }

    /**
     * Sets the MBean server to use for all JMX operations. This method
     * should be invoked prior to invoking {@link #init()}. The behavior
     * of the module manager is not defined if this method is invoked
     * after the module manager has been initialized.
     * <p>
     * By default the platform MBean server is used for all JMX operations.
     *
     * @param inMBeanServer The MBean server to use for all operations.
     */
    public void setMBeanServer(MBeanServer inMBeanServer) {
        mMBeanServer = inMBeanServer;
    }
    /**
     * Creates a module instance. An attempt to create a module instance
     * for a provider that only supports singleton instances will fail.
     * All singleton instances are created when ModuleManager is
     * initialized.
     * <p>
     * This method can only instantiate modules whose creation requires
     * only those parameter types as supported by
     * {@link org.marketcetera.module.StringToTypeConverter}. If an
     * attempt is made to create a module supplying string value for
     * an unsupported type, the module creation will fail with a type
     * mismatch error.
     * <p>
     * Parameters of any type can be used to instantiate modules via
     * {@link #createModule(ModuleURN, Object[])}. However, this API
     * is only available for local invocation as JMX doesn't support
     * remoting of any random data type.
     *
     *
     * @param providerURN The provider URN. The value supplied should match
     * the value returned by a module factory's
     * {@link ModuleFactory#getProviderURN()} that
     * is available in the system.
     * @param parameterList the comma separated list of parameters that
     * are needed to instantiate the module. The string parameters are
     * converted to object types based on type values returned by
     * {@link ModuleFactory#getParameterTypes()}. If any of the types
     * returned by {@link ModuleFactory#getParameterTypes()} are not
     * supported by {@link org.marketcetera.module.StringToTypeConverter},
     * this method will fail.
     *
     * @return the instantiated module's URN
     *
     * @throws ModuleException if there were errors creating the module
     *
     * @see #getProviderInfo(ModuleURN)
     */
    ModuleURN createModuleJMX(ModuleURN providerURN, String parameterList)
            throws ModuleException {

        URNUtils.validateProviderURN(providerURN);
        ModuleFactory factory = getModuleFactory(providerURN);

        //Attempt conversion of the parameters
        //Leave error handling to the overloaded method invoked at the end.
        //Split parameters using , as a delimiter
        String[] parameters = null;
        if(parameterList != null) {
            parameters = parameterList.split(",");  //$NON-NLS-1$
        }
        Object[] params = null;
        Class[] parameterTypes = factory.getParameterTypes();
        if(parameterTypes != null &&
                parameterTypes.length != 0 &&
                parameters != null &&
                parameters.length > 0) {
            //Ignore extra parameters
            params = new Object[Math.min(parameters.length,
                    parameterTypes.length)];

            for(int i= 0; i < params.length; i++) {
                Class type = parameterTypes[i];
                if(StringToTypeConverter.isSupported(type)) {
                    //convert parameters whose conversion is supported
                    try {
                        params[i] = StringToTypeConverter.convert(type,
                                parameters[i]);
                    } catch (IllegalArgumentException e) {
                        throw new MXBeanOperationException(e,
                                new I18NBoundMessage2P(
                                        Messages.CANNOT_CREATE_MODULE_PARAM_CONVERT_ERROR,
                                        providerURN.toString(),i));
                    }
                } else {
                    //ignore conversion for parameters that are not supported
                    //the subsequent API performing parameter type checks
                    //will throw the failure.
                    params[i] = parameters[i];
                }
            }
        }
        return createModule(providerURN, params);
    }

    /**
     * Creates a new data flow.
     *
     * @param inRequests the data flow requests.
     * @param inAppendSink if the sink module should be
     * automatically appended to the data flow
     * @param inRequester the module requesting this data flow. null,
     * if the data flow was not requested by a module.
     *
     * @return unique ID identifying the data flow
     *
     * @throws ModuleException if there were errors setting up
     * data flow.
     */
    DataFlowID createDataFlow(DataRequest[] inRequests,
                              boolean inAppendSink,
                              Module inRequester)
            throws ModuleException {
        //verify that there are enough modules to create a data flow.
        if(inRequests == null || inRequests.length < (inAppendSink ? 1 : 2)) {
            throw new DataFlowException(new I18NBoundMessage1P(
                    Messages.DATA_REQUEST_TOO_SHORT,
                    inRequests == null
                            ? 0
                            : inRequests.length));
        }
        synchronized (mOperationsLock) {
            // verify that the requester is in the right state to
            // be requesting data flows.
            if(inRequester != null && !(inRequester.getState().
                    canParticipateFlows())) {
                throw new ModuleStateException(new I18NBoundMessage1P(
                        Messages.DATAFLOW_REQ_MODULE_STOPPED,
                        inRequester.getURN().toString()));
            }

            //Find modules corresponding to each data request
            Module[] modules = findModules(inRequests, inRequester);

            //Append the sink module if requested and possible
            if(inAppendSink &&
                    // Last module is not the sink module
                    !(SinkModuleFactory.INSTANCE_URN.equals(
                            modules[modules.length - 1].getURN())) &&
                    // the last module is capable of emitting data
                    modules[modules.length - 1] instanceof DataEmitter) {

                //Add sink module to the tail end of the pipeline.
                modules = Arrays.copyOf(modules, modules.length + 1);
                modules[modules.length - 1] = getModule(
                        SinkModuleFactory.INSTANCE_URN);

                //Also add a data request to append the sink module
                inRequests = Arrays.copyOf(inRequests, inRequests.length + 1);
                inRequests[inRequests.length - 1] = new DataRequest(
                        SinkModuleFactory.INSTANCE_URN);
            }
            if(inRequests.length < 2) {
                throw new DataFlowException(new I18NBoundMessage1P(
                        Messages.DATA_REQUEST_TOO_SHORT, inRequests.length));

            }
            // Iterate through the list of module verifying that they can
            // handle data flows and are started.
            for(int i = 0; i < modules.length; i++) {
                //verify that all modules except the last one can emit data
                if((i < (modules.length - 1)) && !(modules[i] instanceof DataEmitter)) {
                    throw new DataFlowException(new I18NBoundMessage1P(
                            Messages.MODULE_NOT_EMITTER,
                            modules[i].getURN().toString()));
                }
                //verify that all modules except the first one can receive data
                if(i > 0 && !(modules[i] instanceof DataReceiver)) {
                    throw new DataFlowException(new I18NBoundMessage1P(
                            Messages.MODULE_NOT_RECEIVER,
                            modules[i].getURN().toString()));
                }
                if(!modules[i].getState().canParticipateFlows()) {
                    throw new ModuleStateException(new I18NBoundMessage1P(
                            Messages.DATAFLOW_REQ_MODULE_STOPPED,
                            modules[i].getURN().toString()));
                }
            }
            // Start going backwards through the modules array plumbing them
            // plumbing the first module last, ensures that the rest of the data
            // pipeline is ready to receive data once the emitter starts emitting
            // data
            AbstractDataCoupler[] couplers = new AbstractDataCoupler[modules.length - 1];
            DataFlowID id = generateFlowID();
            int i = couplers.length - 1;
            boolean failed = true;
            try {
                for(; i >= 0; i--) {
                    couplers[i] = inRequests[i].getCoupling().createCoupler(
                            this, modules[i], modules[i + 1], id);
                    couplers[i].initiateRequest(generateRequestID(), inRequests[i]);
                }
                failed = false;
            } finally {
                if(failed) {
                    //go through all the initiated requests and cancel them.
                    while(++i < couplers.length) {
                        couplers[i].cancelRequest();
                    }
                }
            }
            DataFlow flow = new DataFlow(id,
                    inRequester == null
                            ? null
                            : inRequester.getURN(),
                    inRequests, couplers);
            mDataFlows.addFlow(flow);
            return id;
        }
    }

    /**
     * Cancels the data flow.
     *
     * @param inFlowID the data flow ID of the data flow that needs
     * to be canceled.
     * @param inRequester the module requesting the cancellation, null if
     * the cancellation is not requested by a module.
     *
     * @throws DataFlowNotFoundException if the data flow corresponding
     * to the supplied ID wasn't found.
     * @throws ModuleStateException If the requesting module is not in the
     * correct state to be requesting cancellation of data flows.
     * @throws ModuleException if there were errors deleting auto-created
     * modules that are no longer participating in any data flows.
     */
    void cancel(DataFlowID inFlowID, Module inRequester)
            throws ModuleException {
        DataFlow flow;
        synchronized (mOperationsLock) {
            if(inRequester != null && !inRequester.getState().canStopFlows()) {
                throw new ModuleStateException(new I18NBoundMessage2P(
                        Messages.CANCEL_FAILED_MODULE_NOT_STARTED,
                        inFlowID.getValue(), inRequester.getURN().toString()));
            }
            flow = mDataFlows.remove(inFlowID);
            if(flow == null) {
                throw new DataFlowNotFoundException(new I18NBoundMessage1P(
                        Messages.DATA_FLOW_NOT_FOUND, inFlowID.getValue()));
            }
            flow.cancel(inRequester == null
                    ? null
                    : inRequester.getURN());
            addToFlowHistory(flow.toDataFlowInfo());
        }
    }

    /**
     * Receives sink data from the Data Sink Module
     *
     * @param inFlowID the data flow ID of the data flow that
     * delivered the data.
     * @param inData the data
     */
    void receiveSinkData(DataFlowID inFlowID, Object inData) {
        for(SinkDataListener listener: mSinkListeners) {
            try {
                listener.receivedData(inFlowID, inData);
            } catch (Throwable e) {
                Messages.LOG_SINK_LISTENER_RECEIVE_ERROR.error(this, e);
            }
        }
    }

    /**
     * The maximum number of data flow history records to retain.
     * The default value is set to {@link #DEFAULT_MAX_FLOW_HISTORY}.
     *
     * @return maximum number of data flow history records to retain.
     */
    int getMaxFlowHistory() {
        return mMaxFlowHistory;
    }

    /**
     * Set the maximum number of data flow history records to retain.
     * If the value is reset to a value lower than the current value,
     * the older history records are pruned to bring down the size
     * of the historical records to the new value.
     *
     * @param inMaxFlowHistory the maximum number of data flow history
     * records to retain.
     */
    void setMaxFlowHistory(int inMaxFlowHistory) {
        synchronized (mOperationsLock) {
            mMaxFlowHistory = inMaxFlowHistory;
            //re-size the history records.
            try {
                addToFlowHistory(null);
            } catch (ModuleException e) {
                //Cannot get an exception if null data flow info is passed in.
                SLF4JLoggerProxy.debug(this,"Unexpected exception",e);  //$NON-NLS-1$
            }
        }
    }

    /**
     * Returns the MBean server to use for all JMX operations.
     *
     * @return the MBean server to use for all JMX operations.
     */
    private MBeanServer getMBeanServer() {
        return mMBeanServer;
    }

    /**
     * Creates a module instance after performing module factory checks.
     * Ensures that multiple instances for singleton modules do not get
     * created. Verifies that the correct number and types of parameters
     * are provided to create the module.
     *
     * @param inProviderURN the module provider URN
     * @param inParameters the parameters to create the module
     *
     * @return the created module instance
     *
     * @throws ModuleException if there were errors creating the module
     */
    private Module createModuleImpl(ModuleURN inProviderURN,
                                    Object... inParameters)
            throws ModuleException {
        //validate the URN
        URNUtils.validateProviderURN(inProviderURN);
        Module module;
        synchronized (mOperationsLock) {
            //find the provider factory
            ModuleFactory factory = getModuleFactory(inProviderURN);

            //check if this module supports multiple instances
            //and if it doesn't verify that no other instances exist
            if(!factory.isMultipleInstances()) {
                List<ModuleURN> urns = getModuleInstances(inProviderURN);
                if(!urns.isEmpty()) {
                    throw new ModuleCreationException(new I18NBoundMessage2P(
                            Messages.CANNOT_CREATE_SINGLETON, inProviderURN.toString(),
                            urns.get(0).toString()));
                }
            }

            //Verify if the parameter types match the advertised types
            Class[] paramTypes = factory.getParameterTypes();
            //deal with nulls an empty arrays.
            if(
                    // No parameters are needed but some are provided
                    (paramTypes.length == 0 && inParameters != null &&
                            inParameters.length != 0) ||
                    // OR parameters are needed but the right number are not
                    // provided
                    (paramTypes.length != 0 &&
                            (inParameters == null ||
                                    paramTypes.length != inParameters.length))) {

                throw new ModuleCreationException(new I18NBoundMessage3P(
                        Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_NUM,
                        inProviderURN.toString(), paramTypes.length,
                        inParameters == null
                                ? 0
                                : inParameters.length));
            }
            //Verify if the correct parameter types have been supplied
            int i = 0;
            for(Class c: paramTypes) {
                if(inParameters[i] != null && !c.isInstance(inParameters[i])
                        && !isPrimitiveMatch(c, inParameters[i])) {
                    throw new ModuleCreationException(new I18NBoundMessage4P(
                            Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE,
                            inProviderURN.toString(), i,c.getName(),
                            inParameters[i].getClass().getName()));
                }
                i++;
            }
            //create the module
            module = createModule(factory, inParameters);
        }
        return module;
    }

    /**
     * Returns true if the supplied class is of primitive type
     * and the supplied parameter is of the same type as the
     * boxed primitive type.
     *
     * @param inClass the primitive class
     * @param inParameter the parameter
     *
     * @return if the supplied parameter is of the boxed primitive type.
     */
    private boolean isPrimitiveMatch(Class inClass, Object inParameter) {
        if(inClass.isPrimitive()) {
            if(Boolean.TYPE.equals(inClass)) {
                return Boolean.class.isInstance(inParameter);
            } else if (Byte.TYPE.equals(inClass)) {
                return Byte.class.isInstance(inParameter);
            } else if (Character.TYPE.equals(inClass)) {
                return Character.class.isInstance(inParameter);
            } else if (Short.TYPE.equals(inClass)) {
                return Short.class.isInstance(inParameter);
            } else if (Integer.TYPE.equals(inClass)) {
                return Integer.class.isInstance(inParameter);
            } else if (Float.TYPE.equals(inClass)) {
                return Float.class.isInstance(inParameter);
            } else if (Long.TYPE.equals(inClass)) {
                return Long.class.isInstance(inParameter);
            } else if (Double.TYPE.equals(inClass)) {
                return Double.class.isInstance(inParameter);
            }
        }
        return false;
    }

    /**
     * Adds the flow info to flow history. Prunes the older
     * records from the history to ensure that size of the history
     * records does not exceed {@link #getMaxFlowHistory()}.
     *
     * If the data flow has any participating modules that were
     * auto-created, if they are no longer participating in any data flow, they
     * are deleted.
     *
     * Invokers of this method should acquire the operations lock while
     * invoking it.
     *
     * @param inDataFlowInfo data flow info to add to the history.
     * null, if there are no records to add.
     * @throws ModuleException if there were issues deleting auto-created
     * modules that are not participating in any data flows.
     */
    private void addToFlowHistory(DataFlowInfo inDataFlowInfo) throws ModuleException {
        while (mFlowHistory.size() > mMaxFlowHistory) {
            mFlowHistory.removeLast();
        }
        if (inDataFlowInfo != null) {
            mFlowHistory.addFirst(inDataFlowInfo);
            //figure out if there are any auto-created modules
            //and if they are not participating in any data flows
            //delete them
            for(DataFlowStep step: inDataFlowInfo.getFlowSteps()) {
                Module m = mModules.get(step.getModuleURN());
                if(m != null && m.isAutoCreated()) {
                    //delete the module if its not participating
                    //in any data flows
                    final Set<DataFlowID> flows =
                            mDataFlows.getFlowsParticipating(m.getURN());
                    if(flows == null || flows.isEmpty()) {
                        Messages.LOG_DELETE_AUTO_CREATED_MODULE.info(
                                this, m.getURN(),
                                inDataFlowInfo.getFlowID());
                        deleteModule(m.getURN());
                    }
                }
            }
        }
    }

    /**
     * Unregisters the mbean for the factory / instance
     * with the supplied URN.
     *
     * @param urn the module factory / instance URN.
     *
     * @throws JMException if there were errors.
     * @throws ModuleException if there were errors converting module URN
     * to object name.
     */
    private void unregister(ModuleURN urn)
            throws JMException, ModuleException {
        unregister(urn.toObjectName());
    }

    /**
     * Unregisters the mbean with the supplied object name from
     * the mbean server, if one is registered.
     *
     * @param inName the mbean object name.
     *
     * @throws JMException if there was a failure.
     */
    private void unregister(ObjectName inName) throws JMException {
        if (getMBeanServer().isRegistered(inName)) {
            getMBeanServer().unregisterMBean(inName);
        }
    }

    /**
     * Retrieves the module factory for the supplied URN.
     *
     * @param inUrn the provider URN. The URN should be validated. This method
     * doesn't validate the URN and its behavior is unspecified if the supplied
     * URN is not validated. 
     *
     * @return the module factory instance.
     *
     * @throws ProviderNotFoundException if the module factory was not found.
     */
    private ModuleFactory getModuleFactory(ModuleURN inUrn)
            throws ProviderNotFoundException {
        ModuleFactory factory;
        synchronized (mOperationsLock) {
            factory = mModuleFactories.get(inUrn);
        }
        if(factory == null) {
            throw new ProviderNotFoundException(new I18NBoundMessage1P(
                    Messages.PROVIDER_NOT_FOUND, inUrn.toString()));
        }
        return factory;
    }

    /**
     * Finds the module, given its instance URN.
     *
     * @param inModuleURN the module instance URN. The URN should already
     * be validated, this method does not validate the supplied URN.
     * 
     * @return the module instance
     *
     * @throws InvalidURNException if the module URN is invalid
     * @throws ModuleNotFoundException if the module was not found
     */
    private Module getModule(ModuleURN inModuleURN)
            throws InvalidURNException, ModuleNotFoundException {
        URNUtils.validateInstanceURN(inModuleURN);
        Module module;
        synchronized (mOperationsLock) {
            module = mModules.get(inModuleURN);
        }
        if(module == null) {
            throw new ModuleNotFoundException(new I18NBoundMessage1P(
                    Messages.MODULE_NOT_FOUND, inModuleURN.toString()));
        }
        return module;
    }

    /**
     * Stops the provided module instance.
     *
     * @param inModule the module instance that needs to be stopped.
     *
     * @throws ModuleException if there were errors when stopping the module
     */
    private void stopModule(Module inModule) throws ModuleException {
        //verify that the module is running
        if(!inModule.getState().isStarted()) {
            throw new ModuleStateException(new I18NBoundMessage1P(
                    Messages.STOP_FAILED_MODULE_NOT_STARTED,
                    inModule.getURN().toString()));
        }
        //cannot stop the sink module
        if(inModule.getURN().equals(SinkModuleFactory.INSTANCE_URN)) {
            throw new ModuleException(Messages.CANNOT_STOP_SINK_MODULE);
        }
        //verify that the module is not participating in flows that it didn't
        //initiate
        Set<DataFlowID> initiated = mDataFlows.getInitiatedFlows(
                inModule.getURN());
        Set<DataFlowID> participating = mDataFlows.getFlowsParticipating(
                inModule.getURN());
        if(participating != null) {
            if(initiated != null) {
                participating.removeAll(initiated);
            }
            if(!participating.isEmpty()) {
                throw new DataFlowException(new I18NBoundMessage2P(
                        Messages.CANNOT_STOP_MODULE_DATAFLOWS,
                        inModule.getURN().toString(),
                        participating.toString()));
            }
        }
        boolean stopSucceeded = false;
        try {
            inModule.setState(ModuleState.STOPPING);
            inModule.preStop();
            //cancel initiated flows
            initiated = mDataFlows.getInitiatedFlows(inModule.getURN());
            if (initiated != null) {
                for(DataFlowID flowID: initiated) {
                    cancel(flowID);
                }
            }
            stopSucceeded = true;
            Messages.LOG_MODULE_STOPPED.info(this, inModule.getURN());
        } catch (ModuleException e) {
            inModule.setLastStopFailure(e.getLocalizedDetail());
            Messages.LOG_STOP_MODULE_FAILED.warn(this, e, inModule.getURN());
            throw e;
        } finally {
            if(stopSucceeded) {
                inModule.setState(ModuleState.STOPPED);
                inModule.setLastStopFailure(null);
            } else {
                inModule.setState(ModuleState.STOP_FAILED);
            }
        }
    }

    /**
     * Find module instances matching the specified requests.
     *
     * @param inRequests the requests
     * @param inRequester the module instance requesting the data flow.
     * null, if the data flow wasn't requested by a module.
     *
     * @return the array of module instances that should participate
     * in this data flow.
     *
     * @throws InvalidURNException if invalid module instance URNs
     * were specified in any of the requests
     *
     * @throws ModuleNotFoundException if module corresponding to URNs,
     * specified in the requests, were not found.
     */
    private Module[] findModules(DataRequest[] inRequests,
                                 Module inRequester)
            throws ModuleException {
        Module[] modules = new Module[inRequests.length];
        int i = 0;
        for(DataRequest request:inRequests) {
            modules[i++] = findModule(URNUtils.processURN(
                    inRequester == null
                            ? null
                            : inRequester.getURN(),
                    request.getRequestURN()));
        }
        return modules;
    }

    /**
     * Finds the module corresponding to the specified URN.
     * The URN may have some of its elements missing. The system
     * will find a matching module such that all the specified elements
     * of the specified URN match the module instance URN.
     *
     * If multiple modules match the specified URN, this operation fails.
     *
     * @param inModuleURN the module URN
     *
     * @return the Module instance corresponding to the
     * specified URI
     *
     * @throws InvalidURNException if the specified URN was invalid
     * @throws ModuleNotFoundException if a module matching the
     * URI wasn't found.
     */
    private Module findModule(ModuleURN inModuleURN)
            throws ModuleException {
        Module m = mModules.search(inModuleURN);
        if(m == null) {
            if(inModuleURN.instanceURN()) {
                ModuleURN parent = inModuleURN.parent();
                ModuleFactory factory = mModuleFactories.get(parent);
                if(factory != null && factory.isAutoInstantiate()) {
                    URNUtils.validateInstanceURN(inModuleURN);
                    Module module = createModuleImpl(parent, inModuleURN);
                    module.setAutoCreated(true);
                    return module;
                }
            }
            throw new ModuleNotFoundException(new I18NBoundMessage1P(
                    Messages.MODULE_NOT_FOUND, inModuleURN.toString()));
        }
        return m;
    }

    /**
     * Initializes the module factory. This method is not re-entrant,
     * its caller should guarantee that this method is not invoked
     * from multiple threads simultaneously.
     *
     * @param inFactory the factory to initialize
     *
     * @throws ModuleException if there was an error initializing
     * the factory
     */
    private void initialize(ModuleFactory inFactory) throws ModuleException {
        Messages.LOG_INIT_FACTORY.info(this, inFactory.getClass().getName());
        //Validate the inFactory
        ModuleURN urn = inFactory.getProviderURN();
        URNUtils.validateProviderURN(urn);
        if(inFactory.isMultipleInstances() &&
                inFactory.isAutoInstantiate()) {
            //verify that it only needs ModuleURN parameter to create
            //new instances
            Class[] list = inFactory.getParameterTypes();
            if(list == null || list.length != 1 ||
                    (!ModuleURN.class.equals(list[0]))) {
                throw new ModuleException(new I18NBoundMessage1P(
                        Messages.INCORRECT_FACTORY_AUTO_INSTANTIATE,
                        urn.toString()));
            }
        }
        //Add the factory to the table, if not already there
        if(!mModuleFactories.containsKey(urn)) {
            mModuleFactories.put(urn, inFactory);
        } else {
            //Ignore the factory if its already there.
            //This may happen during a refresh.
            Messages.LOG_INIT_FACTORY_IGNORE.info(this,
                    inFactory.getClass().getName());
            return;
        }
        //Figure out if it has a MXBean interface
        if(isMXBean(inFactory)) {
            ObjectName objectName = registerMXBean(urn, inFactory);
            Messages.LOG_REGISTERED_FACTORY_BEAN.info(this, urn, objectName);
            //Configure the default values, if available
            initializeDefaultValues(urn, objectName);

        }
        //Figure out if its a singleton, instantiate the singleton instance
        if(!inFactory.isMultipleInstances()) {
            // Test if the module can be instantiated without any parameters
            if(inFactory.getParameterTypes() == null ||
                    inFactory.getParameterTypes().length == 0) {
                createModule(inFactory);
            }
        }
    }

    /**
     * Registers the supplied module factory / instance with the
     * MBean server.
     *
     * @param inURN the module factory / instance URN. The MXBean's
     * object name is derived from the URI.
     * @param inMXBean the module factory / instance.
     *
     * @return the objectName of the registered bean
     *
     * @throws MXBeanOperationException if there were errors
     * registering the bean.
     */
    private ObjectName registerMXBean(ModuleURN inURN, Object inMXBean)
            throws MXBeanOperationException {
        ObjectName objectName = inURN.toObjectName();
        try {
            //Register the factory with the mbean server
            getMBeanServer().registerMBean(inMXBean, objectName);
        } catch (JMException e) {
            throw new BeanRegistrationException(e,
                    new I18NBoundMessage1P(
                            Messages.BEAN_REGISTRATION_ERROR, objectName));
        }
        return objectName;
    }

    /**
     * Initialize the writable attributes of the supplied module
     * instance / factory mbean with default values, if available.
     *
     * Only those attributes which can be converted from string values
     * by {@link StringToTypeConverter} are set via this
     * mechanism.
     *
     * @param inURN the module factory / instance URN
     * @param inObjectName the corresponding object name for the module
     * factory / instance.
     *
     * @throws MXBeanOperationException If there were errors carrying out
     * MXBean attribute discovery or set operations.
     */
    private void initializeDefaultValues(ModuleURN inURN,
                                         ObjectName inObjectName)
            throws ModuleException {
        if(mConfigurationProvider == null) {
            //no default values available, skip.
            return;
        }
        //Iterate through all the attributes
        try {
            for(MBeanAttributeInfo attrib:
                    getMBeanServer().getMBeanInfo(inObjectName).getAttributes()) {
                //If the attribute is writable and of type whose default
                //value setting is supported
                if(attrib.isWritable() &&
                        MBeanAttributeSetterHelper.isSupported(attrib)) {
                    //Get the default value for the attribute
                    String defValue = mConfigurationProvider.getDefaultFor(
                            inURN, attrib.getName());
                    //If the default value is available
                    if(defValue != null && !defValue.trim().isEmpty()) {
                        //Set the default value on the bean
                        MBeanAttributeSetterHelper.setValue(getMBeanServer(),
                                inObjectName, attrib, defValue);
                        SLF4JLoggerProxy.debug(this,
                                "Set {} bean's attribute {} to '{}'",  //$NON-NLS-1$
                                inObjectName, attrib.getName(),defValue);
                    }
                }
            }
        } catch (JMException e) {
            throw new MXBeanOperationException(e,
                    new I18NBoundMessage1P(
                            Messages.BEAN_ATTRIB_DISCOVERY_ERROR,
                            inObjectName));
        }
    }

    /**
     * Creates a new module instance.
     *
     * @param inFactory the module factory instance
     * @param inParameters the parameters supplied to create new
     * module instance.
     *
     * @return the new module instance
     *
     * @throws ModuleException if there was an error creating a new
     * module instance
     */
    private Module createModule(ModuleFactory inFactory,
                                Object... inParameters)
            throws ModuleException {
        Module module = inFactory.create(inParameters);
        ModuleURN urn = module.getURN();
        //validate module's URN, verify that its of provider's type
        URNUtils.validateInstanceURN(urn, inFactory.getProviderURN());
        //add the module to appropriate lookup tables
        if(mModules.has(urn)) {
            throw new ModuleCreationException(new I18NBoundMessage1P(
                    Messages.DUPLICATE_MODULE_URN,urn.toString()));
        }
        //if its an mbean register it and initialize its default values
        if(isMXBean(module)) {
            ObjectName objectName = registerMXBean(urn, module);
            Messages.LOG_REGISTERED_MODULE_BEAN.info(this, urn, objectName);
            //Configure default values if available
            initializeDefaultValues(urn,objectName);
        }
        mModules.add(module);
        Messages.LOG_CREATED_MODULE_INSTANCE.info(this, urn);
        //if its auto-start, try starting it
        if(module.isAutoStart()) {
            startModule(module);
        }
        return module;
    }

    /**
     * Starts the module.
     *
     * @param inModule the module instance that needs to be started.
     *
     * @throws ModuleException if there was an error starting the module
     */
    private void startModule(Module inModule) throws ModuleException {
        //Verify module state
        if(inModule.getState().isStarted()) {
            throw new ModuleStateException(new I18NBoundMessage1P(
                    Messages.MODULE_ALREADY_STARTED,
                    inModule.getURN().toString()));
        }
        boolean startSucceeded = false;
        try {
            if(inModule instanceof DataFlowRequester) {
                //supply it a data flow requester support
                //instance
                ((DataFlowRequester)inModule).setFlowSupport(
                        new DataFlowSupportImpl(inModule,this));
            }
            //Tell the module that its about to be started
            inModule.setState(ModuleState.STARTING);
            inModule.preStart();
            startSucceeded = true;
            Messages.LOG_MODULE_STARTED.info(this, inModule.getURN());
        } catch(ModuleException e) {
            inModule.setLastStartFailure(e.getLocalizedDetail());
            Messages.LOG_START_MODULE_FAILED.warn(this,e,inModule.getURN());
            throw e;
        } finally {
            if(startSucceeded) {
                inModule.setState(ModuleState.STARTED);
                inModule.setLastStartFailure(null);
            } else {
                inModule.setState(ModuleState.START_FAILED);
                //check if the module created any data flows and if it
                //did make sure they are canceled.
                final Set<DataFlowID> flows = mDataFlows.getInitiatedFlows(
                        inModule.getURN());
                if(flows != null && !flows.isEmpty()) {
                    for(DataFlowID id: flows) {
                        Messages.LOG_CANCEL_DATA_FLOW_START_FAILED.info(
                                this, id, inModule.getURN());
                        cancel(id);
                    }
                }
            }
        }
    }

    /**
     * Generates a unique ID for a data flow.
     *
     * @return a unique ID for identifying a data flow
     *
     * @throws ModuleException if there were errors generating the
     * data flow ID.
     */
    private DataFlowID generateFlowID() throws ModuleException {
        try {
            return new DataFlowID(mIDFactory.getNext());
        } catch (NoMoreIDsException e) {
            throw new ModuleException(e, Messages.UNABLE_GENERATE_FLOW_ID);
        }
    }

    /**
     * Generates the request ID.
     *
     * @return the request ID.
     *
     * @throws ModuleException if there were errors generating the request ID.
     */
    private RequestID generateRequestID() throws ModuleException {
        try {
            return new RequestID(mIDFactory.getNext());
        } catch (NoMoreIDsException e) {
            throw new ModuleException(e, Messages.UNABLE_GENERATE_REQUEST_ID);
        }
    }

    /**
     * Returns true if the supplied object implements an
     * interface that is an MXBean interface.
     *
     * @param inObject the object instance that needs to be tested
     *
     * @return if the object is an MXBean.
     */
    private static boolean isMXBean(Object inObject) {
        for (Class c = inObject.getClass();
             !Object.class.equals(c);
             c = c.getSuperclass()) {

            for(Class intf: c.getInterfaces()) {
                if(JMX.isMXBeanInterface(intf)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * The JMX domain name for all the module framework beans
     */
    public static final String MBEAN_DOMAIN_NAME =
            ModuleManager.class.getPackage().getName();
    /**
     * The name for the module manager MXBean
     */
    public static final String MODULE_MBEAN_NAME =
            MBEAN_DOMAIN_NAME + ":name=" +  //$NON-NLS-1$
                    ModuleManager.class.getSimpleName();

    /**
     * The default maximum flow history to maintain.
     */
    public static final int DEFAULT_MAX_FLOW_HISTORY = 10;

    /**
     * Array of listeners that listen to data received by the data sink
     */
    private volatile SinkDataListener[] mSinkListeners =
            new SinkDataListener[0];

    /**
     * The listener for {@link #refresh()} invocations.
     */
    private RefreshListener mRefreshListener;
    /**
     * The lock object used to synchronize concurrent updates to
     * <code>mSinkListeners</code>
     */
    private final Object mSinkListenerLock = new Object();
    /**
     * The lock object used to synchronize all the module and data
     * flow operations.
     */
    private final Object mOperationsLock = new Object();
    /**
     * Table of module instances.
     */
    private final ModuleInstanceTracker mModules =
            new ModuleInstanceTracker();
    /**
     * Table of module factory instances.
     */
    private final Map<ModuleURN,ModuleFactory> mModuleFactories =
            new HashMap<ModuleURN, ModuleFactory>();
    /**
     * The service loader used to discover and instantiate module
     * providers
     */
    private final ServiceLoader<ModuleFactory> mLoader;
    /**
     * The module configuration provider that provides default configuration
     * properties for module factories and instances.
     */
    private ModuleConfigurationProvider mConfigurationProvider;
    /**
     * The table of currently active data flows.
     */
    private final DataFlowTracker mDataFlows =
            new DataFlowTracker();

    /**
     * ID factory for generating data flow IDs.
     */
    private final IDFactory mIDFactory = new InMemoryIDFactory(1);
    /**
     * History of flows that are not active any more
     */
    private Deque<DataFlowInfo> mFlowHistory =
            new LinkedList<DataFlowInfo>();
    /**
     * Maximum number flow histories to keep a record of.
     */
    private volatile int mMaxFlowHistory = DEFAULT_MAX_FLOW_HISTORY;

    /**
     * The MBean server to use for all JMX operations.
     */
    private MBeanServer mMBeanServer =
            ManagementFactory.getPlatformMBeanServer();
}

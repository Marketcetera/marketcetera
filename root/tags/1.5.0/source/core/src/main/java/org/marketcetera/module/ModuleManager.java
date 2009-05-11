package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

import javax.management.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
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
 * <p>
 * <b>Implementation Notes on Fine grained locking</b> 
 * This class uses fine grained locks for certain operations. 
 * Following is a summary of various fine grained locking carried out in
 * this class.
 * <ol>
 *  <li>All data structures are synchronized on themselves to ensure
 *      consistent behavior when used concurrently. Examples include
 *      {@link #mModuleFactories}, {@link #mDataFlows} and {@link #mModules} </li>
 *  <li>Module creation operations are serialized via the factory instance.
 *      {@link ModuleFactory#getLock() lock}.</li>
 *  <li>Module deletion operations are serialized on Module
 *      {@link Module#getLock() write lock}
 *      to ensure that no other operations can be performed on the module
 *      while it's being deleted.</li>
 * <li>Garbage collection of auto-created modules acquires a write lock on
 *     the module when stopping and removing it.</li>
 *  <li>Module lifecycle operations like start and stop, acquire Module
 *      {@link Module#getLock() write lock} when changing
 *      the module state during the operation. The {@link Module#preStart()} &
 *      {@link Module#preStop()} methods are invoked without acquiring any
 *      locks on the module.</li>
 *  <li>Data flow creation operations acquire Module
 *      {@link Module#getLock() read lock} on the requesting and participating
 *      modules to prevent module state changes while data flow operations
 *      are being carried out.</li>
 *  <li>Data flow cancellation operations acquire Module
 *      {@link Module#getLock() read lock} on only the requesting
 *      module to prevent module state changes while data flow operations 
 *      are being carried out.</li>
 * </ol>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class ModuleManager {
    /**
     * Creates an instance that uses the same classloader as this class
     * to load module providers.
     */
    public ModuleManager() {
        this(ModuleManager.class.getClassLoader());
    }

    /**
     * Creates an instance that uses the supplied classloader to
     * load module providers. The supplied classloader is also set
     * as the thread context classloader when the MXBean implementations of
     * the module factories and instances are invoked.
     *
     * @param inClassLoader the classloader to use for loading module
     * providers.
     */
    public ModuleManager(ClassLoader inClassLoader) {
        mClassLoader = inClassLoader;
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
        synchronized (mModuleFactories) {
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
        return getModuleFactory(inProviderURN).getProviderInfo();
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
        for(ModuleURN moduleURI: mModules.getAllURNs()) {
            if(inProviderURN == null || inProviderURN.parentOf(moduleURI)) {
                urns.add(moduleURI);
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
     * @throws ModuleCreationException if an attempt was made to create
     * multiple instances of a singleton module OR if a module with the same
     * URN already exists OR if wrong number / type of parameters were
     * supplied to create the module.
     * @throws InvalidURNException if the created module's URN failed URN
     * validation.
     * @throws MXBeanOperationException if there problems registering
     * the module's MXBean with the MBean server.
     * @throws ModuleException if there was an error creating a new
     * module instance OR if this was an auto-start module, if there were
     * errors starting it. See {@link #start(ModuleURN)} for details on
     * possible errors when starting a module.
     *
     * @see #getProviderInfo(ModuleURN)
     */
    public ModuleURN createModule(ModuleURN inProviderURN, Object... inParameters)
            throws ModuleException {
        return createModuleImpl(inProviderURN, inParameters).getURN();
    }

    /**
     * Deletes the module identified by the supplied module URN.
     * Singleton instances of a module cannot be deleted. The module
     * should not be running when this method is invoked, otherwise
     * the operation fails with an exception.
     *
     * @param inModuleURN the module URN, that uniquely identifies
     * the module being deleted.
     *
     * @throws ModuleException if a module with the
     * supplied URN cannot be deleted
     * @throws ModuleStateException if the module is not in the correct
     * state to be deleted
     * @throws InvalidURNException if the supplied module URN is
     * not a valid URN.
     * @throws ModuleNotFoundException if the module matching
     * the URN was not found.
     * @throws BeanRegistrationException if unregistration of the module's
     * MBean failed.
     */
    public void deleteModule(ModuleURN inModuleURN)
            throws ModuleException {
        // URN validation already done in getModule()
        Module module = getModule(inModuleURN);
        ModuleURN providerURN = module.getURN().parent();
        assert providerURN != null;
        if(!getModuleFactory(providerURN).isMultipleInstances()) {
            throw new ModuleException(new I18NBoundMessage1P(
                    Messages.CANNOT_DELETE_SINGLETON,
                    inModuleURN.toString()));
        }
        Lock moduleLock = module.getLock().writeLock();
        //Acquire lock for module lifecycle changes and ensure
        //that no other operations are active on the module
        moduleLock.lock();
        try {
            //Verify that the module can still be found, ie. it didn't
            //get deleted by another thread before we acquired the lock.
            getModule(inModuleURN);
            //And is in the right state.
            if(!module.getState().canBeDeleted()) {
                throw new ModuleStateException(new I18NBoundMessage3P(
                        Messages.DELETE_FAILED_MODULE_STATE_INCORRECT,
                        inModuleURN.toString(),  module.getState(),
                        ModuleState.DELETABLE_STATES.toString()));
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
        } finally {
            moduleLock.unlock();
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
        Module module = getModule(inModuleURN);

        Set<DataFlowID> initiatedFlows =
                mDataFlows.getInitiatedFlows(inModuleURN);
        Set<DataFlowID> participatingFlows =
                mDataFlows.getFlowsParticipating(inModuleURN);
        return module.getModuleInfo(
                initiatedFlows == null
                        ? null
                        : initiatedFlows.toArray(new DataFlowID[initiatedFlows.size()]),
                participatingFlows == null
                        ? null
                        : participatingFlows.toArray(new DataFlowID[participatingFlows.size()]));
    }

    /**
     * Starts the module instance
     *
     * @param inModuleURN the module instance URN uniquely identifying
     * the module that needs to be started.
     *
     * @throws ModuleStateException if the module is not in the correct
     * state to be started.
     * @throws ModuleException if {@link Module#preStart()} threw an exception
     * OR if there were other errors starting the module.
     * @throws InvalidURNException if the module URN is invalid
     * @throws ModuleNotFoundException if the module was not found
     */
    public void start(ModuleURN inModuleURN) throws ModuleException {
        startModule(getModule(inModuleURN));
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
     * @throws ModuleStateException if the module is not in the correct state
     * to be stopped.
     * @throws DataFlowException if the module is participating in data flows
     * that it didn't initiate.
     * @throws ModuleException if {@link Module#preStop()} threw an exception
     * or if there were other errors stopping the module.
     */
    public void stop(ModuleURN inModuleURN) throws ModuleException {
        stopModule(getModule(inModuleURN));
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
     * @see #createDataFlow(DataRequest[], boolean) 
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
     * @throws DataFlowException if the data request wasn't specified correctly
     * OR if a non-emitter module was requested to participate as an emitter OR
     * if a non-receiver module was requested to participate as a receiver.
     * @throws ModuleStateException if the participating modules were not in
     * the correct state to be able to participate in the data flow.
     * @throws RequestDataException if any of the participating modules failed
     * when
     * {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)}  initiating}
     * the request.
     * @throws ModuleNotFoundException if a module instance corresponding to
     * the specified module URN could not be found.
     * @throws ModuleException if there were other errors setting up
     * data flow including errors instantiating any auto-instantiated modules
     * in the data flow.
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
     *
     * @throws ModuleStateException If the requesting module is not in the
     * correct state to be requesting cancellation of data flows.
     * @throws DataFlowNotFoundException if the data flow corresponding
     * to the supplied ID wasn't found.
     * @throws DataFlowException if the data flow is in the process of
     * being canceled by another operation.
     */
    public void cancel(DataFlowID inFlowID)
            throws ModuleStateException, DataFlowException {
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
        return mDataFlows.getDataFlows(inIncludeModuleCreated);
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
        DataFlow flow = mDataFlows.get(inFlowID);
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
        synchronized (mFlowHistory) {
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
     * <p>
     * This method should only be invoked once for an instance. Multiple
     * invocations of the method after the first one have undefined behavior.
     *
     * @throws ModuleException If there were errors initializing
     * the module framework.
     */
    public void init() throws ModuleException {
        //Register itself with the platform MBean server
        synchronized (mOperationsLock) {
            boolean failed = true;
            try {
                for(ModuleFactory factory: mLoader) {
                    initialize(factory);
                }
                SLF4JLoggerProxy.info(this, mModules.toString());
                // Supply this reference to the Sink module for sink listening to work
                ((SinkModule)getModule(SinkModuleFactory.INSTANCE_URN)).setManager(this);
                registerMXBean(new ModuleManagerMXBeanImpl(this),
                        new ObjectName(MODULE_MBEAN_NAME));
                failed = false;
            } catch (ServiceConfigurationError e) {
                throw new ModuleException(e, Messages.MODULE_CONFIGURATION_ERROR);
            } catch (JMException e) {
                throw new BeanRegistrationException(e,new I18NBoundMessage1P(
                        Messages.BEAN_REGISTRATION_ERROR,MODULE_MBEAN_NAME));
            } finally {
                if(failed) {
                    try {
                        stop();
                    } catch(Exception e) {
                        Messages.ERROR_CLEANING_UP_INIT_FAILURE.error(this, e);
                    }
                }
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
     * <p>
     * This method should be invoked before {@link #init()} is invoked.
     * Invocation of this method after  {@link #init()} has been invoked
     * has undefined behavior.
     *
     * @param inConfigurationProvider the module configuration provider instance
     * to use for this framework
     */
    public void setConfigurationProvider(
            ModuleConfigurationProvider inConfigurationProvider) {
        mConfigurationProvider = inConfigurationProvider;
    }

    /**
     * Stops the module manager and all the data flow activities.
     * This method, stops all the non-module initiated data flows first,
     * it then stops all the modules that have initiated data flows,
     * finally it stops all the running modules.
     *
     * @throws ModuleException if there were errors stopping the module manager.
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
     * @throws DataFlowException if the data request wasn't specified correctly
     * OR if a non-emitter module was requested to participate as an emitter OR
     * if a non-receiver module was requested to participate as a receiver.
     * @throws ModuleStateException if the participating modules were not in
     * the correct state to be able to participate in the data flow.
     * @throws RequestDataException if any of the participating modules failed
     * when
     * {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)}  initiating}
     * the request.
     * @throws ModuleNotFoundException if a module instance corresponding to
     * the specified module URN could not be found.
     * @throws ModuleException if there were other errors setting up
     * data flow including errors instantiating any auto-instantiated modules
     * in the data flow.
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
        Lock requesterLock = null;
        boolean failed = true;
        Module[] modules = null;
        try {
            // verify that the requester is in the right state to
            // be requesting data flows.
            if(inRequester != null) {
                //acquire the requester lock to prevent state changes to
                //it while the data flow is being setup.
                requesterLock = inRequester.getLock().readLock();
                requesterLock.lock();
                ModuleState state = inRequester.getState();
                if (!(state.canRequestFlows())) {
                    throw new ModuleStateException(new I18NBoundMessage3P(
                            Messages.DATAFLOW_FAILED_REQ_MODULE_STATE_INCORRECT,
                            inRequester.getURN().toString(), state,
                            ModuleState.REQUEST_FLOW_STATES.toString()));
                }
            }

            //Find and lock modules corresponding to each data request
            modules = findAndLockModules(inRequests, inRequester);

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
                //Acquire the lock on the sink module, like all other modules
                modules[modules.length - 1].getLock().readLock().lock();

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
                Module module = modules[i];
                //verify that all modules except the last one can emit data
                if((i < (modules.length - 1)) && !(module instanceof DataEmitter)) {
                    throw new DataFlowException(new I18NBoundMessage1P(
                            Messages.MODULE_NOT_EMITTER,
                            module.getURN().toString()));
                }
                //verify that all modules except the first one can receive data
                if(i > 0 && !(module instanceof DataReceiver)) {
                    throw new DataFlowException(new I18NBoundMessage1P(
                            Messages.MODULE_NOT_RECEIVER,
                            module.getURN().toString()));
                }
                //Check if the modules are in the state wherein they can
                //participate in data flows. Skip the check for the requester
                //as it's already been checked before.
                if(inRequester != module &&
                        (!module.getState().canParticipateFlows())) {
                    throw new ModuleStateException(new I18NBoundMessage3P(
                            Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                            module.getURN().toString(),
                            module.getState(),
                            ModuleState.PARTICIPATE_FLOW_STATES.toString()));
                }
            }
            // Start going backwards through the modules array plumbing them
            // plumbing the first module last, ensures that the rest of the data
            // pipeline is ready to receive data once the emitter starts emitting
            // data
            DataFlow flow = new DataFlow(this,
                    inRequester == null
                            ? null
                            : inRequester.getURN(),
                    inRequests, modules);
            DataFlowID id = flow.getFlowID();
            //Add the flow right away, to allow any concurrent flow
            //cancellations, that happen while the data flow is being
            //initialized, to not fail as they are not able to find the data
            //flow. 
            mDataFlows.addFlow(flow);
            try {
                flow.initFlow();
                failed = false;
            } finally {
                if(failed) {
                    //Remove the flow if it didn't initialize
                    mDataFlows.remove(id);
                }
            }
            return id;
        } finally {
            if(requesterLock != null) {
                requesterLock.unlock();
            }
            if(modules != null) {
                for(Module module: modules) {
                    module.getLock().readLock().unlock();
                }
            }
            //garbage collect any auto-created modules, if data flow creation
            //failed.
            if(failed && modules != null) {
                for(Module m: modules) {
                    removeIfOrphaned(m, null);
                }
            }
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
     * @throws ModuleStateException If the requesting module is not in the
     * correct state to be requesting cancellation of data flows.
     * @throws DataFlowNotFoundException if the data flow corresponding
     * to the supplied ID wasn't found.
     * @throws DataFlowException if the data flow is in the process of
     * being canceled by another operation.
     */
    void cancel(DataFlowID inFlowID, Module inRequester)
            throws ModuleStateException, DataFlowException {
        DataFlow flow;
        Lock requesterLock = null;
        DataFlowInfo dataFlowInfo = null;
        try {
            if(inRequester != null) {
                requesterLock = inRequester.getLock().readLock();
                //acquire requester lock to prevent requester state changes
                //while this operation is running.
                requesterLock.lock();
                ModuleState state = inRequester.getState();
                if (!state.canCancelFlows()) {
                    throw new ModuleStateException(new I18NBoundMessage4P(
                            Messages.CANCEL_FAILED_MODULE_STATE_INCORRECT,
                            inFlowID.getValue(),
                            inRequester.getURN().toString(), state,
                            ModuleState.CANCEL_FLOW_STATES.toString()));
                }
            }
            flow = mDataFlows.get(inFlowID);
            if(flow == null) {
                throw new DataFlowNotFoundException(new I18NBoundMessage1P(
                        Messages.DATA_FLOW_NOT_FOUND, inFlowID.getValue()));
            }
            //No need to acquire locks when cancelling flows as module
            //state changes are blocked until the module is participating
            //in the flow
            flow.cancel(inRequester == null
                    ? null
                    : inRequester.getURN());
            dataFlowInfo = flow.toDataFlowInfo();
            //Remove the data flow after the flow is canceled, so that
            //the participating modules cannot be stopped until flow
            //cancellation is complete
            mDataFlows.remove(inFlowID);
            addToFlowHistory(dataFlowInfo);
        } finally {
            if(requesterLock != null) {
                requesterLock.unlock();
            }
        }
        //figure out if there are any auto-created modules
        //and if they are not participating in any data flows
        //delete them
        for(DataFlowStep step: dataFlowInfo.getFlowSteps()) {
            removeIfOrphaned(step.getModuleURN(),
                    dataFlowInfo.getFlowID());
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
        mMaxFlowHistory = inMaxFlowHistory;
        //re-size the history records.
        addToFlowHistory(null);
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
     * @throws ModuleCreationException if an attempt was made to create
     * multiple instances of a singleton module OR if a module with the same
     * URN already exists OR if wrong number / type of parameters were
     * supplied to create the module.
     * @throws InvalidURNException if the created module's URN failed URN
     * validation.
     * @throws MXBeanOperationException if there problems registering
     * the module's MXBean with the MBean server.
     * @throws ModuleException if there was an error creating a new
     * module instance OR if this was an auto-start module, if there were
     * errors starting it.
     */
    private Module createModuleImpl(ModuleURN inProviderURN,
                                    Object... inParameters)
            throws ModuleException {
        //validate the URN
        URNUtils.validateProviderURN(inProviderURN);
        Module module;
        //find the provider factory
        ModuleFactory factory = getModuleFactory(inProviderURN);

        //check if this module supports multiple instances
        //and if it doesn't verify that no other instances exist
        //This is a check to fail early. Another thread-safe check is
        //performed later
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
     * @param inDataFlowInfo data flow info to add to the history.
     * null, if there are no records to add.
     */
    private void addToFlowHistory(DataFlowInfo inDataFlowInfo) {
        synchronized (mFlowHistory) {
            while (mFlowHistory.size() > mMaxFlowHistory) {
                mFlowHistory.removeLast();
            }
            if (inDataFlowInfo != null) {
                mFlowHistory.addFirst(inDataFlowInfo);
            }
        }
    }

    /**
     * This method is invoked when canceling a data flow.
     * This method will check if the module with the supplied URN is
     * auto-created. If it is and is not participating in any data flows,
     * it will delete the module.
     *
     * @param inModuleURN the module URN, cannot be null.
     * @param inFlowID the data flowID, can be null.
     *
     */
    private void removeIfOrphaned(ModuleURN inModuleURN, DataFlowID inFlowID) {
        Module m = mModules.get(inModuleURN);
        removeIfOrphaned(m, inFlowID);
    }

    /**
     * This method is invoked when canceling a data flow.
     * This method will check if the module with the supplied module is
     * auto-created. If it is and is not participating in any data flows,
     * it will delete the supplied module.
     *
     * @param inModule the module, if null, this method does nothing.
     * @param inFlowID the data flowID, can be null.
     *
     */
    private void removeIfOrphaned(Module inModule, DataFlowID inFlowID) {
        if(inModule != null && inModule.isAutoCreated()) {
            //delete the module if its not participating
            //in any data flows
            Lock moduleLock = inModule.getLock().writeLock();
            //Acquire write lock on the module to ensure that its state
            //flow participation doesn't change as we figure out if
            //we need to delete it and stop & delete it.
            moduleLock.lock();
            try {
                //verify that the module still exists, it case it got removed
                //before we acquired the lock
                if(!mModules.has(inModule.getURN())) {
                    return;
                }
                final Set<DataFlowID> flows =
                        mDataFlows.getFlowsParticipatingNotInitiated(inModule.getURN());
                if(flows == null || flows.isEmpty()) {
                    Messages.LOG_DELETE_AUTO_CREATED_MODULE.info(
                            this, inModule.getURN(),
                            inFlowID);
                    try {
                        stopModule(inModule);
                        deleteModule(inModule.getURN());
                    } catch (Exception e) {
                        Messages.LOG_DELETE_AUTO_CREATED_MODULE_FAIL.warn(this,
                                e, inModule.getURN());
                    }
                }
            } finally {
                moduleLock.unlock();
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
        factory = findFactoryWithURN(inUrn);
        if(factory == null) {
            throw new ProviderNotFoundException(new I18NBoundMessage1P(
                    Messages.PROVIDER_NOT_FOUND, inUrn.toString()));
        }
        return factory;
    }

    /**
     * Finds the factory with the supplied provider URN. Returns null
     * if a factory with the supplied provider URN is not found.
     *
     * @param inUrn the provider URN. The URN should be validated. This method
     * doesn't validate the URN and its behavior is unspecified if the supplied
     * URN is not validated.
     *
     * @return the module factory instance, if one having the supplied
     * provider URN was found, null otherwise.
     */
    private ModuleFactory findFactoryWithURN(ModuleURN inUrn) {
        synchronized (mModuleFactories) {
            return mModuleFactories.get(inUrn);
        }
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
        Module module = mModules.get(inModuleURN);
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
     * @throws ModuleStateException if the module is not in the correct state
     * to be stopped.
     * @throws DataFlowException if the module is participating in data flows
     * that it didn't initiate.
     * @throws ModuleException if {@link Module#preStop()} threw an exception
     * OR if there were other errors stopping the module.
     */
    private void stopModule(Module inModule) throws ModuleException {
        Set<DataFlowID> initiated;
        Lock moduleLock = inModule.getLock().writeLock();
        //acquire the lock for state changes.
        moduleLock.lock();
        try {
            //verify that the module is running
            ModuleState state = inModule.getState();
            if(!state.canBeStopped()) {
                throw new ModuleStateException(new I18NBoundMessage3P(
                        Messages.MODULE_NOT_STOPPED_STATE_INCORRECT,
                        inModule.getURN().toString(), state,
                        ModuleState.STOPPABLE_STATES.toString()));
            }
            //cannot stop the sink module
            if(inModule.getURN().equals(SinkModuleFactory.INSTANCE_URN)) {
                throw new ModuleException(Messages.CANNOT_STOP_SINK_MODULE);
            }
            //verify that the module is not participating in flows that it
            // didn't initiate
            Set<DataFlowID> participating = mDataFlows.
                    getFlowsParticipatingNotInitiated(inModule.getURN());
            if(participating != null && (!participating.isEmpty())) {
                throw new DataFlowException(new I18NBoundMessage2P(
                        Messages.CANNOT_STOP_MODULE_DATAFLOWS,
                        inModule.getURN().toString(),
                        participating.toString()));
            }
            inModule.setState(ModuleState.STOPPING);
        } finally {
            moduleLock.unlock();
        }
        boolean stopSucceeded = false;
        try {
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
            //Acquire the lock for all state changes.
            moduleLock.lock();
            try {
                if(stopSucceeded) {
                    inModule.setState(ModuleState.STOPPED);
                    inModule.setLastStopFailure(null);
                } else {
                    inModule.setState(ModuleState.STOP_FAILED);
                }
            } finally {
                moduleLock.unlock();
            }
        }
    }

    /**
     * Find module instances matching the specified requests and acquire their
     * read locks before they are returned. No read locks are acquired if this
     * method fails.
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
    private Module[] findAndLockModules(DataRequest[] inRequests,
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
        //lock all the modules before returning.
        for(Module m: modules) {
            m.getLock().readLock().lock();
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
            //Check if the module can be auto-instantiated.
            if(inModuleURN.instanceURN()) {
                ModuleURN parent = inModuleURN.parent();
                ModuleFactory factory = findFactoryWithURN(parent);
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
        synchronized (mModuleFactories) {
            if(!mModuleFactories.containsKey(urn)) {
                mModuleFactories.put(urn, inFactory);
            } else {
                //Ignore the factory if its already there.
                //This may happen during a refresh.
                Messages.LOG_INIT_FACTORY_IGNORE.info(this,
                        inFactory.getClass().getName());
                return;
            }
        }
        //Figure out if it has a MXBean interface
        if(isMXBean(inFactory)) {
            boolean mBeanOpsFailed = true;
            try {
                ObjectName objectName = registerMXBean(urn, inFactory);
                Messages.LOG_REGISTERED_FACTORY_BEAN.info(this, urn, objectName);
                //Configure the default values, if available
                boolean initDefaultValueFailed = true;
                try {
                    initializeDefaultValues(urn, objectName);
                    initDefaultValueFailed = false;
                } finally {
                    if(initDefaultValueFailed) {
                        try {
                            unregister(objectName);
                        } catch (JMException e) {
                            SLF4JLoggerProxy.debug(this, e,
                                    "Error unregistering MBean {} on init default value failure",  //$NON-NLS-1$
                                    objectName);
                        }
                    }
                }
                mBeanOpsFailed = false;
            } finally {
                if(mBeanOpsFailed) {
                    //remove the factory from the list initialized factories
                    synchronized (mModuleFactories) {
                        mModuleFactories.remove(urn);
                    }
                }
            }

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
        return registerMXBean(inMXBean, objectName);
    }

    /**
     * Registers the supplied Object instance with the MBean server.
     *
     * @param inMXBean the module factory / instance.
     * @param inObjectName The object name to use when registering the MBean
     *
     * @return the objectName of the registered bean
     *
     * @throws BeanRegistrationException if there were errors
     * registering the bean.
     */
    private ObjectName registerMXBean(Object inMXBean, ObjectName inObjectName) 
            throws BeanRegistrationException {
        Set<Class<?>> intfs = new HashSet<Class<?>>();
        for (Class<?> clazz = inMXBean.getClass();
             !Object.class.equals(clazz);
             clazz = clazz.getSuperclass()) {
            
            //We should need to only implement MXBean interfaces
            //However there are other interfaces that are special for
            //JMX, like NotificationEmitter, DynamicMBean, etc.
            //And there might be more of such interfaces in the future
            //Have the proxy implement all the interfaces, it's harmless
            //for the interfaces that are not used by the MBeanServer.
            intfs.addAll(Arrays.asList(clazz.getInterfaces()));
        }
        Object proxy = inMXBean;
        if (!intfs.isEmpty()) {
            proxy = Proxy.newProxyInstance(inMXBean.getClass().getClassLoader(),
                    intfs.toArray(new Class<?>[intfs.size()]),
                    new SetContextClassLoaderWrapper(inMXBean));
        }
        try {
            //Register the factory with the mbean server
            getMBeanServer().registerMBean(proxy, inObjectName);
        } catch (JMException e) {
            throw new BeanRegistrationException(e,
                    new I18NBoundMessage1P(
                            Messages.BEAN_REGISTRATION_ERROR, inObjectName));
        }
        return inObjectName;
    }

    /**
     * The invocation handler for each Module / Factory MX Bean. This
     * invocation handler sets up the thread context classloader to be
     * the same value as the classloader for the class Module / Factory
     * delegated to.
     */
    private class SetContextClassLoaderWrapper
            implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method,
                             Object[] args) throws Throwable {
            Thread thread = Thread.currentThread();
            ClassLoader loader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(ModuleManager.this.mClassLoader);
                return method.invoke(mDelegate, args);
            } catch(InvocationTargetException e) {
                throw e.getCause();
            } finally {
                thread.setContextClassLoader(loader);
            }
        }

        /**
         * Creates an instance.
         *
         * @param inDelegate the MBean instance that needs to wrapped up.
         * Cannot be null.
         */
        private SetContextClassLoaderWrapper(Object inDelegate) {
            if(inDelegate == null) {
                throw new NullPointerException();
            }
            mDelegate = inDelegate;
        }

        private final Object mDelegate;
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
        } catch (RuntimeException e) {
            //bean setters may throw runtime exception
            throw new MXBeanOperationException(e,
                    new I18NBoundMessage1P(
                            Messages.BEAN_ATTRIB_DISCOVERY_ERROR,
                            inObjectName));
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
     * @throws ModuleCreationException if an attempt was made to create
     * multiple instances of a singleton module OR if a module with the same
     * URN already exists.
     * @throws InvalidURNException if the created module's URN failed URN
     * validation.
     * @throws MXBeanOperationException if there problems registering
     * the module's MXBean with the MBean server. 
     * @throws ModuleException if there was an error creating a new
     * module instance OR if this was an auto-start module, if there were
     * errors starting it.
     */
    private Module createModule(ModuleFactory inFactory,
                                Object... inParameters)
            throws ModuleException {
        Module module;
        Lock factoryLock = null;
        ModuleURN urn;
        try {
            //Singleton factory create operations are synchronized on the factory
            //to ensure that a singleton factory is not asked to create more
            //than one instance.
            //Multiple factory create operations are synchronized on the factory
            //to ensure that it doesn't create modules with duplicate URNs.
            factoryLock = inFactory.getLock();
            factoryLock.lock();
            if(inFactory.isMultipleInstances()) {
                module = inFactory.create(inParameters);
            } else {
                List<ModuleURN> urns = getModuleInstances(inFactory.getProviderURN());
                if(!urns.isEmpty()) {
                    throw new ModuleCreationException(new I18NBoundMessage2P(
                            Messages.CANNOT_CREATE_SINGLETON,
                            inFactory.getProviderURN().toString(),
                            urns.get(0).toString()));
                }
                module = inFactory.create(inParameters);
            }

            urn = module.getURN();
            //validate module's URN, verify that it's of provider's type
            URNUtils.validateInstanceURN(urn, inFactory.getProviderURN());
            //Verify that a duplicate module is not being created.
            //We already have a factory lock and only the same factory
            //has the capability to create a module having a duplicate URN.
            //This scheme prevents concurrent instantiation of module instances
            //from the same provider, but is easier to implement than
            //implementing unique, module URN based locks.
            if(mModules.has(urn)) {
                throw new ModuleCreationException(new I18NBoundMessage1P(
                        Messages.DUPLICATE_MODULE_URN,urn.toString()));
            }
            //if its an mbean, register it and initialize its default values
            if(isMXBean(module)) {
                ObjectName objectName = registerMXBean(urn, module);
                Messages.LOG_REGISTERED_MODULE_BEAN.info(this, urn, objectName);
                //Configure default values if available
                boolean initDefaultValuesFailed = true;
                try {
                    initializeDefaultValues(urn,objectName);
                    initDefaultValuesFailed = false;
                } finally {
                    if(initDefaultValuesFailed) {
                        //unregister the MBean.
                        try {
                            unregister(objectName);
                        } catch (JMException e) {
                            SLF4JLoggerProxy.debug(this, e,
                                    "Error unregistering MBean {} on init default value failure", objectName);  //$NON-NLS-1$
                        }
                    }
                }
            }
            //add the module to appropriate lookup tables
            mModules.add(module);
        } finally {
            if(factoryLock != null) {
                factoryLock.unlock();
            }
        }
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
     * @throws ModuleStateException if the module is not in the correct
     * state to be started.
     * @throws ModuleException if {@link Module#preStart()} threw an exception
     * OR if there were other errors starting the module.
     */
    private void startModule(Module inModule) throws ModuleException {
        Lock moduleLock = inModule.getLock().writeLock();
        //Acquire the lock for module state changes
        moduleLock.lock();
        try {
            //Verify module still exists, in case it got deleted after we
            //found it but before we acquired the lock
            getModule(inModule.getURN());
            //Verify module state
            ModuleState state = inModule.getState();
            if(!state.canBeStarted()) {
                throw new ModuleStateException(new I18NBoundMessage3P(
                        Messages.MODULE_NOT_STARTED_STATE_INCORRECT,
                        inModule.getURN().toString(), state,
                        ModuleState.STARTABLE_STATES.toString()));
            }
            //Tell the module that its about to be started
            inModule.setState(ModuleState.STARTING);
        } finally {
            moduleLock.unlock();
        }
        boolean startSucceeded = false;
        try {
            if(inModule instanceof DataFlowRequester) {
                //supply it a data flow requester support
                //instance
                ((DataFlowRequester)inModule).setFlowSupport(
                        new DataFlowSupportImpl(inModule,this));
            }
            inModule.preStart();
            startSucceeded = true;
            Messages.LOG_MODULE_STARTED.info(this, inModule.getURN());
        } catch(ModuleException e) {
            inModule.setLastStartFailure(e.getLocalizedDetail());
            Messages.LOG_START_MODULE_FAILED.warn(this,e,inModule.getURN());
            throw e;
        } finally {
            //Acquire the lock for module state changes.
            moduleLock.lock();
            try {
                if(startSucceeded) {
                    inModule.setState(ModuleState.STARTED);
                    inModule.setLastStartFailure(null);
                } else {
                    inModule.setState(ModuleState.START_FAILED);
                }
            } finally {
                moduleLock.unlock();
            }
            if (!startSucceeded) {
                //check if the module created any data flows and if it
                //did, make sure they are canceled.
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
     * The classloader to use for loading providers.
     */
    private final ClassLoader mClassLoader;
    /**
     * The module configuration provider that provides default configuration
     * properties for module factories and instances.
     */
    private volatile ModuleConfigurationProvider mConfigurationProvider;
    /**
     * The table of currently active data flows.
     */
    private final DataFlowTracker mDataFlows =
            new DataFlowTracker();

    /**
     * History of flows that are not active any more
     */
    private final Deque<DataFlowInfo> mFlowHistory =
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

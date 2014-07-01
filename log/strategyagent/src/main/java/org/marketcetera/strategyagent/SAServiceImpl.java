package org.marketcetera.strategyagent;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.marketcetera.core.Util;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAService;
import org.marketcetera.saclient.rpc.SAServiceAdapter;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.CopyBytesUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */
/**
 * Implements the remote web services offered by the strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SAServiceImpl
        extends ServiceBaseImpl<ClientSession>
        implements SAService,SAServiceAdapter
{

    @Override
    public List<ModuleURN> getProviders(ClientContext inCtx)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,List<ModuleURN>>(getSessionManager()) {
            @Override
            protected List<ModuleURN> call(ClientContext context,
                                           SessionHolder<ClientSession> sessionHolder)
                    throws Exception
            {
                return doGetProviders();
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#getProviders()
     */
    @Override
    public List<ModuleURN> getProviders()
    {
        return doGetProviders();
    }
    @Override
    public List<ModuleURN> getInstances(ClientContext inCtx,
                                        final ModuleURN inProviderURN)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,List<ModuleURN>>(getSessionManager()) {
            @Override
            protected List<ModuleURN> call(ClientContext inContext,
                                           SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                return doGetInstances(inProviderURN);
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#getInstances(org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ModuleURN inProvider)
    {
        return doGetInstances(inProvider);
    }
    @Override
    public ModuleInfo getModuleInfo(ClientContext inCtx,
                                    final ModuleURN inURN)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,ModuleInfo>(getSessionManager()) {
            @Override
            protected ModuleInfo call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                    throws Exception
            {
                return doGetModuleInfo(inURN);
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#getModuleInfo(org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ModuleURN inInstance)
    {
        return doGetModuleInfo(inInstance);
    }
    @Override
    public void start(ClientContext inCtx,
                      final ModuleURN inURN) 
            throws RemoteException
    {
        new RemoteCaller<ClientSession,Void>(getSessionManager()) {
            @Override
            protected Void call(ClientContext inContext,
                                SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                doStart(inURN);
                return null;
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#start(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(ModuleURN inInstance)
    {
        doStart(inInstance);
    }
    @Override
    public void stop(ClientContext inCtx,
                     final ModuleURN inURN) 
            throws RemoteException
    {
        new RemoteCaller<ClientSession,Void>(getSessionManager()) {
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<ClientSession> sessionHolder)
                    throws Exception
            {
                doStop(inURN);
                return null;
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#stop(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ModuleURN inInstance)
    {
        doStop(inInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAService#sendData(org.marketcetera.util.ws.stateful.ClientContext, java.lang.Object)
     */
    @Override
    public void sendData(ClientContext inServiceContext,
                         final Object inData)
            throws RemoteException
    {
        new RemoteCaller<ClientSession,Void>(getSessionManager()) {
            @Override
            protected Void call(ClientContext inContext,
                                SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                doSendData(inData);
                return null;
            }
        }.execute(inServiceContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
    {
        doSendData(inData);
    }
    @Override
    public void delete(ClientContext inCtx, final ModuleURN inURN)
            throws RemoteException {
        new RemoteCaller<ClientSession, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                failIfNotStrategy(inURN, Messages.DELETE_MODULE_NOT_STRATEGY);
                mManager.deleteModule(inURN);
                //Remove the strategy create parameter value.
                mStrategies.remove(inURN);
                return null;
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#delete(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ModuleURN inInstance)
    {
        failOnNullURN(inInstance);
        failIfNotStrategy(inInstance,
                          Messages.DELETE_MODULE_NOT_STRATEGY);
        mManager.deleteModule(inInstance);
        //Remove the strategy create parameter value.
        mStrategies.remove(inInstance);
    }
    @Override
    public MapWrapper<String,Object> getProperties(ClientContext inCtx,
                                                   final ModuleURN inURN)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,MapWrapper<String,Object>>(getSessionManager()) {
            @Override
            protected MapWrapper<String,Object> call(ClientContext inContext,
                                                     SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                return new MapWrapper<String,Object>(doGetProperties(inURN));
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#getProperties(org.marketcetera.module.ModuleURN)
     */
    @Override
    public Map<String,Object> getProperties(ModuleURN inInstance)
    {
        try {
            return doGetProperties(inInstance);
        } catch (IntrospectionException | InstanceNotFoundException | ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public MapWrapper<String,Object> setProperties(ClientContext inCtx,
                                                   final ModuleURN inURN,
                                                   final MapWrapper<String,Object> inProperties)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,MapWrapper<String,Object>>(getSessionManager()) {
            @Override
            protected MapWrapper<String,Object> call(ClientContext inContext,
                                                     SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                return new MapWrapper<String,Object>(doSetProperties(inURN,
                                                                     inProperties.getMap()));
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#setProperties(org.marketcetera.module.ModuleURN, java.util.Map)
     */
    @Override
    public Map<String,Object> setProperties(ModuleURN inInstance,
                                            Map<String,Object> inProperties)
    {
        try {
            return doSetProperties(inInstance,
                                   inProperties);
        } catch (InstanceNotFoundException | ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ModuleURN createStrategy(ClientContext inCtx,
                                    final CreateStrategyParameters inParameters)
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,ModuleURN>(getSessionManager()) {
            @Override
            protected ModuleURN call(ClientContext inContext,
                                      SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                return doCreateStrategy(inParameters);
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#createStrategy(org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(CreateStrategyParameters inParameters)
    {
        try {
            return doCreateStrategy(inParameters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inCtx,
                                                           final ModuleURN inURN) 
            throws RemoteException
    {
        return new RemoteCaller<ClientSession,CreateStrategyParameters>(getSessionManager()) {
            @Override
            protected CreateStrategyParameters call(ClientContext inContext,
                                      SessionHolder<ClientSession> inSessionHolder)
                    throws Exception
            {
                return doGetStrategyCreateParms(inURN);
            }
        }.execute(inCtx);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAServiceAdapter#getStrategyCreateParms(org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ModuleURN inInstance)
    {
        return doGetStrategyCreateParms(inInstance);
    }
    /**
     * Creates an instance.
     *
     * @param inSessionManager the session manager.
     * @param inManager the module manager.
     * @param inPublisher an <code>IPublisher</code> value
     */
    public SAServiceImpl(SessionManager<ClientSession> inSessionManager,
                         ModuleManager inManager,
                         IPublisher inPublisher)
    {
        super(inSessionManager);
        mManager = inManager;
        dataPublisher = inPublisher;
    }
    /**
     * Throws an exception if the supplied URN is null.
     *
     * @param inURN the URN to test.
     *
     * @throws I18NException if the supplied URN is null.
     */
    private static void failOnNullURN(ModuleURN inURN) throws I18NException {
        if(inURN == null) {
            throw new I18NException(Messages.CANNOT_PROCESS_NULL_URN);
        }
    }

    /**
     * Throws an exception with the supplied message if the supplied URN
     * is not a strategy module URN.
     *
     * @param inURN the URN to test.
     * @param inFailureMessage the message to use in the exception.
     *
     * @throws I18NException if the supplied URN is not a strategy URN.
     */
    private static void failIfNotStrategy(ModuleURN inURN,
                                   I18NMessage1P inFailureMessage)
            throws I18NException {
        if(!STRATEGY_PROVIDER.parentOf(inURN)) {
            throw new I18NException(new I18NBoundMessage1P(
                    inFailureMessage, inURN));
        }
    }
    /**
     * Returns the MBean server to use for all MBean operations.
     *
     * @return the MBean server instance.
     */
    private static MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
    /**
     * Gets module info for the given URN.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>ModuleInfo</code> value
     */
    private ModuleInfo doGetModuleInfo(ModuleURN inInstance)
    {
        failOnNullURN(inInstance);
        return mManager.getModuleInfo(inInstance);
    }
    /**
     * Gets the list of providers.
     *
     * @return a <code>List&lt;ModuleURN</code> value
     */
    private List<ModuleURN> doGetProviders()
    {
        return mManager.getProviders();
    }
    /**
     * Gets the instances for the given provider URN.
     *
     * @param inProvider a <code>ModuleURN</code> value
     * @return a <code>List&lt;ModuleURN</code> value
     */
    private List<ModuleURN> doGetInstances(ModuleURN inProvider)
    {
        return mManager.getModuleInstances(inProvider);
    }
    /**
     * Stops the module with the given URN.
     *
     * @param inInstance a <code>ModuleURN</code> value
     */
    private void doStop(ModuleURN inInstance)
    {
        failOnNullURN(inInstance);
        failIfNotStrategy(inInstance,
                          Messages.STOP_MODULE_NOT_STRATEGY);
        mManager.stop(inInstance);
    }
    /**
     * Sends the given data.
     *
     * @param inData an <code>Object</code> value
     */
    private void doSendData(Object inData)
    {
        dataPublisher.publish(inData);
    }
    /**
     * Starts the module with the given URN.
     *
     * @param inInstance a <code>ModuleURN</code> value
     */
    private void doStart(ModuleURN inInstance)
    {
        failOnNullURN(inInstance);
        failIfNotStrategy(inInstance, Messages.START_MODULE_NOT_STRATEGY);
        mManager.start(inInstance);
    }
    /**
     * Gets the properties for the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>Map&lt;String,Object&gt;</code> value
     * @throws IntrospectionException if an error occurs transcribing the properties
     * @throws InstanceNotFoundException if an error occurs transcribing the properties
     * @throws ReflectionException if an error occurs transcribing the properties
     */
    private Map<String,Object> doGetProperties(ModuleURN inInstance)
            throws IntrospectionException, InstanceNotFoundException, ReflectionException
    {
        failOnNullURN(inInstance);
        ObjectName on = inInstance.toObjectName();
        MBeanServer beanServer = getMBeanServer();
        List<String> attribs = Lists.newArrayList();
        for(MBeanAttributeInfo info: beanServer.getMBeanInfo(on).getAttributes()) {
            attribs.add(info.getName());
        }
        AttributeList values = beanServer.getAttributes(on,
                                                        attribs.toArray(new String[attribs.size()]));
        Map<String,Object> props = Maps.newHashMap();
        for(Attribute a: values.asList()) {
            props.put(a.getName(), a.getValue());
        }
        return props;
    }
    /**
     * Sets the properties for the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @param inProperties a <code>Map&lt;String,Object&gt;</code> value
     * @return a <code>Map&lt;String,Object&gt;</code> value
     * @throws InstanceNotFoundException if an error occurs transcribing the properties
     * @throws ReflectionException if an error occurs transcribing the properties
     */
    private Map<String,Object> doSetProperties(ModuleURN inInstance,
                                               Map<String,Object> inProperties)
            throws InstanceNotFoundException, ReflectionException
    {
        failOnNullURN(inInstance);
        failIfNotStrategy(inInstance, Messages.SET_PROPERTY_MODULE_NOT_STRATEGY);
        AttributeList list = new AttributeList();
        Map<String,Object> output = Maps.newHashMap();
        if (inProperties != null) {
            for(String key: inProperties.keySet()) {
                if(!EDITABLE_STRATEGY_PROPERTIES.contains(key)) {
                    throw new I18NException(new I18NBoundMessage3P(Messages.UNEDITABLE_STRATEGY_PROPERTY,
                                                                   key, 
                                                                   inInstance,
                                                                   EDITABLE_STRATEGY_PROPERTIES.toString()));
                }
            }
            ObjectName on = inInstance.toObjectName();
            MBeanServer beanServer = getMBeanServer();
            beanServer.setAttributes(on,
                                     list);
            for(String key: inProperties.keySet()) {
                try {
                    Object value = inProperties.get(key);
                    beanServer.setAttribute(on,
                                            new Attribute(key,
                                                          value));
                    output.put(key, value);
                } catch (Exception e) {
                    output.put(key, new RemoteProperties(e));
                    Messages.LOG_ERROR_SET_ATTRIBUTE.error(this, e, key, inInstance);
                }
            }
        }
        return output;
    }
    /**
     * Creates a strategy with the given parameters.
     *
     * @param inParameters a <code>CreateStrategyParameters</code> value
     * @return a <code>ModuleURN</code> value
     * @throws IOException if an error occurs creating the strategy
     */
    private ModuleURN doCreateStrategy(CreateStrategyParameters inParameters)
            throws IOException
    {
        if(inParameters == null) {
            throw new I18NException(Messages.NO_STRATEGY_CREATE_PARMS_SPECIFIED);
        }
        // copy the input stream to file.
        File file = File.createTempFile("strat",  //$NON-NLS-1$
                                        ".tmp");  //$NON-NLS-1$
        file.deleteOnExit();
        CopyBytesUtils.copy(inParameters.getStrategySource(),
                            false,
                            file.getAbsolutePath());
        // Generate the strategy creation parameter referencing the local copy
        CreateStrategyParameters sp = new CreateStrategyParameters(inParameters.getInstanceName(),
                                                                   inParameters.getStrategyName(),
                                                                   inParameters.getLanguage(),
                                                                   file,
                                                                   inParameters.getParameters(),
                                                                   inParameters.isRouteOrdersToServer());
        ModuleURN urn = null;
        try {
            urn = mManager.createModule(STRATEGY_PROVIDER,
                                        inParameters.getInstanceName(),
                                        inParameters.getStrategyName(),
                                        inParameters.getLanguage(),
                                        file,
                                        Util.propertiesFromString(inParameters.getParameters()),
                                        inParameters.isRouteOrdersToServer(),
                                        REMOTE_RECEIVER);
            return urn;
        } finally {
            if(urn != null) {
                mStrategies.put(urn,
                                sp);
            }
        }
    }
    /**
     * Gets the strategy parameters for the given module.
     *
     * @param inInstance a <code>ModuleURN</code> value
     * @return a <code>CreateStrategyParameters</code> value
     */
    private CreateStrategyParameters doGetStrategyCreateParms(ModuleURN inInstance)
    {
        failOnNullURN(inInstance);
        CreateStrategyParameters parameters = mStrategies.get(inInstance);
        if(parameters == null) {
            throw new I18NException(new I18NBoundMessage1P(Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY,
                                                           inInstance));
        }
        return parameters;
    }
    /**
     * publisher responsible for distributing data to interested publishers
     */
    private final IPublisher dataPublisher;
    private final ModuleManager mManager;
    private final Map<ModuleURN,CreateStrategyParameters> mStrategies = Maps.newConcurrentMap();
    private static final ModuleURN REMOTE_RECEIVER = ReceiverFactory.PROVIDER_URN;
    private static final ModuleURN STRATEGY_PROVIDER = new ModuleURN("metc:strategy:system");   //$NON-NLS-1$
    static final Set<String> EDITABLE_STRATEGY_PROPERTIES =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "Parameters",  //$NON-NLS-1$
                    "RoutingOrdersToORS"  //$NON-NLS-1$
            )));
}

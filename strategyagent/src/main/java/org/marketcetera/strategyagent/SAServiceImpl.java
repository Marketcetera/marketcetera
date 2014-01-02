package org.marketcetera.strategyagent;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.*;

import org.marketcetera.core.Util;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.CopyBytesUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

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
        implements SAService
{

    @Override
    public List<ModuleURN> getProviders(ClientContext inCtx)
            throws RemoteException {
        return new RemoteCaller<ClientSession, List<ModuleURN>>(
                getSessionManager()){
            @Override
            protected List<ModuleURN> call(
                    ClientContext context,
                    SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                return mManager.getProviders();
            }
        }.execute(inCtx);
    }

    @Override
    public List<ModuleURN> getInstances(ClientContext inCtx,
                                        final ModuleURN inProviderURN)
            throws RemoteException {
        return new RemoteCaller<ClientSession, List<ModuleURN>>(
                getSessionManager()){
            @Override
            protected List<ModuleURN> call(
                    ClientContext context,
                    SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                return mManager.getModuleInstances(inProviderURN);
            }
        }.execute(inCtx);
    }

    @Override
    public ModuleInfo getModuleInfo(ClientContext inCtx,
                                    final ModuleURN inURN)
            throws RemoteException {
        return new RemoteCaller<ClientSession, ModuleInfo>(
                getSessionManager()){
            @Override
            protected ModuleInfo call(
                    ClientContext context,
                    SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                return mManager.getModuleInfo(inURN);
            }
        }.execute(inCtx);
    }

    @Override
    public void start(ClientContext inCtx,
                      final ModuleURN inURN) 
            throws RemoteException {
        new RemoteCaller<ClientSession, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                failIfNotStrategy(inURN, Messages.START_MODULE_NOT_STRATEGY);
                mManager.start(inURN);
                return null;
            }
        }.execute(inCtx);
    }

    @Override
    public void stop(ClientContext inCtx,
                     final ModuleURN inURN) 
            throws RemoteException {
        new RemoteCaller<ClientSession, Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext context,
                                SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                failIfNotStrategy(inURN, Messages.STOP_MODULE_NOT_STRATEGY);
                mManager.stop(inURN);
                return null;
            }
        }.execute(inCtx);
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
            protected Void call(ClientContext context,
                                SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                dataPublisher.publish(inData);
                return null;
            }
        }.execute(inServiceContext);
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

    @Override
    public MapWrapper<String,Object> getProperties(ClientContext inCtx,
                                                   final ModuleURN inURN)
            throws RemoteException {
        return new RemoteCaller<ClientSession, MapWrapper<String,Object>>(
                getSessionManager()){
            @Override
            protected MapWrapper<String,Object> call(
                    ClientContext context,
                    SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                ObjectName on = inURN.toObjectName();
                MBeanServer beanServer = getMBeanServer();
                List<String> attribs = new ArrayList<String>();
                for(MBeanAttributeInfo info: beanServer.getMBeanInfo(on).getAttributes()) {
                    attribs.add(info.getName());
                }
                AttributeList values = beanServer.getAttributes(on, attribs.toArray(
                        new String[attribs.size()]));
                Map<String,Object> props = new HashMap<String, Object>();
                for(Attribute a: values.asList()) {
                    props.put(a.getName(), a.getValue());
                }
                return new MapWrapper<String,Object>(props);
            }
        }.execute(inCtx);
    }

    @Override
    public MapWrapper<String, Object> setProperties(ClientContext inCtx,
                              final ModuleURN inURN,
                              final MapWrapper<String, Object> inProperties)
            throws RemoteException {
        return new RemoteCaller<ClientSession, MapWrapper<String,Object>>(
                getSessionManager()){
            @Override
            protected MapWrapper<String, Object> call(
                    ClientContext context,
                    SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                failIfNotStrategy(inURN, Messages.SET_PROPERTY_MODULE_NOT_STRATEGY);
                AttributeList list = new AttributeList();
                Map<String,Object> output = new HashMap<String, Object>();
                Map<String, Object> map = inProperties.getMap();
                if (map != null) {
                    for(String key: map.keySet()) {
                        if(!EDITABLE_STRATEGY_PROPERTIES.contains(key)) {
                            throw new I18NException(new I18NBoundMessage3P(
                                    Messages.UNEDITABLE_STRATEGY_PROPERTY, key, 
                                    inURN, EDITABLE_STRATEGY_PROPERTIES.toString()));
                        }
                    }
                    ObjectName on = inURN.toObjectName();
                    MBeanServer beanServer = getMBeanServer();
                    beanServer.setAttributes(on, list);
                    for(String key: map.keySet()) {
                        try {
                            Object value = map.get(key);
                            beanServer.setAttribute(on, new Attribute(key, value));
                            output.put(key, value);
                        } catch (Exception e) {
                            output.put(key, new RemoteProperties(e));
                            Messages.LOG_ERROR_SET_ATTRIBUTE.error(this, e, key, inURN);
                        }
                    }
                }
                return new MapWrapper<String, Object>(output);
            }
        }.execute(inCtx);
    }

    @Override
    public ModuleURN createStrategy(ClientContext inCtx,
                                    final CreateStrategyParameters inParameters)
            throws RemoteException {
        return new RemoteCaller<ClientSession, ModuleURN>(getSessionManager()){
            @Override
            protected ModuleURN call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                if(inParameters == null) {
                    throw new I18NException(Messages.NO_STRATEGY_CREATE_PARMS_SPECIFIED);
                }
                //copy the input stream to file.
                File file = File.createTempFile("strat", ".tmp");  //$NON-NLS-1$ $NON-NLS-2$
                file.deleteOnExit();
                CopyBytesUtils.copy(inParameters.getStrategySource(), false, file.getAbsolutePath());
                //Generate the strategy creation parameter referencing the local copy
                CreateStrategyParameters sp = new CreateStrategyParameters(
                        inParameters.getInstanceName(),
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
                        mStrategies.put(urn, sp);
                    }
                }
            }
        }.execute(inCtx);
    }

    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inCtx,
                                                           final ModuleURN inURN) 
            throws RemoteException {
        return new RemoteCaller<ClientSession, CreateStrategyParameters>(getSessionManager()){
            @Override
            protected CreateStrategyParameters call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                    throws Exception {
                failOnNullURN(inURN);
                CreateStrategyParameters parameters = mStrategies.get(inURN);
                if(parameters == null) {
                    throw new I18NException(new I18NBoundMessage1P(
                            Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY, inURN));
                }
                return parameters;
            }
        }.execute(inCtx);
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
     * publisher responsible for distributing data to interested publishers
     */
    private final IPublisher dataPublisher;
    private final ModuleManager mManager;
    private final Map<ModuleURN, CreateStrategyParameters> mStrategies =
            new ConcurrentHashMap<ModuleURN, CreateStrategyParameters>();
    private static final ModuleURN REMOTE_RECEIVER = ReceiverFactory.PROVIDER_URN;
    private static final ModuleURN STRATEGY_PROVIDER = new ModuleURN("metc:strategy:system");   //$NON-NLS-1$
    static final Set<String> EDITABLE_STRATEGY_PROPERTIES =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "Parameters",  //$NON-NLS-1$
                    "RoutingOrdersToORS"  //$NON-NLS-1$
            )));
}

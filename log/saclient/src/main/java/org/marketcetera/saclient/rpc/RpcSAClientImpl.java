package org.marketcetera.saclient.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.core.CloseableLock;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.AbstractSAClient;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.saclient.SAClientVersion;
import org.marketcetera.saclient.rpc.RpcSAClient.Locale;
import org.marketcetera.saclient.rpc.RpcSAClient.LoginRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.LoginResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.LogoutRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService;
import org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;

/* $License$ */

/**
 * Provides an RPC implementation of {@link SAClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class RpcSAClientImpl
        extends AbstractSAClient
{
    /**
     * Create a new RpcSAClientImpl instance.
     *
     * @param inParameters an <code>SAClientParameters</code> value
     */
    RpcSAClientImpl(SAClientParameters inParameters)
    {
        super(inParameters);
        contextClassProvider = inParameters.getContextClassProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getProviders()
     */
    @Override
    public List<ModuleURN> getProviders()
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.ProvidersResponse response = clientService.getProviders(controller,
                                                                                RpcSAClient.ProvidersRequest.newBuilder().setSessionId(sessionId.getValue()).build());
            List<ModuleURN> providers = Lists.newArrayList();
            for(RpcSAClient.ModuleURN provider : response.getProviderList()) {
                providers.add(new ModuleURN(provider.getValue()));
            }
            return providers;
        } catch (ServiceException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getInstances(org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ModuleURN inProviderURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.InstancesResponse response = clientService.getInstances(controller,
                                                                                RpcSAClient.InstancesRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                    .setProvider(RpcSAClient.ModuleURN.newBuilder().setValue(inProviderURN.getValue())).build());
            List<ModuleURN> instances = Lists.newArrayList();
            for(RpcSAClient.ModuleURN instance : response.getInstanceList()) {
                instances.add(new ModuleURN(instance.getValue()));
            }
            return instances;
        } catch (ServiceException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getModuleInfo(org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.ModuleInfoResponse response = clientService.getModuleInfo(controller,
                                                                                  RpcSAClient.ModuleInfoRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                      .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            ModuleInfo info = null;
            if(response.hasInfo()) {
                info = unmarshal(response.getInfo().getPayload());
            }
            return info;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#start(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            clientService.start(controller,
                                RpcSAClient.StartRequest.newBuilder().setSessionId(sessionId.getValue())
                                    .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            return;
        } catch (ServiceException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#stop(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            clientService.stop(controller,
                               RpcSAClient.StopRequest.newBuilder().setSessionId(sessionId.getValue())
                                   .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            return;
        } catch (ServiceException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#delete(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            clientService.delete(controller,
                                 RpcSAClient.DeleteRequest.newBuilder().setSessionId(sessionId.getValue())
                                     .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            return;
        } catch (ServiceException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getProperties(org.marketcetera.module.ModuleURN)
     */
    @Override
    public Map<String,Object> getProperties(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.GetPropertiesResponse response = clientService.getProperties(controller,
                                                                                     RpcSAClient.GetPropertiesRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                         .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            Map<String,Object> properties = Maps.newHashMap();
            for(RpcSAClient.Entry entry : response.getProperties().getEntryList()) {
                String key = entry.getKey();
                Object value = ((XmlValue)unmarshal(entry.getValue())).getValue();
                properties.put(key,
                               value);
            }
            return properties;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#setProperties(org.marketcetera.module.ModuleURN, java.util.Map)
     */
    @Override
    public Map<String,Object> setProperties(ModuleURN inURN,
                                            Map<String,Object> inProperties)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.Properties.Builder propertiesBuilder = RpcSAClient.Properties.newBuilder();
            for(Map.Entry<String,Object> entry : inProperties.entrySet()) {
                RpcSAClient.Entry.Builder entryBuilder = RpcSAClient.Entry.newBuilder();
                entryBuilder.setKey(entry.getKey());
                // note that this assumes that all values are marshallable
                try {
                    entryBuilder.setValue(marshal(new XmlValue(entry.getValue())));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                propertiesBuilder.addEntry(entryBuilder.build());
            }
            RpcSAClient.SetPropertiesResponse response = clientService.setProperties(controller,
                                                                                     RpcSAClient.SetPropertiesRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                     .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue()))
                                                                                     .setProperties(propertiesBuilder.build()).build());
            Map<String,Object> properties = Maps.newHashMap();
            for(RpcSAClient.Entry entry : response.getProperties().getEntryList()) {
                String key = entry.getKey();
                Object value = ((XmlValue)unmarshal(entry.getValue())).getValue();
                properties.put(key,
                               value);
            }
            return properties;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#createStrategy(org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(CreateStrategyParameters inParameters)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.CreateStrategyResponse response = clientService.createStrategy(controller,
                                                                                       RpcSAClient.CreateStrategyRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                           .setCreateStrategyParameters(RpcSAClient.CreateStrategyParameters.newBuilder().setPayload(marshal(inParameters)).build()).build());
            ModuleURN instance = new ModuleURN(response.getInstance().getValue());
            return instance;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getStrategyCreateParms(org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ModuleURN inURN)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            RpcSAClient.StrategyCreateParmsResponse response = clientService.getStrategyCreateParms(controller,
                                                                                                    RpcSAClient.StrategyCreateParmsRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                                        .setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
            CreateStrategyParameters params = unmarshal(response.getCreateStrategyParameters().getPayload());
            return params;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            // note that inData must be JAXB marshallable
            clientService.sendData(controller,
                                   RpcSAClient.SendDataRequest.newBuilder().setSessionId(sessionId.getValue())
                                       .setPayload(marshal(new XmlValue(inData))).build());
            return;
        } catch (ServiceException | JAXBException e) {
            throw wrapRemoteFailure(e);
        }
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.AbstractSAClient#doStart()
     */
    @Override
    protected void doStart()
    {
        try {
            synchronized(contextLock) {
                context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
                marshaller = context.createMarshaller();
                unmarshaller = context.createUnmarshaller();
            }
            startService();
            heartbeatFuture = heartbeatService.scheduleAtFixedRate(new HeartbeatMonitor(),
                                                                   heartbeatInterval,
                                                                   heartbeatInterval,
                                                                   TimeUnit.MILLISECONDS);
        } catch (IOException | ServiceException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.AbstractSAClient#doStop()
     */
    @Override
    protected void doStop()
    {
        if(heartbeatFuture != null) {
            try {
                heartbeatFuture.cancel(true);
            } catch (Exception ignored) {}
        }
    }
    /**
     * Starts the remote service.
     *
     * @throws IOException if an error occurs starting the service
     * @throws ServiceException if an error occurs starting the service
     */
    private void startService()
            throws IOException, ServiceException
    {
        try(CloseableLock startLock = CloseableLock.create(serviceLock.writeLock())) {
            startLock.lock();
            SLF4JLoggerProxy.debug(this,
                                   "Connecting to RPC server at {}:{}", //$NON-NLS-1$
                                   parameters.getHostname(),
                                   parameters.getPort());
            PeerInfo server = new PeerInfo(parameters.getHostname(),
                                           parameters.getPort());
            DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
            executor = new ThreadPoolCallExecutor(1,
                                                  10);
            clientFactory.setRpcServerCallExecutor(executor);
            clientFactory.setConnectResponseTimeoutMillis(10000);
            clientFactory.setCompression(true);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.handler(clientFactory);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY,
                             true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                             10000);
            bootstrap.option(ChannelOption.SO_SNDBUF,
                             1048576);
            bootstrap.option(ChannelOption.SO_RCVBUF,
                             1048576);
            CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
            logger.setLogRequestProto(false);
            logger.setLogResponseProto(false);
            clientFactory.setRpcLogger(logger);
            channel = clientFactory.peerWith(server,
                                             bootstrap);
            clientService = RpcSAClientService.newBlockingStub(channel);
            controller = channel.newRpcController();
            java.util.Locale currentLocale = java.util.Locale.getDefault();
            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setAppId(SAClientVersion.APP_ID.getValue())
                    .setVersionId(SAClientVersion.APP_ID_VERSION.getVersionInfo())
                    .setClientId(NodeId.generate().getValue())
                    .setLocale(Locale.newBuilder()
                               .setCountry(currentLocale.getCountry()==null?"":currentLocale.getCountry()) //$NON-NLS-1$
                               .setLanguage(currentLocale.getLanguage()==null?"":currentLocale.getLanguage()) //$NON-NLS-1$
                               .setVariant(currentLocale.getVariant()==null?"":currentLocale.getVariant()).build()) //$NON-NLS-1$
                               .setUsername(parameters.getUsername())
                               .setPassword(new String(parameters.getPassword())).build();
            LoginResponse loginResponse = clientService.login(controller,
                                                              loginRequest);
            sessionId = new SessionId(loginResponse.getSessionId());
            connectionStatusChanged(isRunning(),
                                    true);
        }
    }
    /**
     * Stops the remote service.
     */
    private void stopService()
    {
        try(CloseableLock stopLock = CloseableLock.create(serviceLock.writeLock())) {
            stopLock.lock();
            try {
                clientService.logout(controller,
                                     LogoutRequest.newBuilder().setSessionId(sessionId.getValue()).build());
            } catch (Exception ignored) {}
            if(executor != null) {
                try {
                    executor.shutdownNow();
                } catch (Exception ignored) {}
            }
            if(channel != null) {
                try {
                    channel.close();
                } catch (Exception ignored) {}
            }
        } finally {
            executor = null;
            controller = null;
            clientService = null;
            channel = null;
            sessionId = null;
            running.set(false);
        }
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private String marshal(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(contextLock) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings("unchecked")
    private <Clazz> Clazz unmarshal(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * Sends heartbeats and monitors the responses.
     * 
     * <p>This class also manages reconnection, if necessary.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private class HeartbeatMonitor
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            if(!isRunning() && !shutdownRequested.get()) {
                try {
                    stopService();
                    startService();
                } catch (Exception ignored) {}
            }
            try(CloseableLock heartbeatLock = CloseableLock.create(serviceLock.readLock())) {
                heartbeatLock.lock();
                clientService.heartbeat(controller,
                                        RpcSAClient.HeartbeatRequest.newBuilder().setId(System.nanoTime()).build());
            } catch (Exception e) {
                // heartbeat failed for some reason
                SLF4JLoggerProxy.debug(RpcSAClientImpl.this,
                                       e,
                                       "Heartbeat failed"); //$NON-NLS-1$
                connectionStatusChanged(isRunning(),
                                        false);
            }
        }
    }
    /**
     * guards access to RPC service objects
     */
    private final ReadWriteLock serviceLock = new ReentrantReadWriteLock();
    /**
     * provides access to RPC services
     */
    private BlockingInterface clientService;
    /**
     * executes the nitty-gritty of the calls
     */
    private RpcServerCallExecutor executor;
    /**
     * channel over which calls are made
     */
    private RpcClientChannel channel;
    /**
     * controller responsible for the RPC connection
     */
    private RpcController controller;
    /**
     * guards access to JAXB context objects
     */
    private final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private Unmarshaller unmarshaller;
    /**
     * provides context classes for marshalling/unmarshalling, may be <code>null</code>
     */
    private ContextClassProvider contextClassProvider;
    /**
     * session ID value for this connection, may be <code>null</code> if the connection is inactive
     */
    private SessionId sessionId;
    /**
     * indicates that a shutdown has been requested
     */
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    /**
     * stores a handle to the heartbeat scheduled job
     */
    private ScheduledFuture<?> heartbeatFuture;
    /**
     * interval at which to execute heartbeats
     */
    private long heartbeatInterval = 10000;
    /**
     * executes heartbeats
     */
    private final ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1);
}

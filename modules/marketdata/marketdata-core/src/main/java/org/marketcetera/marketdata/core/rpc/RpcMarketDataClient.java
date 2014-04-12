package org.marketcetera.marketdata.core.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.client.ClientVersion;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.Locale;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LogoutRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.trade.Instrument;
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

/* $License$ */

/**
 * Provides market data services via RPC.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class RpcMarketDataClient
        implements MarketDataServiceClient
{
    /**
     * Create a new MarketDataServiceRpcClient instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public RpcMarketDataClient(String inUsername,
                               String inPassword,
                               String inHostname,
                               int inPort,
                               ContextClassProvider inContextClassProvider)
    {
        username = inUsername;
        password = inPassword;
        hostname = inHostname;
        port = inPort;
        contextClassProvider = inContextClassProvider;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        try {
            // TODO sort out sync
            context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
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
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        exit.set(true);
        try {
            if(heartbeatFuture != null) {
                try {
                    heartbeatFuture.cancel(true);
                } catch (Exception ignored) {}
            }
            try {
                stopService();
            } catch (Exception ignored) {}
        } finally {
            heartbeatFuture = null;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(MarketDataRequest inRequest,
                        boolean inStreamEvents)
    {
        SLF4JLoggerProxy.debug(this,
                               "MarketDataRequest: {}",
                               inRequest);
        try(CloseableLock requestLock = CloseableLock.create(serviceLock.readLock())) {
            requestLock.lock();
            RpcMarketdata.MarketDataResponse response = clientService.request(controller,
                                                                              RpcMarketdata.MarketDataRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                              .setRequest(inRequest.toString())
                                                                              .setStreamEvents(inStreamEvents).build());
            SLF4JLoggerProxy.debug(this,
                                   "MarketDataResponse: {}",
                                   response.getId());
            return response.getId();
        } catch (ServiceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getLastUpdate(long)
     */
    @Override
    public long getLastUpdate(long inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "GetLastUpdate: {}",
                               inRequestId);
        try(CloseableLock requestLock = CloseableLock.create(serviceLock.readLock())) {
            requestLock.lock();
            RpcMarketdata.LastUpdateResponse response = clientService.getLastUpdate(controller,
                                                                                    RpcMarketdata.LastUpdateRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                        .setId(inRequestId).build());
            SLF4JLoggerProxy.debug(this,
                                   "GetLastUpdateResponse: {}",
                                   response.getTimestamp());
            return response.getTimestamp();
        } catch (ServiceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#cancel(long)
     */
    @Override
    public void cancel(long inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Cancel: {}",
                               inRequestId);
        try(CloseableLock requestLock = CloseableLock.create(serviceLock.readLock())) {
            requestLock.lock();
            RpcMarketdata.CancelResponse response = clientService.cancel(controller,
                                                                         RpcMarketdata.CancelRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                             .setId(inRequestId).build());
            SLF4JLoggerProxy.debug(this,
                                   "Cancel Response: {}",
                                   response);
            return;
        } catch (ServiceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getEvents(long)
     */
    @Override
    public Deque<Event> getEvents(long inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "GetEvents: {}",
                               inRequestId);
        try(CloseableLock requestLock = CloseableLock.create(serviceLock.readLock())) {
            requestLock.lock();
            RpcMarketdata.EventsResponse response = clientService.getEvents(controller,
                                                                            RpcMarketdata.EventsRequest.newBuilder().setSessionId(sessionId.getValue())
                                                                                .setId(inRequestId).build());
            Deque<Event> events = Lists.newLinkedList();
            for(String payload : response.getPayloadList()) {
                events.add((Event)unmarshall(payload));
            }
            SLF4JLoggerProxy.debug(this,
                                   "GetEventsResponse: {}",
                                   events);
            return events;
        } catch (ServiceException | JAXBException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getAllEvents(java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent,
                                    String inProvider)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.marketdata.core.webservice.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(Instrument inInstrument,
                                        Content inContent,
                                        String inProvider,
                                        PageRequest inPage)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#addServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(final ServerStatusListener inListener)
    {
        synchronized(serverStatusSubscribers) {
            ISubscriber subscriberProxy = new ISubscriber() {
                @Override
                public boolean isInteresting(Object inData)
                {
                    return inData instanceof Boolean;
                }
                @Override
                public void publishTo(Object inData)
                {
                    inListener.receiveServerStatus((Boolean)inData);
                }
            };
            serverStatusSubscribers.put(inListener,
                                        subscriberProxy);
        }
        inListener.receiveServerStatus(isRunning());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#removeServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        synchronized(serverStatusSubscribers) {
            ISubscriber subscriberProxy = serverStatusSubscribers.remove(inListener);
            if(subscriberProxy != null) {
                publisher.unsubscribe(subscriberProxy);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        throw new UnsupportedOperationException(); // TODO
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
    /**
     * 
     *
     *
     * @param inObject
     * @return
     * @throws JAXBException
     */
    private String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(marshaller) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * 
     *
     *
     * @param inData
     * @return
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    private <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(unmarshaller) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * 
     *
     *
     */
    private void setServerStatus(boolean inStatus)
    {
        if(inStatus == isRunning()) {
            return;
        }
        running.set(inStatus);
        synchronized(serverStatusSubscribers) {
            for(ISubscriber subscriber : serverStatusSubscribers.values()) {
                if(subscriber.isInteresting(inStatus)) {
                    subscriber.publishTo(inStatus);
                }
            }
        }
    }
    /**
     * 
     *
     *
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
     * 
     *
     *
     * @throws IOException
     * @throws ServiceException
     */
    private void startService()
            throws IOException, ServiceException
    {
        try(CloseableLock startLock = CloseableLock.create(serviceLock.writeLock())) {
            startLock.lock();
            SLF4JLoggerProxy.debug(this,
                                   "Connecting to RPC server at {}:{}",
                                   hostname,
                                   port);
            PeerInfo server = new PeerInfo(hostname,
                                           port);
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
            channel = clientFactory.peerWith(server,
                                             bootstrap);
            clientService = RpcClientService.newBlockingStub(channel);
            controller = channel.newRpcController();
            java.util.Locale currentLocale = java.util.Locale.getDefault();
            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setAppId(ClientVersion.APP_ID.getValue())
                    .setVersionId(ClientVersion.APP_ID_VERSION.getVersionInfo())
                    .setClientId(NodeId.generate().getValue())
                    .setLocale(Locale.newBuilder()
                               .setCountry(currentLocale.getCountry()==null?"":currentLocale.getCountry())
                               .setLanguage(currentLocale.getLanguage()==null?"":currentLocale.getLanguage())
                               .setVariant(currentLocale.getVariant()==null?"":currentLocale.getVariant()).build())
                               .setUsername(username)
                               .setPassword(new String(password)).build();
            LoginResponse loginResponse = clientService.login(controller,
                                                              loginRequest);
            sessionId = new SessionId(loginResponse.getSessionId());
            setServerStatus(true);
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
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
            if(!isRunning() && !exit.get()) {
                try {
                    stopService();
                    startService();
                } catch (Exception ignored) {}
            }
            try(CloseableLock heartbeatLock = CloseableLock.create(serviceLock.readLock())) {
                heartbeatLock.lock();
                clientService.heartbeat(controller,
                                        RpcMarketdata.HeartbeatRequest.newBuilder().setId(System.nanoTime()).build());
            } catch (Exception e) {
                // heartbeat failed for some reason
                SLF4JLoggerProxy.debug(RpcMarketDataClient.this,
                                       e,
                                       "Heartbeat failed");
                setServerStatus(false);
            }
        }
    }
    /**
     * 
     */
    private final AtomicBoolean exit = new AtomicBoolean(false);
    /**
     * 
     */
    private ScheduledFuture<?> heartbeatFuture;
    /**
     * 
     */
    private ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1);
    /**
     * 
     */
    @GuardedBy("serverStatusSubscribers")
    private final Map<ServerStatusListener,ISubscriber> serverStatusSubscribers = Maps.newHashMap();
    /**
     * 
     */
    private final PublisherEngine publisher = new PublisherEngine(true);
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
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
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private ContextClassProvider contextClassProvider;
    /**
     * session ID value for this connection, may be <code>null</code> if the connection is inactive
     */
    private SessionId sessionId;
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
     * 
     */
    private long heartbeatInterval = 10000;
}

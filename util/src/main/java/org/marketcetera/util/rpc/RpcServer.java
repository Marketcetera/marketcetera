package org.marketcetera.util.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.springframework.context.Lifecycle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.BlockingService;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/* $License$ */

/**
 * Provides RPC services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RpcServer<SessionClazz>
        implements Lifecycle,RpcServerServices<SessionClazz>
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    @PostConstruct
    public synchronized void start()
    {
        Validate.notNull(rpcHostname);
        Validate.isTrue(rpcPort > 0 && rpcPort < 65536);
        Validate.notNull(sessionManager);
        Validate.notNull(authenticator);
        Validate.isTrue(threadPoolCore > 0);
        Validate.isTrue(threadPoolMax > 0);
        Validate.isTrue(threadPoolMax >= threadPoolCore);
        Validate.isTrue(sendBufferSize > 0);
        Validate.isTrue(receiveBufferSize > 0);
        Validate.notEmpty(serviceSpecs);
        Messages.SERVER_STARTING.info(this,
                                      rpcHostname,
                                      rpcPort);
        if(isRunning()) {
            stop();
        }
        try {
            reportContext = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = reportContext.createMarshaller();
            unmarshaller = reportContext.createUnmarshaller();
        } catch (JAXBException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
        PeerInfo serverInfo = new PeerInfo(getRpcHostname(),
                                           getRpcPort());
        executor = new ThreadPoolCallExecutor(threadPoolCore,
                                              threadPoolMax);
        DuplexTcpServerPipelineFactory serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);
        serverFactory.setRpcServerCallExecutor(executor);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(0,
                                              new RenamingThreadFactoryProxy("boss",
                                                                             Executors.defaultThreadFactory())),
                        new NioEventLoopGroup(0,
                                              new RenamingThreadFactoryProxy("worker",
                                                                             Executors.defaultThreadFactory())));
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(serverFactory);
        bootstrap.localAddress(serverInfo.getPort());
        bootstrap.option(ChannelOption.SO_SNDBUF,
                         sendBufferSize);
        bootstrap.option(ChannelOption.SO_RCVBUF,
                         receiveBufferSize);
        bootstrap.childOption(ChannelOption.SO_RCVBUF,
                              receiveBufferSize);
        bootstrap.childOption(ChannelOption.SO_SNDBUF,
                              sendBufferSize);
        bootstrap.option(ChannelOption.TCP_NODELAY,
                         noDelay);
        for(RpcServiceSpec<SessionClazz> serviceSpec : serviceSpecs) {
            serviceSpec.setRpcServerServices(this);
            BlockingService activeService = serviceSpec.generateService();
            serverFactory.getRpcServiceRegistry().registerService(activeService);
            Messages.SERVICE_STARTING.info(this,
                                           serviceSpec.getDescription());
        }
        channelToken = bootstrap.bind();
        while(!channelToken.isDone()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // TODO throw exception?
        running.set(channelToken.isSuccess());
        //RpcClientConnectionRegistry clientRegistry = new RpcClientConnectionRegistry();
        //serverFactory.registerConnectionEventListener(clientRegistry);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    @PreDestroy
    public synchronized void stop()
    {
        Messages.SERVER_STOPPING.info(this);
        try {
            try {
                if(executor != null) {
                    executor.shutdownNow();
                }
            } catch (Exception ignored) {}
            try {
                if(channelToken != null && channelToken.channel() != null) {
                    channelToken.channel().close();
                }
            } catch (Exception ignored) {}
            for(SessionId session : rpcSessions.keySet()) {
                try {
                    sessionManager.remove(session);
                } catch (Exception ignored) {}
            }
        } finally {
            rpcSessions.clear();
            channelToken = null;
            executor = null;
            reportContext = null;
            marshaller = null;
            unmarshaller = null;
            running.set(false);
        }
    }
    /**
     * Get the serviceSpecs value.
     *
     * @return a <code>List&lt;RpcServiceSpec&gt;</code> value
     */
    public List<RpcServiceSpec<SessionClazz>> getServiceSpecs()
    {
        return serviceSpecs;
    }
    /**
     * Sets the serviceSpecs value.
     *
     * @param inServiceSpecs a <code>List&lt;RpcServiceSpec&gt;</code> value
     */
    public void setServiceSpecs(List<RpcServiceSpec<SessionClazz>> inServiceSpecs)
    {
        serviceSpecs.clear();
        if(inServiceSpecs != null) {
            serviceSpecs.addAll(inServiceSpecs);
        }
    }
    /**
     * Get the sendBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getSendBufferSize()
    {
        return sendBufferSize;
    }
    /**
     * Sets the sendBufferSize value.
     *
     * @param inSendBufferSize an <code>int</code> value
     */
    public void setSendBufferSize(int inSendBufferSize)
    {
        sendBufferSize = inSendBufferSize;
    }
    /**
     * Get the receiveBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }
    /**
     * Sets the receiveBufferSize value.
     *
     * @param inReceiveBufferSize an <code>int</code> value
     */
    public void setReceiveBufferSize(int inReceiveBufferSize)
    {
        receiveBufferSize = inReceiveBufferSize;
    }
    /**
     * Get the noDelay value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getNoDelay()
    {
        return noDelay;
    }
    /**
     * Sets the noDelay value.
     *
     * @param inNoDelay a <code>boolean</code> value
     */
    public void setNoDelay(boolean inNoDelay)
    {
        noDelay = inNoDelay;
    }
    /**
     * Get the threadPoolCore value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolCore()
    {
        return threadPoolCore;
    }
    /**
     * Sets the threadPoolCore value.
     *
     * @param inThreadPoolCore an <code>int</code> value
     */
    public void setThreadPoolCore(int inThreadPoolCore)
    {
        threadPoolCore = inThreadPoolCore;
    }
    /**
     * Get the threadPoolMax value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolMax()
    {
        return threadPoolMax;
    }
    /**
     * Sets the threadPoolMax value.
     *
     * @param inThreadPoolMax an <code>int</code> value
     */
    public void setThreadPoolMax(int inThreadPoolMax)
    {
        threadPoolMax = inThreadPoolMax;
    }
    /**
     * Get the rpcHostname value.
     *
     * @return a <code>String</code> value
     */
    public String getRpcHostname()
    {
        return rpcHostname;
    }
    /**
     * Sets the rpcHostname value.
     *
     * @param inRpcHostname a <code>String</code> value
     */
    public void setHostname(String inRpcHostname)
    {
        rpcHostname = inRpcHostname;
    }
    /**
     * Get the rpcPort value.
     *
     * @return an <code>int</code> value
     */
    public int getRpcPort()
    {
        return rpcPort;
    }
    /**
     * Sets the rpcPort value.
     *
     * @param inRpcPort an <code>int</code> value
     */
    public void setPort(int inRpcPort)
    {
        rpcPort = inRpcPort;
    }
    /**
     * Get the authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager&lt;SessionClazz&gt;</code> value
     */
    public SessionManager<SessionClazz> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager&lt;SessionClazz&gt;</code> value
     */
    public void setSessionManager(SessionManager<SessionClazz> inSessionManager)
    {
        sessionManager = inSessionManager;
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
     * @see org.marketcetera.client.rpc.RpcServerServices#login(org.marketcetera.client.rpc.Credentials)
     */
    @Override
    public SessionId login(RpcCredentials inCredentials)
    {
        StatelessClientContext context = new StatelessClientContext();
        context.setAppId(new AppId(inCredentials.getAppId()));
        context.setClientId(new NodeId(inCredentials.getClientId()));
        context.setVersionId(new VersionId(inCredentials.getVersionId()));
        LocaleWrapper locale = new LocaleWrapper(inCredentials.getLocale());
        context.setLocale(locale);
        authenticator.shouldAllow(context,
                                  inCredentials.getUsername(),
                                  inCredentials.getPassword().toCharArray());
        SessionId sessionId = SessionId.generate();
        SessionHolder<SessionClazz> sessionHolder = new SessionHolder<SessionClazz>(inCredentials.getUsername(),
                                                                                    context);
        sessionManager.put(sessionId,
                           sessionHolder);
        rpcSessions.put(sessionId,
                        inCredentials.getUsername());
        return sessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerServices#logout(java.lang.String)
     */
    @Override
    public void logout(String inSessionIdValue)
    {
        SessionId session = new SessionId(inSessionIdValue);
        rpcSessions.remove(session);
        sessionManager.remove(session);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerServices#validateAndReturnSession(java.lang.String)
     */
    @Override
    public SessionHolder<SessionClazz> validateAndReturnSession(String inSessionIdValue)
    {
        SessionId session = new SessionId(inSessionIdValue);
        SessionHolder<SessionClazz> sessionInfo = sessionManager.get(session);
        if(sessionInfo == null) {
            throw new IllegalArgumentException("Invalid session: " + inSessionIdValue); // TODO
        }
        return sessionInfo;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerServices#marshall(java.lang.Object)
     */
    @Override
    public String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(marshaller) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerServices#unmarshall(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(unmarshaller) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * 
     */
    private SessionManager<SessionClazz> sessionManager;
    /**
     * 
     */
    private Authenticator authenticator;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * 
     */
    private RpcServerCallExecutor executor;
    /**
     * 
     */
    private ChannelFuture channelToken;
    /**
     * 
     */
    private final Map<SessionId,String> rpcSessions = Maps.newConcurrentMap();
    /**
     * 
     */
    private String rpcHostname;
    /**
     * 
     */
    private int rpcPort;
    /**
     * 
     */
    private int sendBufferSize = 1048576;
    /**
     * 
     */
    private int receiveBufferSize = 1048576;
    /**
     * 
     */
    private boolean noDelay = true;
    /**
     * 
     */
    private int threadPoolCore = 10;
    /**
     * 
     */
    private int threadPoolMax = 200;
    /**
     * 
     */
    private ContextClassProvider contextClassProvider;
    /**
     * 
     */
    private JAXBContext reportContext;
    /**
     * 
     */
    private Marshaller marshaller;
    /**
     * 
     */
    private Unmarshaller unmarshaller;
    /**
     * 
     */
    private final List<RpcServiceSpec<SessionClazz>> serviceSpecs = Lists.newArrayList();
}

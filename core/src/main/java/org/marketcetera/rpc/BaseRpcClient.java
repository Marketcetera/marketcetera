package org.marketcetera.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.BaseRpc;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.context.Lifecycle;

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
 * Provides common RPC client behaviors.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseRpcClient<BlockingInterfaceClazz>
        implements Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    @PostConstruct
    public void start()
    {
        try {
            startService();
            BaseRpc.LoginRequest.Builder requestBuilder =  BaseRpc.LoginRequest.newBuilder();
            requestBuilder.setAppId(getAppId().getValue())
                    .setVersionId(getVersionInfo().getVersionInfo())
                    .setClientId(NodeId.generate().getValue())
                    .setLocale(BaseRpc.Locale.newBuilder()
                               .setCountry(locale.getCountry())
                               .setLanguage(locale.getLanguage())
                               .setVariant(locale.getVariant()).build())
                               .setUsername(username)
                               .setPassword(password).build();
            BaseRpc.LoginResponse response = executeLogin(controller,
                                                          requestBuilder.build());
            if(response.getStatus().getFailed()) {
                sessionId = null;
                throw new RuntimeException(response.getStatus().getMessage());
            }
            sessionId = new SessionId(response.getSessionId());
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    @PreDestroy
    public void stop()
    {
        try {
            stopService();
        } catch (Exception ignored) {}
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        // TODO
        return true;
    }
    /**
     * Get the locale value.
     *
     * @return a <code>Locale</code> value
     */
    public Locale getLocale()
    {
        return locale;
    }
    /**
     * Sets the locale value.
     *
     * @param inLocale a <code>Locale</code> value
     */
    public void setLocale(Locale inLocale)
    {
        locale = inLocale;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the port value.
     *
     * @return a <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort a <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Validate the current session.
     */
    protected void validateSession()
    {
        Validate.notNull(sessionId,
                         "Not logged in");
    }
    /**
     * Get the clientService value.
     *
     * @return a <code>BlockingInterfaceClazz</code> value
     */
    protected BlockingInterfaceClazz getClientService()
    {
        return clientService;
    }
    /**
     * Get the controller value.
     *
     * @return a <code>RpcController</code> value
     */
    protected RpcController getController()
    {
        return controller;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionId</code> value
     */
    protected SessionId getSessionId()
    {
        return sessionId;
    }
    /**
     * 
     *
     *
     * @return
     */
    protected abstract AppId getAppId();
    /**
     * 
     *
     *
     * @return
     */
    protected abstract VersionInfo getVersionInfo();
    /**
     * 
     *
     *
     * @param inChannel
     * @return
     */
    protected abstract BlockingInterfaceClazz createClient(RpcClientChannel inChannel);
    /**
     * 
     *
     *
     * @param inController
     * @param inRequest
     * @return
     * @throws ServiceException
     */
    protected abstract BaseRpc.LoginResponse executeLogin(RpcController inController,
                                                          BaseRpc.LoginRequest inRequest)
            throws ServiceException;
    /**
     * 
     *
     *
     * @param inController
     * @param inRequest
     * @return
     * @throws ServiceException
     */
    protected abstract BaseRpc.LogoutResponse executeLogout(RpcController inController,
                                                            BaseRpc.LogoutRequest inRequest)
            throws ServiceException;
    /**
     * Start the remote service.
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
            CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
            logger.setLogRequestProto(false);
            logger.setLogResponseProto(false);
            clientFactory.setRpcLogger(logger);
            channel = clientFactory.peerWith(server,
                                             bootstrap);
            clientService = createClient(channel);
            controller = channel.newRpcController();
        }
    }
    /**
     * Stop the remote service.
     */
    private void stopService()
    {
        try(CloseableLock stopLock = CloseableLock.create(serviceLock.writeLock())) {
            stopLock.lock();
            if(sessionId != null) {
                try {
                    BaseRpc.LogoutRequest.Builder requestBuilder =  BaseRpc.LogoutRequest.newBuilder();
                    requestBuilder.setSessionId(sessionId.getValue());
                    BaseRpc.LogoutResponse response = executeLogout(controller,
                                                                    requestBuilder.build());
                    if(response.getStatus().getFailed()) {
                        SLF4JLoggerProxy.warn(this,
                                              response.getStatus().getMessage());
                    }
                } catch (ServiceException e) {
                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                        SLF4JLoggerProxy.warn(this,
                                              e);
                    } else {
                        SLF4JLoggerProxy.warn(this,
                                              ExceptionUtils.getRootCauseMessage(e));
                    }
                } finally {
                    sessionId = null;
                }
            }
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
        }
    }
    /**
     * client locale value
     */
    private Locale locale = Locale.getDefault();
    /**
     * session id of current session
     */
    private SessionId sessionId;
    /**
     * username to use to connect
     */
    private String username;
    /**
     * password user to connect
     */
    private String password;
    /**
     * hostname to which to connect
     */
    private String hostname;
    /**
     * port to which to connect
     */
    private int port;
    /**
     * provides access to RPC services
     */
    private BlockingInterfaceClazz clientService;
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
     * guards access to RPC service objects
     */
    private final ReadWriteLock serviceLock = new ReentrantReadWriteLock();
}

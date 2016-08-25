package org.marketcetera.rpc.client;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.context.Lifecycle;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides common RPC client behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractRpcClient<BlockingStubClazz extends AbstractStub<BlockingStubClazz>,
                                        AsyncStubClazz extends AbstractStub<AsyncStubClazz>>
        implements Lifecycle
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    @Override
    public void start()
    {
        stopped.set(false);
        startService();
        doLogin();
        heartbeat();
    }
    /**
     * Validate and stop the object.
     */
    @PreDestroy
    @Override
    public void stop()
    {
        stopped.set(true);
        doLogout();
        stopService();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return alive.get() && !stopped.get();
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Sets the host value.
     *
     * @param inHost a <code>String</code> value
     */
    public void setHost(String inHost)
    {
        host = inHost;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
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
     * Get the shutdownWait value.
     *
     * @return a <code>long</code> value
     */
    public long getShutdownWait()
    {
        return shutdownWait;
    }
    /**
     * Sets the shutdownWait value.
     *
     * @param inShutdownWait a <code>long</code> value
     */
    public void setShutdownWait(long inShutdownWait)
    {
        shutdownWait = inShutdownWait;
    }
    /**
     * Indicate that the server connection status has changed.
     *
     * @param inIsConnected a <code>boolean</code> value
     */
    protected void onStatusChange(boolean inIsConnected)
    {
    }
    /**
     * Indicate that a heartbeat has been received from the server.
     */
    protected void onHeartbeat()
    {
    }
    /**
     * 
     *
     *
     * @return
     */
    protected BlockingStubClazz getBlockingStub()
    {
        return blockingStub;
    }
    /**
     * 
     *
     *
     * @return
     */
    protected AsyncStubClazz getAsyncStub()
    {
        return asyncStub;
    }
    /**
     * 
     *
     *
     * @param inChannel
     * @return
     */
    protected abstract BlockingStubClazz getBlockingStub(Channel inChannel);
    /**
     * 
     *
     *
     * @param inChannel
     * @return
     */
    protected abstract AsyncStubClazz getAsyncStub(Channel inChannel);
    /**
     * Execute the login call using the client.
     *
     * @param inRequest a <code>BaseRpc.LoginRequest</code> value
     * @return a <code>BaseRpc.LoginResponse</code> value
     */
    protected abstract BaseRpc.LoginResponse executeLogin(BaseRpc.LoginRequest inRequest);
    /**
     * Execute the logout call using the client.
     *
     * @param inRequest a <code>BaseRpc.LogoutRequest</code> value
     * @return a <code>BaseRpc.LogoutResponse</code> value
     */
    protected abstract BaseRpc.LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest);
    /**
     * Execute the heartbeat call using the client.
     *
     * @param inRequest a <code>BaseRpc.HeartbeatRequest</code> value
     * @return an <code>Iterator&lt;BaseRpc.HeartbeatResponse&gt;</code> value
     */
    protected abstract void executeHeartbeat(BaseRpc.HeartbeatRequest inRequest,
                                             StreamObserver<BaseRpc.HeartbeatResponse> inObserver);
    /**
     * Get the app id value of the client.
     *
     * @return an <code>AppId</code> value
     */
    protected abstract AppId getAppId();
    /**
     * Get the version info of the client.
     *
     * @return a <code>VersionInfo</code> value
     */
    protected abstract VersionInfo getVersionInfo();
    /**
     * 
     *
     *
     */
    private void startService()
    {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host,
                                                                                   port).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = getBlockingStub(channel);
        asyncStub = getAsyncStub(channel);
    }
    /**
     * 
     *
     *
     */
    private void stopService()
    {
        // TODO stop hearbeat?
        if(channel != null) {
            try {
                channel.shutdown().awaitTermination(shutdownWait,
                                                    TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            channel = null;
        }
    }
    /**
     * Perform and verify heartbeat request.
     */
    protected void heartbeat()
    {
        if(sessionId == null) {
            return;
        }
        if(heartbeatExecutor == null) {
            heartbeatExecutor = new HeartbeatExecutor();
        }
        SLF4JLoggerProxy.debug(this,
                               "{} sending heartbeat request: {}",
                               getAppId(),
                               sessionId);
        executeHeartbeat(BaseRpc.HeartbeatRequest.newBuilder().setSessionId(sessionId.getValue()).setInterval(heartbeatInterval).build(),
                         heartbeatExecutor);
    }
    /**
     * Perform the logout and adjust the status.
     */
    private void doLogout()
    {
        if(alive.get()) {
            try {
                BaseRpc.LogoutRequest.Builder requestBuilder =  BaseRpc.LogoutRequest.newBuilder();
                requestBuilder.setSessionId(sessionId.getValue());
                BaseRpc.LogoutResponse response = executeLogout(requestBuilder.build());
                SLF4JLoggerProxy.trace(this,
                                       "{}/{} received logout response {}",
                                       getAppId(),
                                       sessionId,
                                       response);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
                if(e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                throw new RuntimeException(e);
            } finally {
                notifyStatusChange(false);
                sessionId = null;
            }
        }
    }
    /**
     * Perform the login and adjust the status if successful.
     */
    private void doLogin()
    {
        SLF4JLoggerProxy.debug(this,
                               "{} initiating login to {}/{}",
                               getAppId(),
                               host,
                               port);
        alive.set(false);
        BaseRpc.LoginRequest.Builder requestBuilder =  BaseRpc.LoginRequest.newBuilder();
        requestBuilder.setAppId(getAppId().getValue())
            .setVersionId(getVersionInfo().getVersionInfo())
            .setClientId(NodeId.generate().getValue())
            .setLocale(BaseRpc.Locale.newBuilder().setCountry(locale.getCountry())
                   .setLanguage(locale.getLanguage())
                   .setVariant(locale.getVariant()).build())
            .setUsername(username)
            .setPassword(password).build();
        try {
            BaseRpc.LoginResponse response = executeLogin(requestBuilder.build());
            sessionId = new SessionId(response.getSessionId());
            alive.set(true);
            notifyStatusChange(true);
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      ExceptionUtils.getRootCauseMessage(e));
            } else {
                SLF4JLoggerProxy.warn(this,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
            sessionId = null;
            notifyStatusChange(false);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /**
     * Notify on server status.
     *
     * @param inIsConnected a <code>boolean</code> value
     */
    private void notifyStatusChange(boolean inIsConnected)
    {
        if(inIsConnected != lastStatus) {
            try {
                SLF4JLoggerProxy.debug(this,
                                       "{}/{} status change, connected: {}",
                                       getAppId(),
                                       sessionId,
                                       inIsConnected);
                onStatusChange(inIsConnected);
            } catch (Exception e) {
                String message = ExceptionUtils.getRootCauseMessage(e);
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          message);
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          message);
                }
            } finally {
                lastStatus = inIsConnected;
            }
        }
    }
    /**
     * 
     *
     *
     */
    private void reconnect()
    {
        if(stopped.get()) {
            return;
        }
        stopService();
        while(!alive.get()) {
            try {
                SLF4JLoggerProxy.info(this,
                                      "{} trying to reconnect",
                                      getAppId());
                startService();
                doLogin();
                heartbeat();
            } catch (Exception e) {
                String message = ExceptionUtils.getRootCauseMessage(e);
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          message);
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          message);
                }
                if(e instanceof StatusRuntimeException) {
                    try {
                        Thread.sleep(heartbeatInterval);
                    } catch (InterruptedException e1) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }
    /**
     * Tracks and responds to heartbeat messages from the server.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class HeartbeatExecutor
            implements StreamObserver<BaseRpc.HeartbeatResponse>
    {
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
         */
        @Override
        public void onNext(HeartbeatResponse inValue)
        {
            SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                   "{}/{} received heartbeat: {}",
                                   getAppId(),
                                   sessionId,
                                   inValue);
            try {
                // TODO if not received within a certain interval, you know we're disconnected
                onHeartbeat();
            } catch (Exception e) {
                String message = ExceptionUtils.getRootCauseMessage(e);
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                          e,
                                          message);
                } else {
                    SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                          message);
                }
            }
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inT)
        {
            alive.set(false);
            notifyStatusChange(false);
            if(stopped.get()) {
                return;
            }
            SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                  inT,
                                  "{} received a heartbeat error",
                                  getAppId());
            reconnect();
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onCompleted()
         */
        @Override
        public void onCompleted()
        {
            SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                   "{} heartbeat completed",
                                   getAppId());
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
     * indicates if the client is currently started and connected
     */
    private final AtomicBoolean alive = new AtomicBoolean(false);
    /**
     * indicates if the client has been stopped
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    /**
     * 
     */
    private long shutdownWait = 5000;
    /**
     * username to use to connect
     */
    private String username;
    /**
     * password user to connect
     */
    private String password;
    /**
     * 
     */
    private String host;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private ManagedChannel channel;
    /**
     * 
     */
    private BlockingStubClazz blockingStub;
    /**
     * 
     */
    private AsyncStubClazz asyncStub;
    /**
     * interval in ms at which to execute heartbeat/health check calls
     */
    private long heartbeatInterval = 1000;
    /**
     * tracks the last notified status value
     */
    private volatile boolean lastStatus;
    /**
     * 
     */
    private HeartbeatExecutor heartbeatExecutor;
}

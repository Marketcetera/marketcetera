package org.marketcetera.rpc.client;

import java.util.Iterator;
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
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        stopped.set(false);
        startService();
        doLogin();
        heartbeat();
    }
    private void doLogout()
    {
        
    }
    /**
     * Validate and stop the object.
     */
    @PreDestroy
    public void stop()
    {
        stopped.set(true);
        stopService();
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
        if(heartbeatExecutor != null) {
            try {
                heartbeatExecutor.stop();
            } catch (Exception ignored) {}
            heartbeatExecutor = null;
        }
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
        if(heartbeatExecutor != null) {
            heartbeatExecutor.stop();
        }
        heartbeatExecutor = new HeartbeatExecutor();
        heartbeatExecutor.start();
    }
    private class HeartbeatExecutor
            implements Runnable, StreamObserver<BaseRpc.HeartbeatResponse>
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                       "{} sending heartbeat request: {}",
                                       getAppId(),
                                       sessionId);
                executeHeartbeat(BaseRpc.HeartbeatRequest.newBuilder().setSessionId(sessionId.getValue()).build(),
                                 this);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                      e);
            } finally {
                SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                       "{} no more heartbeats",
                                       getAppId());
            }
            // TODO auto-reconnect
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
         */
        @Override
        public void onNext(HeartbeatResponse inValue)
        {
            SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                   "{} received {}",
                                   getAppId(),
                                   inValue);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inT)
        {
            SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                  inT,
                                  "{} received a heartbeat error",
                                  getAppId());
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
        /**
         * 
         *
         *
         */
        private void start()
        {
            thread = new Thread(this,
                                getAppId() + " Heartbeat Executor");
            thread.start();
        }
        /**
         * 
         *
         *
         */
        private void stop()
        {
            if(thread != null) {
                try {
                    thread.interrupt();
                } catch (Exception ignored) {}
                thread = null;
            }
        }
        /**
         * 
         */
        private Thread thread;
    }
    /**
     * Perform the login and adjust the status if successful.
     */
    private void doLogin()
    {
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
        } catch (StatusRuntimeException e) {
            sessionId = null;
            notifyStatusChange(false);
            throw new RuntimeException(e.getStatus().toString());
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
            }
            lastStatus = inIsConnected;
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

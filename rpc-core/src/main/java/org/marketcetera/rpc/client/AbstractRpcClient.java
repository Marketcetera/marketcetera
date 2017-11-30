package org.marketcetera.rpc.client;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

/* $License$ */

/**
 * Provides common RPC client behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractRpcClient<BlockingStubClazz extends AbstractStub<BlockingStubClazz>,
                                        AsyncStubClazz extends AbstractStub<AsyncStubClazz>,
                                        ParameterClazz extends RpcClientParameters>
        implements RpcClient
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        stopped.set(false);
        startService();
        doLogin();
        scheduleHeartbeat();
    }
    /**
     * Validate and stop the object.
     */
    @PreDestroy
    public void stop()
            throws Exception
    {
        stopped.set(true);
        cancelHeartbeat();
        doLogout();
        stopService();
    }
    /**
     * Indicate if the service is running.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        return alive.get() && !stopped.get();
    }
    /**
     * Create a new AbstractRpcClient instance.
     *
     * @param inParameters
     */
    protected AbstractRpcClient(ParameterClazz inParameters)
    {
        parameters = inParameters;
    }
    /**
     * Get the session id of the current session.
     *
     * @return a <code>SessionId</code> value or <code>null</code>
     */
    protected SessionId getSessionId()
    {
        return sessionId;
    }
    /**
     * Execute the given call with session awareness and error handling.
     *
     * @param inRequest a <code>Callable&lt;ResponseClazz&gt;</code> value
     * @return a <code>ResponseClazz</code> value
     */
    protected <ResponseClazz> ResponseClazz executeCall(Callable<ResponseClazz> inRequest)
    {
        validateSession();
        try {
            return inRequest.call();
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
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
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
     * Get the blocking stub value.
     *
     * @return a <code>BlockingStubClazz</code> value
     */
    protected BlockingStubClazz getBlockingStub()
    {
        return blockingStub;
    }
    /**
     * Get the async stub value.
     *
     * @return an <code>AsyncStubClazz</code> value
     */
    protected AsyncStubClazz getAsyncStub()
    {
        return asyncStub;
    }
    /**
     * Get the blocking stub for the given channel.
     *
     * @param inChannel a <code>Channel</code> value
     * @return a <code>BlockingStubClazz</code> value
     */
    protected abstract BlockingStubClazz getBlockingStub(Channel inChannel);
    /**
     * Get the async stub for the given channel.
     *
     * @param inChannel a <code>Channel</code> value
     * @return an <code>AsyncStubClazz</code> value
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
     * @return a <code>BaseRpc.HeartbeatResponse</code> value
     */
    protected abstract BaseRpc.HeartbeatResponse executeHeartbeat(BaseRpc.HeartbeatRequest inRequest);
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
     * Validate the current session.
     */
    private void validateSession()
    {
        Validate.isTrue(alive.get(),
                        "Not connected");
        Validate.notNull(sessionId,
                         "Not logged in");
    }
    /**
     * Start the client service.
     */
    private void startService()
    {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(parameters.getHostname(),
                                                                                   parameters.getPort()).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = getBlockingStub(channel);
        asyncStub = getAsyncStub(channel);
    }
    /**
     * Stop the client service.
     */
    private void stopService()
    {
        if(channel != null) {
            try {
                channel.shutdown().awaitTermination(parameters.getHeartbeatInterval(),
                                                    TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
            channel = null;
        }
    }
    /**
     * Establish heartbeat request.
     */
    protected void scheduleHeartbeat()
    {
        if(sessionId == null) {
            return;
        }
        cancelHeartbeat();
        heartbeatToken = heartbeatExecutorService.scheduleAtFixedRate(new HeartbeatTask(),
                                                                      parameters.getHeartbeatInterval(),
                                                                      parameters.getHeartbeatInterval(),
                                                                      TimeUnit.MILLISECONDS);
        SLF4JLoggerProxy.debug(this,
                               "{} scheduling heartbeat request at {}ms intervals",
                               sessionId,
                               parameters.getHeartbeatInterval());
    }
    /**
     * Cancel the current heartbeat request, if necessary.
     */
    protected void cancelHeartbeat()
    {
        if(heartbeatToken != null) {
            heartbeatToken.cancel(false);
        }
        heartbeatToken = null;
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
                alive.set(false);
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
                               parameters.getHeartbeatInterval(),
                               parameters.getPort());
        alive.set(false);
        BaseRpc.LoginRequest.Builder requestBuilder =  BaseRpc.LoginRequest.newBuilder();
        requestBuilder.setAppId(getAppId().getValue())
            .setVersionId(getVersionInfo().getVersionInfo())
            .setClientId(NodeId.generate().getValue())
            .setLocale(BaseRpc.Locale.newBuilder().setCountry(locale.getCountry())
            .setLanguage(locale.getLanguage())
            .setVariant(locale.getVariant()).build())
            .setUsername(parameters.getUsername())
            .setPassword(parameters.getPassword()).build();
        try {
            BaseRpc.LoginResponse response = executeLogin(requestBuilder.build());
            sessionId = new SessionId(response.getSessionId());
            alive.set(true);
            notifyStatusChange(true);
        } catch (Exception e) {
            alive.set(false);
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
     * Reconnect the client service.
     */
    private void reconnect()
    {
        if(stopped.get()) {
            return;
        }
        while(!alive.get()) {
            try {
                SLF4JLoggerProxy.info(this,
                                      "{} trying to reconnect",
                                      getAppId());
                stopService();
                startService();
                doLogin();
                scheduleHeartbeat();
            } catch (Exception e) {
                alive.set(false);
                String message = ExceptionUtils.getRootCauseMessage(e);
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          message);
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          message);
                }
                try {
                    Thread.sleep(parameters.getHeartbeatInterval());
                } catch (InterruptedException e1) {
                    break;
                }
            }
        }
    }
    /**
     * Executes a heartbeat request and processes the response.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class HeartbeatTask
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                BaseRpc.HeartbeatRequest request = BaseRpc.HeartbeatRequest.newBuilder().setSessionId(sessionId.getValue()).build();
                SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                       "{} sending heartbeat request: {}",
                                       getAppId(),
                                       request);
                BaseRpc.HeartbeatResponse response = executeHeartbeat(request);
                SLF4JLoggerProxy.trace(AbstractRpcClient.this,
                                       "{} received heartbeat response: {}",
                                       getAppId(),
                                       response);
            } catch (Exception e) {
                cancelHeartbeat();
                alive.set(false);
                notifyStatusChange(false);
                if(stopped.get()) {
                    return;
                }
                SLF4JLoggerProxy.warn(AbstractRpcClient.this,
                                      e,
                                      "{} received heartbeat error",
                                      getAppId());
                reconnect();
            }
        }
    }
    /**
     * token holding current heartbeat scheduled job, if any, may be <code>null</code>
     */
    private ScheduledFuture<?> heartbeatToken;
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
     * RPC channel value
     */
    private ManagedChannel channel;
    /**
     * blocking stub value
     */
    private BlockingStubClazz blockingStub;
    /**
     * async stub value
     */
    private AsyncStubClazz asyncStub;
    /**
     * tracks the last notified status value
     */
    private volatile boolean lastStatus;
    /**
     * parameters used to start the client
     */
    private final ParameterClazz parameters;
    /**
     * common heartbeat executor for <em>all</em> clients, do not touch it when this client stops
     */
    private static final ScheduledExecutorService heartbeatExecutorService = Executors.newScheduledThreadPool(1);
}

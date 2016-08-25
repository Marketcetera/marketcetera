package org.marketcetera.rpc.server;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides common behavior for RPC service implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractRpcService<SessionClazz,ServiceClazz extends BindableService>
        implements BindableService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(authenticator,
                         "Cannot bind " + getServiceDescription() + ": authenticator required");
        Validate.notNull(sessionManager,
                         "Cannot bind " + getServiceDescription() + ": session manager required");
    }
    /* (non-Javadoc)
     * @see io.grpc.BindableService#bindService()
     */
    @Override
    public ServerServiceDefinition bindService()
    {
        return getService().bindService();
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
     * Get the defaultHearbeatExecutorPoolSize value.
     *
     * @return a <code>long</code> value
     */
    public long getDefaultHearbeatExecutorPoolSize()
    {
        return defaultHearbeatExecutorPoolSize;
    }
    /**
     * Sets the defaultHearbeatExecutorPoolSize value.
     *
     * @param inDefaultHearbeatExecutorPoolSize a <code>long</code> value
     */
    public void setDefaultHearbeatExecutorPoolSize(long inDefaultHearbeatExecutorPoolSize)
    {
        defaultHearbeatExecutorPoolSize = inDefaultHearbeatExecutorPoolSize;
    }
    /**
     * Get the defaultHeartbeatInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getDefaultHeartbeatInterval()
    {
        return defaultHeartbeatInterval;
    }
    /**
     * Sets the defaultHeartbeatInterval value.
     *
     * @param inDefaultHeartbeatInterval a <code>long</code> value
     */
    public void setDefaultHeartbeatInterval(long inDefaultHeartbeatInterval)
    {
        defaultHeartbeatInterval = inDefaultHeartbeatInterval;
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager<SessionClazz></code> value
     */
    public SessionManager<SessionClazz> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager<SessionClazz></code> value
     */
    public void setSessionManager(SessionManager<SessionClazz> inSessionManager)
    {
        sessionManager = inSessionManager;
    }
    /**
     * Get the description of the service.
     *
     * @return a <code>String<code> value
     */
    protected abstract String getServiceDescription();
    /**
     * Get the service instance.
     *
     * @return a <code>ServiceClazz</code> value
     */
    protected abstract ServiceClazz getService();
    /**
     * 
     *
     *
     * @param inRequest
     * @param inResponseObserver
     */
    protected void doLogin(BaseRpc.LoginRequest inRequest,
                           StreamObserver<BaseRpc.LoginResponse> inResponseObserver)
    {
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} received login request for {}/{}",
                                   getServiceDescription(),
                                   inRequest.getAppId(),
                                   inRequest.getUsername());
            BaseRpc.LoginResponse.Builder responseBuilder = BaseRpc.LoginResponse.newBuilder();
            String username = inRequest.getUsername();
            String password = inRequest.getPassword();
            StatelessClientContext context = new StatelessClientContext();
            context.setAppId(new AppId(inRequest.getAppId()));
            context.setClientId(new NodeId(inRequest.getClientId()));
            context.setVersionId(new VersionId(inRequest.getVersionId()));
            LocaleWrapper locale = new LocaleWrapper(new Locale(inRequest.getLocale().getLanguage(),
                                                                inRequest.getLocale().getCountry(),
                                                                inRequest.getLocale().getVariant()));
            context.setLocale(locale);
            if(authenticator.shouldAllow(context,
                                         username,
                                         password.toCharArray())) {
                SessionId sessionId = SessionId.generate();
                SessionHolder<SessionClazz> sessionHolder = new SessionHolder<SessionClazz>(username,
                                                                                            context);
                sessionManager.put(sessionId,
                                   sessionHolder);
                responseBuilder.setSessionId(sessionId.getValue());
                allSessionMetaData.put(sessionId,
                                       new SessionMetaData(sessionId,
                                                           username,
                                                           context));
                BaseRpc.LoginResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(this,
                                       "Returning login response: {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } else {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription(inRequest.getUsername() + " is not a valid user or the password was invalid"));
            }
        } catch (StatusRuntimeException e) {
            throw e;
        } catch (Exception e) {
            String message = "Unexpected error occurred during login: " + ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            throw new StatusRuntimeException(Status.INTERNAL.withDescription(message).withCause(e));
        }
    }
    /**
     * 
     *
     *
     * @param inRequest
     * @param inResponseObserver
     */
    protected void doLogout(BaseRpc.LogoutRequest inRequest,
                            StreamObserver<BaseRpc.LogoutResponse> inResponseObserver)
    {
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} received logout request for {}",
                                   getServiceDescription(),
                                   inRequest.getSessionId());
            String rawRequestSessionId = StringUtils.trimToNull(inRequest.getSessionId());
            if(rawRequestSessionId != null) {
                SessionId sessionId = new SessionId(rawRequestSessionId);
                SessionMetaData sessionMetaData = allSessionMetaData.remove(sessionId);
                sessionMetaData.heartbeatToken.cancel(true);
                sessionMetaData.heartbeatToken = null;
                SLF4JLoggerProxy.trace(this,
                                       "{} logged out",
                                       sessionMetaData);
            }
            BaseRpc.LogoutResponse.Builder responseBuilder = BaseRpc.LogoutResponse.newBuilder();
            BaseRpc.LogoutResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(this,
                                   "Returning logout response: {}",
                                   response);
            inResponseObserver.onNext(response);
            inResponseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            throw e;
        } catch (Exception e) {
            String message = "Unexpected error occurred during logout: " + ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            throw new StatusRuntimeException(Status.INTERNAL.withDescription(message).withCause(e));
        }
    }
    /**
     * 
     *
     *
     * @param inRequest
     * @param inResponseObserver
     */
    protected void doHeartbeat(final BaseRpc.HeartbeatRequest inRequest,
                               final StreamObserver<BaseRpc.HeartbeatResponse> inResponseObserver)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received hearbeat request: {}", //$NON-NLS-1$
                               getServiceDescription(),
                               inRequest.getSessionId());
        String rawRequestSessionId = StringUtils.trimToNull(inRequest.getSessionId());
        if(rawRequestSessionId == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED);
        }
        SessionId requestSessionId = new SessionId(rawRequestSessionId);
        final SessionMetaData sessionMetaData = allSessionMetaData.get(requestSessionId);
        if(sessionMetaData == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED);
        }
        BaseRpc.HeartbeatResponse.Builder responseBuilder = BaseRpc.HeartbeatResponse.newBuilder();
        responseBuilder.setSessionId(inRequest.getSessionId());
        long heartbeatInterval = inRequest.getInterval();
        if(heartbeatInterval == 0) {
            heartbeatInterval = defaultHeartbeatInterval;
        }
        sessionMetaData.heartbeatToken = heartbeatExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    responseBuilder.setTimestamp(System.currentTimeMillis());
                    BaseRpc.HeartbeatResponse response = responseBuilder.build();
                    inResponseObserver.onNext(response);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(AbstractRpcService.this,
                                          e,
                                          "Ending heartbeats to {}/{}",
                                          sessionMetaData.context.getAppId(),
                                          sessionMetaData.sessionId);
                    // TODO which of these, or neither?
//                    inResponseObserver.onError(new StatusRuntimeException(Status.CANCELLED));
                    inResponseObserver.onCompleted();
                }
            }
        }, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
    }
    /**
     * 
     *
     *
     * @param inSessionIdValue
     * @return
     */
    protected SessionHolder<SessionClazz> validateAndReturnSession(String inSessionIdValue)
    {
        SessionId session = new SessionId(inSessionIdValue);
        SessionHolder<SessionClazz> sessionInfo = sessionManager.get(session);
        if(sessionInfo == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid session: " + inSessionIdValue)); // TODO
        }
        return sessionInfo;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class SessionMetaData
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("SessionMetaData [username=").append(username).append(", sessionId=").append(sessionId)
                    .append(", context=").append(context).append(", created=").append(created).append("]");
            return builder.toString();
        }
        private SessionMetaData(SessionId inSessionId,
                                String inUsername,
                                StatelessClientContext inContext)
        {
            sessionId = inSessionId;
            username = inUsername;
            context = inContext;
        }
        private final String username;
        private final SessionId sessionId;
        private final StatelessClientContext context;
        private final DateTime created = new DateTime();
        private ScheduledFuture<?> heartbeatToken;
    }
    /**
     * 
     */
    private final Map<SessionId,SessionMetaData> allSessionMetaData = new ConcurrentHashMap<>();
    /**
     * manages sessions
     */
    private SessionManager<SessionClazz> sessionManager;
    /**
     * provides authentication services
     */
    private Authenticator authenticator;
    /**
     * 
     */
    private long defaultHearbeatExecutorPoolSize = 10;
    /**
     * 
     */
    private long defaultHeartbeatInterval = 1000;
    /**
     * 
     */
    private ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(10);
}

package org.marketcetera.rpc.server;

import java.util.Locale;

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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
     * 
     * @throws Exception if an unexpected error occurs
     */
    @PostConstruct
    public void start()
            throws Exception
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
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              getServiceDescription());
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
     * Execute the given login request.
     *
     * @param inRequest a <code>BaseRpc.LoginRequest</code> value
     * @param inResponseObserver a <code>StreamObserver&lt;BaseRpc.LoginResponse&gt;</code> value
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
        } catch (Exception e) {
            handleError(e,
                        inResponseObserver);
        }
    }
    /**
     * Execute the given logout request.
     *
     * @param inRequest a <code>BaseRpc.LogoutRequest</code> value
     * @param inResponseObserver a <code>StreamObserver&lt;BaseRpc.LogoutResponse&gt;</code> value
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
                allSessionMetaData.invalidate(sessionId);
            }
            BaseRpc.LogoutResponse.Builder responseBuilder = BaseRpc.LogoutResponse.newBuilder();
            BaseRpc.LogoutResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(this,
                                   "Returning logout response: {}",
                                   response);
            inResponseObserver.onNext(response);
            inResponseObserver.onCompleted();
        } catch (Exception e) {
            handleError(e,
                        inResponseObserver);
        }
    }
    /**
     * Execute the heartbeat request.
     *
     * @param inRequest a <code>BaseRpc.HeartbeatRequest</code> value
     * @param inResponseObserver a <code>StreamObserver&lt;BaseRpc.HeartbeatResponse&gt;</code> value
     */
    protected void doHeartbeat(final BaseRpc.HeartbeatRequest inRequest,
                               final StreamObserver<BaseRpc.HeartbeatResponse> inResponseObserver)
    {
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} received hearbeat request: {}", //$NON-NLS-1$
                                   getServiceDescription(),
                                   inRequest.getSessionId());
            String rawRequestSessionId = StringUtils.trimToNull(inRequest.getSessionId());
            if(rawRequestSessionId == null) {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED);
            }
            SessionId requestSessionId = new SessionId(rawRequestSessionId);
            final SessionMetaData sessionMetaData = allSessionMetaData.getIfPresent(requestSessionId);
            if(sessionMetaData == null) {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED);
            }
            BaseRpc.HeartbeatResponse.Builder responseBuilder = BaseRpc.HeartbeatResponse.newBuilder();
            responseBuilder.setSessionId(inRequest.getSessionId());
            responseBuilder.setTimestamp(System.currentTimeMillis());
            inResponseObserver.onNext(responseBuilder.build());
            inResponseObserver.onCompleted();
        } catch (Exception e) {
            handleError(e,
                        inResponseObserver);
        }
    }
    /**
     * Validates the given session value and returns the session meta information if successful.
     *
     * @param inSessionIdValue a <code>String</code> value
     * @return a <code>SessionHolder&lt;SessionClazz&gt;</code> value
     * @throws StatusRuntimeException if the session is not valud
     */
    protected SessionHolder<SessionClazz> validateAndReturnSession(String inSessionIdValue)
    {
        String rawRequestSessionId = StringUtils.trimToNull(inSessionIdValue);
        if(rawRequestSessionId == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED);
        }
        SessionId requestSessionId = new SessionId(rawRequestSessionId);
        final SessionMetaData sessionMetaData = allSessionMetaData.getIfPresent(requestSessionId);
        if(sessionMetaData == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED);
        }
        SessionId session = new SessionId(inSessionIdValue);
        SessionHolder<SessionClazz> sessionInfo = sessionManager.get(session);
        if(sessionInfo == null) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid session: " + inSessionIdValue)); // TODO
        }
        return sessionInfo;
    }
    /**
     * Handle an exception thrown during an RPC call.
     *
     * @param inException a <code>Throwable</code> value
     * @param inResponseObserver a <code>StreamObserver&lt;Clazz&gt;</code> value
     */
    protected <Clazz> void handleError(Throwable inException,
                                       StreamObserver<Clazz> inResponseObserver)
    {
        StatusRuntimeException sre;
        if(inException instanceof StatusRuntimeException) {
            sre = (StatusRuntimeException)inException;
        } else {
            sre = new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(inException).withDescription(ExceptionUtils.getRootCauseMessage(inException)));
        }
        inResponseObserver.onError(sre);
        throw sre;
    }
    /**
     * Holds meta data for a session.
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
        /**
         * Create a new SessionMetaData instance.
         *
         * @param inSessionId a <code>SessionId</code> value
         * @param inUsername a <code>String</code> value
         * @param inContext a <code>StatelessClientContext</code> value
         */
        private SessionMetaData(SessionId inSessionId,
                                String inUsername,
                                StatelessClientContext inContext)
        {
            sessionId = inSessionId;
            username = inUsername;
            context = inContext;
        }
        /**
         * username value
         */
        private final String username;
        /**
         * session id value
         */
        private final SessionId sessionId;
        /**
         * context value
         */
        private final StatelessClientContext context;
        /**
         * created value
         */
        private final DateTime created = new DateTime();
    }
    /**
     * holds session meta data by session id
     */
    private final Cache<SessionId,SessionMetaData> allSessionMetaData = CacheBuilder.newBuilder().build();
    /**
     * manages sessions
     */
    private SessionManager<SessionClazz> sessionManager;
    /**
     * provides authentication services
     */
    private Authenticator authenticator;
}

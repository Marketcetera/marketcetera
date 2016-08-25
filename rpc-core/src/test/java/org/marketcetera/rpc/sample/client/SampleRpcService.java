package org.marketcetera.rpc.sample.client;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.sample.SampleRpcServiceGrpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
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
 * Provides a sample {@link BindableService} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SampleRpcService
        extends SampleRpcServiceGrpc.SampleRpcServiceImplBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#bindService()
     */
    @Override
    public ServerServiceDefinition bindService()
    {
        Validate.notNull(authenticator,
                         "Cannot bind " + getServiceDescription() + ": authenticator required");
        return super.bindService();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void login(LoginRequest inRequest,
                      StreamObserver<LoginResponse> inResponseObserver)
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
                SessionId sessionId = new SessionId(UUID.randomUUID().toString());
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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void logout(LogoutRequest inRequest,
                       StreamObserver<LogoutResponse> inResponseObserver)
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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void heartbeat(final HeartbeatRequest inRequest,
                          final StreamObserver<HeartbeatResponse> inResponseObserver)
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
                    SLF4JLoggerProxy.warn(SampleRpcService.this,
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
     * 
     *
     *
     * @return
     */
    protected String getServiceDescription()
    {
        return description;
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
    private final Map<SessionId,SessionMetaData> allSessionMetaData = new ConcurrentHashMap<>();
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
    /**
     * provides authentication services
     */
    private Authenticator authenticator;
    private final static String description = "Sample RPC Service";
}
//private static class SampleService
//extends SampleRpcServiceGrpc.SampleRpcServiceImplBase
//{
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#login(org.marketcetera.util.rpc.SampleRpc.LoginRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void login(LoginRequest inRequest,
//            StreamObserver<LoginResponse> inResponseObserver)
//{
//System.out.println("COLIN: login invoked: " + inRequest);
//SampleRpc.LoginResponse.Builder responseBuilder = SampleRpc.LoginResponse.newBuilder();
//SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//statusBuilder.setFailed(false);
//try {
////  SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
////                                                                inRequest.getPassword(),
////                                                                inRequest.getAppId(),
////                                                                inRequest.getClientId(),
////                                                                inRequest.getVersionId(),
////                                                                new Locale(inRequest.getLocale().getLanguage(),
////                                                                           inRequest.getLocale().getCountry(),
////                                                                           inRequest.getLocale().getVariant())));
//  String sessionId = UUID.randomUUID().toString();
//  statusBuilder.setSessionId(sessionId);
//  responseBuilder.setSessionId(sessionId);
//} catch (Exception e) {
////  String message = ExceptionUtils.getRootCauseMessage(e);
////  if(SLF4JLoggerProxy.isDebugEnabled(this)) {
////      SLF4JLoggerProxy.warn(this,
////                            e,
////                            message);
////  } else {
////      SLF4JLoggerProxy.warn(this,
////                            message);
////  }
//  String message = e.getMessage();
//  statusBuilder.setFailed(true);
//  statusBuilder.setMessage(message);
//  responseBuilder.setSessionId("null"); //$NON-NLS-1$
//}
//responseBuilder.setStatus(statusBuilder.build());
//SampleRpc.LoginResponse response = responseBuilder.build();
////SLF4JLoggerProxy.trace(this,
////                     "Returning {} for {}",
////                     response,
////                     inRequest.getUsername());
//inResponseObserver.onNext(response);
//inResponseObserver.onCompleted();
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#logout(org.marketcetera.util.rpc.SampleRpc.LogoutRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void logout(LogoutRequest inRequest,
//             StreamObserver<LogoutResponse> inResponseObserver)
//{
//throw new UnsupportedOperationException(); // TODO
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#heartbeat(org.marketcetera.util.rpc.SampleRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void heartbeat(HeartbeatRequest inRequest,
//                StreamObserver<HeartbeatResponse> inResponseObserver)
//{
//throw new UnsupportedOperationException(); // TODO
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#newHeartbeat(org.marketcetera.util.rpc.SampleRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void newHeartbeat(HeartbeatRequest inRequest,
//                   StreamObserver<HeartbeatResponse> inResponseObserver)
//{
///*
//SLF4JLoggerProxy.trace(this,
//                 "{} received hearbeat request: {}", //$NON-NLS-1$
//                 getDescription(),
//                 inRequest.getId());
//SampleRpc.HeartbeatResponse.Builder responseBuilder = SampleRpc.HeartbeatResponse.newBuilder();
//SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//statusBuilder.setFailed(false);
//try {
//responseBuilder.setId(inRequest.getId());
//} catch (Exception e) {
//String message = ExceptionUtils.getRootCauseMessage(e);
//if(SLF4JLoggerProxy.isDebugEnabled(this)) {
//  SLF4JLoggerProxy.warn(this,
//                        e,
//                        message);
//} else {
//  SLF4JLoggerProxy.warn(this,
//                        message);
//}
//statusBuilder.setFailed(true);
//statusBuilder.setMessage(message);
//}
//responseBuilder.setStatus(statusBuilder.build());
//SampleRpc.HeartbeatResponse response = responseBuilder.build();
//SLF4JLoggerProxy.trace(this,
//                 "Returning {}: {}",
//                 response,
//                 inRequest.getId());
//return response;
//*/
//try {
//  for(int i=0;i<10;i++) {
//      SampleRpc.HeartbeatResponse.Builder responseBuilder = SampleRpc.HeartbeatResponse.newBuilder();
//      SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//      statusBuilder.setFailed(false);
//      responseBuilder.setId(inRequest.getId());
//      responseBuilder.setStatus(statusBuilder.build());
//      SampleRpc.HeartbeatResponse response = responseBuilder.build();
//      inResponseObserver.onNext(response);
//      Thread.sleep(1000);
//  }
//} catch (InterruptedException e) {
//  throw new RuntimeException(e);
//}
//inResponseObserver.onCompleted();
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#bindService()
//*/
//@Override
//public ServerServiceDefinition bindService()
//{
//System.out.println("COLIN: bindService invoked");
//return super.bindService();
//}
//}

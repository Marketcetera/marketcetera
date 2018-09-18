package org.marketcetera.fix.rpc;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.Validate;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.fix.FixAdminRpc;
import org.marketcetera.fix.FixAdminRpc.AddBrokerStatusListenerRequest;
import org.marketcetera.fix.FixAdminRpc.BrokerStatusListenerResponse;
import org.marketcetera.fix.FixAdminRpc.BrokersStatusRequest;
import org.marketcetera.fix.FixAdminRpc.BrokersStatusResponse;
import org.marketcetera.fix.FixAdminRpc.CreateFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.CreateFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.DeleteFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.DeleteFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.DisableFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.DisableFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.EnableFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.EnableFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest;
import org.marketcetera.fix.FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse;
import org.marketcetera.fix.FixAdminRpc.ReadFixSessionsRequest;
import org.marketcetera.fix.FixAdminRpc.ReadFixSessionsResponse;
import org.marketcetera.fix.FixAdminRpc.RemoveBrokerStatusListenerRequest;
import org.marketcetera.fix.FixAdminRpc.RemoveBrokerStatusListenerResponse;
import org.marketcetera.fix.FixAdminRpc.StartFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.StartFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.StopFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.StopFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.UpdateFixSessionRequest;
import org.marketcetera.fix.FixAdminRpc.UpdateFixSessionResponse;
import org.marketcetera.fix.FixAdminRpc.UpdateSequenceNumbersRequest;
import org.marketcetera.fix.FixAdminRpc.UpdateSequenceNumbersResponse;
import org.marketcetera.fix.FixAdminRpcServiceGrpc;
import org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase;
import org.marketcetera.fix.FixPermissions;
import org.marketcetera.fix.FixRpcUtil;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.fix.store.MessageStoreSession;
import org.marketcetera.fix.store.MessageStoreSessionDao;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a FIX admin RPC server implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase>
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        service = new Service();
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getServiceDescription()
     */
    @Override
    protected String getServiceDescription()
    {
        return DESCRIPTION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getService()
     */
    @Override
    protected FixAdminRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Provides a FIX admin RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            FixAdminRpcService.this.doLogin(inRequest,
                                            inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            FixAdminRpcService.this.doLogout(inRequest,
                                             inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            FixAdminRpcService.this.doHeartbeat(inRequest,
                                                inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#createFixSession(com.marketcetera.fix.FixAdminRpc.CreateFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createFixSession(CreateFixSessionRequest inRequest,
                                     StreamObserver<CreateFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received create FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.AddSessionAction.name());
                FixAdminRpc.CreateFixSessionResponse.Builder responseBuilder = FixAdminRpc.CreateFixSessionResponse.newBuilder();
                if(inRequest.hasFixSession()) {
                    FixSession fixSession = FixRpcUtil.getFixSession(inRequest.getFixSession());
                    Validate.isTrue(null == fixSessionProvider.findFixSessionByName(fixSession.getName()),
                                    "FIX Session " + fixSession.getName() + " already exists");
                    fixSession = fixSessionProvider.save(fixSession);
                    FixRpcUtil.getRpcFixSession(fixSession).ifPresent(rpcFixSession->responseBuilder.setFixSession(rpcFixSession));
                }
                FixAdminRpc.CreateFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#readFixSessions(com.marketcetera.fix.FixAdminRpc.ReadFixSessionsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readFixSessions(ReadFixSessionsRequest inRequest,
                                    StreamObserver<ReadFixSessionsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received read FIX sessions request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ViewSessionAction.name());
                FixAdminRpc.ReadFixSessionsResponse.Builder responseBuilder = FixAdminRpc.ReadFixSessionsResponse.newBuilder();
                PageRequest pageRequest = null;
                if(inRequest.hasPage()) {
                    pageRequest = PagingRpcUtil.getPageRequest(inRequest.getPage());
                } else {
                    pageRequest = new PageRequest(0,Integer.MAX_VALUE);
                }
//                CollectionPageResponse<ActiveFixSession> pagedResponse = fixSessionProvider.findActiveFixSessions(pageRequest);
//                if(pagedResponse != null) {
//                    responseBuilder.setPage(PagingRpcUtil.getPageResponse(pageRequest,
//                                                                          pagedResponse));
//                    for(ActiveFixSession activeFixSession : pagedResponse.getElements()) {
//                        FixRpcUtil.getRpcActiveFixSession(activeFixSession).ifPresent(rpcFixSession->responseBuilder.addFixSession(rpcFixSession));
//                    }
//                }
//                FixAdminRpc.ReadFixSessionsResponse response = responseBuilder.build();
//                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
//                                       "Returning {}",
//                                       response);
//                inResponseObserver.onNext(response);
//                inResponseObserver.onCompleted();
                throw new UnsupportedOperationException();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#updateFixSession(com.marketcetera.fix.FixAdminRpc.UpdateFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void updateFixSession(UpdateFixSessionRequest inRequest,
                                     StreamObserver<UpdateFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received update FIX sessions request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.EditSessionAction.name());
                FixAdminRpc.UpdateFixSessionResponse.Builder responseBuilder = FixAdminRpc.UpdateFixSessionResponse.newBuilder();
                FixAdminRpc.FixSession rpcFixSession = inRequest.getFixSession();
                FixSession existingFixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                Validate.isTrue(existingFixSession != null,
                                "FIX Session " + inRequest.getName() + " does not exist");
                if(existingFixSession instanceof MutableFixSession) {
                    MutableFixSession mutableFixSession = (MutableFixSession)existingFixSession;
                    mutableFixSession.setAffinity(rpcFixSession.getAffinity());
                    mutableFixSession.setBrokerId(rpcFixSession.getBrokerId());
                    mutableFixSession.setDescription(rpcFixSession.getDescription());
                    mutableFixSession.setHost(rpcFixSession.getHost());
                    mutableFixSession.setIsAcceptor(rpcFixSession.getAcceptor());
                    mutableFixSession.setIsEnabled(false); // no back-door enabling!
                    mutableFixSession.setMappedBrokerId(rpcFixSession.getMappedBrokerId());
                    mutableFixSession.setName(rpcFixSession.getName());
                    mutableFixSession.setPort(rpcFixSession.getPort());
                    mutableFixSession.setSessionId(rpcFixSession.getSessionId());
                    mutableFixSession.getSessionSettings().clear();
                    mutableFixSession.getSessionSettings().putAll(BaseRpcUtil.getMap(rpcFixSession.getSessionSettings()));
                    existingFixSession = fixSessionProvider.save(mutableFixSession);
                } else {
                    throw new IllegalStateException("Broker service returned a non-mutable FIX session - check configuration");
                }
                FixAdminRpc.UpdateFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#readFixSessionAttributeDescriptors(com.marketcetera.fix.FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void readFixSessionAttributeDescriptors(ReadFixSessionAttributeDescriptorsRequest inRequest,
                                                       StreamObserver<ReadFixSessionAttributeDescriptorsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received read FIX session attribute descriptors request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadFixSessionAttributeDescriptorsAction.name());
                FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse.Builder responseBuilder = FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse.newBuilder();
                Collection<FixSessionAttributeDescriptor> descriptors = fixSessionProvider.getFixSessionAttributeDescriptors();
                for(FixSessionAttributeDescriptor descriptor : descriptors) {
                    responseBuilder.addFixSessionAttributeDescriptors(FixRpcUtil.getRpcFixSessionAttributeDescriptor(descriptor));
                }
                FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#enableFixSession(com.marketcetera.fix.FixAdminRpc.EnableFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void enableFixSession(EnableFixSessionRequest inRequest,
                                     StreamObserver<EnableFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received enable FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.EnableSessionAction.name());
                FixAdminRpc.EnableFixSessionResponse.Builder responseBuilder = FixAdminRpc.EnableFixSessionResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                fixSessionProvider.enableSession(new SessionID(fixSession.getSessionId()));
                FixAdminRpc.EnableFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#disableFixSession(com.marketcetera.fix.FixAdminRpc.DisableFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void disableFixSession(DisableFixSessionRequest inRequest,
                                      StreamObserver<DisableFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received disable FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DisableSessionAction.name());
                FixAdminRpc.DisableFixSessionResponse.Builder responseBuilder = FixAdminRpc.DisableFixSessionResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                fixSessionProvider.disableSession(new SessionID(fixSession.getSessionId()));
                FixAdminRpc.DisableFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#deleteFixSession(com.marketcetera.fix.FixAdminRpc.DeleteFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteFixSession(DeleteFixSessionRequest inRequest,
                                     StreamObserver<DeleteFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received delete FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.DeleteSessionAction.name());
                FixAdminRpc.DeleteFixSessionResponse.Builder responseBuilder = FixAdminRpc.DeleteFixSessionResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                fixSessionProvider.delete(new SessionID(fixSession.getSessionId()));
                FixAdminRpc.DeleteFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#startFixSession(com.marketcetera.fix.FixAdminRpc.StartFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void startFixSession(StartFixSessionRequest inRequest,
                                    StreamObserver<StartFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received start FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.StartSessionAction.name());
                FixAdminRpc.StartFixSessionResponse.Builder responseBuilder = FixAdminRpc.StartFixSessionResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                fixSessionProvider.startSession(new SessionID(fixSession.getSessionId()));
                FixAdminRpc.StartFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#stopFixSession(com.marketcetera.fix.FixAdminRpc.StopFixSessionRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void stopFixSession(StopFixSessionRequest inRequest,
                                   StreamObserver<StopFixSessionResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received stop FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.StopSessionAction.name());
                FixAdminRpc.StopFixSessionResponse.Builder responseBuilder = FixAdminRpc.StopFixSessionResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                fixSessionProvider.stopSession(new SessionID(fixSession.getSessionId()));
                FixAdminRpc.StopFixSessionResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#updateSequenceNumbers(com.marketcetera.fix.FixAdminRpc.UpdateSequenceNumbersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void updateSequenceNumbers(UpdateSequenceNumbersRequest inRequest,
                                          StreamObserver<UpdateSequenceNumbersResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received update sequence numbers for FIX session request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.UpdateSequenceAction.name());
                FixAdminRpc.UpdateSequenceNumbersResponse.Builder responseBuilder = FixAdminRpc.UpdateSequenceNumbersResponse.newBuilder();
                FixSession fixSession = fixSessionProvider.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                BrokerStatus brokerStatus = brokerService.getBrokerStatus(new BrokerID(fixSession.getBrokerId()));
                if(brokerStatus.getStatus().isStarted()) {
                    throw new IllegalArgumentException("FIX session " + inRequest.getName() + " is running");
                }
                // TODO this should be moved to a service with transactions
                MessageStoreSession sessionInfo = fixSessionStoreDao.findBySessionId(fixSession.getSessionId());
                if(sessionInfo == null) {
                    throw new IllegalArgumentException("No FIX session store with name '" + inRequest.getName() + "'. The session may need to be started first.");
                }
                if(inRequest.getSenderSequenceNumber() != -1) {
                    sessionInfo.setSenderSeqNum(inRequest.getSenderSequenceNumber());
                }
                if(inRequest.getTargetSequenceNumber() != -1) {
                    sessionInfo.setTargetSeqNum(inRequest.getTargetSequenceNumber());
                }
                fixSessionStoreDao.save(sessionInfo);
                FixAdminRpc.UpdateSequenceNumbersResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#removeBrokerStatusListener(org.marketcetera.trading.rpc.FixAdminRpc.RemoveBrokerStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeBrokerStatusListener(RemoveBrokerStatusListenerRequest inRequest,
                                               StreamObserver<RemoveBrokerStatusListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received remove broker status listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> brokerStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(brokerStatusListenerProxy != null) {
                    brokerService.removeBrokerStatusListener((BrokerStatusListener)brokerStatusListenerProxy);
                    brokerStatusListenerProxy.close();
                }
                FixAdminRpc.RemoveBrokerStatusListenerResponse.Builder responseBuilder = FixAdminRpc.RemoveBrokerStatusListenerResponse.newBuilder();
                FixAdminRpc.RemoveBrokerStatusListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#getBrokersStatus(org.marketcetera.trading.rpc.FixAdminRpc.BrokersStatusRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getBrokersStatus(BrokersStatusRequest inRequest,
                                     StreamObserver<BrokersStatusResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received get brokers status request {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       FixPermissions.ViewBrokerStatusAction.name());
                FixAdminRpc.BrokersStatusResponse.Builder responseBuilder = FixAdminRpc.BrokersStatusResponse.newBuilder();
                BrokersStatus brokersStatus = brokerService.getBrokersStatus();
                FixRpcUtil.setBrokersStatus(brokersStatus,
                                            responseBuilder);
                FixAdminRpc.BrokersStatusResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#addBrokerStatusListener(org.marketcetera.fix.FixAdminRpc.AddBrokerStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addBrokerStatusListener(AddBrokerStatusListenerRequest inRequest,
                                            StreamObserver<BrokerStatusListenerResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received add broker status listener request {}",
                                       inRequest);
                authzService.authorize(sessionHolder.getUser(),
                                       FixPermissions.ViewBrokerStatusAction.name());
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> brokerStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(brokerStatusListenerProxy == null) {
                    brokerStatusListenerProxy = new BrokerStatusListenerProxy(listenerId,
                                                                              inResponseObserver);
                    listenerProxiesById.put(brokerStatusListenerProxy.getId(),
                                            brokerStatusListenerProxy);
                    brokerService.addBrokerStatusListener((BrokerStatusListener)brokerStatusListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
    }
    /**
     * Provides a connection between broker status requests and the server interface.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class BrokerStatusListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<BrokerStatusListenerResponse>
            implements BrokerStatusListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.brokers.BrokerStatus)
         */
        @Override
        public void receiveBrokerStatus(BrokerStatus inStatus)
        {
            FixRpcUtil.setBrokerStatus(inStatus,
                                       responseBuilder);
            BrokerStatusListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(FixAdminRpcService.class,
                                   "{} received broker status {}, sending {}",
                                   getId(),
                                   inStatus,
                                   response);
            // TODO does the user have permissions to view this broker?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new BrokerStatusListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;BrokerStatusListenerResponse&gt;</code> value
         */
        private BrokerStatusListenerProxy(String inId,
                                          StreamObserver<BrokerStatusListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final FixAdminRpc.BrokerStatusListenerResponse.Builder responseBuilder = FixAdminRpc.BrokerStatusListenerResponse.newBuilder();
    }
    /**
     * provides access to core broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to FIX sessions
     */
    @Autowired
    private FixSessionProvider fixSessionProvider;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides access to the FIX session data store
     */
    @Autowired
    private MessageStoreSessionDao fixSessionStoreDao;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * holds message listeners by id
     */
    private final Cache<String,BaseRpcUtil.AbstractServerListenerProxy<?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP FIX Admin RPC Service"; //$NON-NLS-1$
}

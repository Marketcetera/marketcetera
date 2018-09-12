package com.marketcetera.fix.rpc;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.Validate;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.persist.CollectionPageResponse;
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

import com.marketcetera.fix.FixAdminRpc;
import com.marketcetera.fix.FixAdminRpc.CreateFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.CreateFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.DeleteFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.DeleteFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.DisableFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.DisableFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.EnableFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.EnableFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.InstanceDataRequest;
import com.marketcetera.fix.FixAdminRpc.InstanceDataResponse;
import com.marketcetera.fix.FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest;
import com.marketcetera.fix.FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse;
import com.marketcetera.fix.FixAdminRpc.ReadFixSessionsRequest;
import com.marketcetera.fix.FixAdminRpc.ReadFixSessionsResponse;
import com.marketcetera.fix.FixAdminRpc.StartFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.StartFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.StopFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.StopFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.UpdateFixSessionRequest;
import com.marketcetera.fix.FixAdminRpc.UpdateFixSessionResponse;
import com.marketcetera.fix.FixAdminRpc.UpdateSequenceNumbersRequest;
import com.marketcetera.fix.FixAdminRpc.UpdateSequenceNumbersResponse;
import com.marketcetera.fix.FixAdminRpcServiceGrpc;
import com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase;
import com.marketcetera.fix.FixRpcUtil;
import com.marketcetera.fix.store.MessageStoreSession;
import com.marketcetera.fix.store.MessageStoreSessionDao;

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
                    Validate.isTrue(null == brokerService.findFixSessionByName(fixSession.getName()),
                                    "FIX Session " + fixSession.getName() + " already exists");
                    fixSession = brokerService.save(fixSession);
                    responseBuilder.setFixSession(FixRpcUtil.getRpcFixSession(fixSession));
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
                CollectionPageResponse<FixSession> pagedResponse = brokerService.findFixSessions(pageRequest);
                if(pagedResponse != null) {
                    responseBuilder.setPage(PagingRpcUtil.getPageResponse(pageRequest,
                                                                          pagedResponse));
                    for(FixSession fixSession : pagedResponse.getElements()) {
                        responseBuilder.addFixSession(FixRpcUtil.getRpcActiveFixSession(fixSession));
                    }
                }
                FixAdminRpc.ReadFixSessionsResponse response = responseBuilder.build();
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
                FixSession existingFixSession = brokerService.findFixSessionByName(inRequest.getName());
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
                    existingFixSession = brokerService.save(mutableFixSession);
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
         * @see com.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceImplBase#getInstanceData(com.marketcetera.fix.FixAdminRpc.InstanceDataRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getInstanceData(InstanceDataRequest inRequest,
                                    StreamObserver<InstanceDataResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(FixAdminRpcService.this,
                                       "Received get instance data {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadInstanceDataAction.name());
                FixAdminRpc.InstanceDataResponse.Builder responseBuilder = FixAdminRpc.InstanceDataResponse.newBuilder();
                AcceptorSessionAttributes acceptorSessionAttributes = brokerService.getFixSettingsFor(inRequest.getAffinity());
                responseBuilder.setInstanceData(FixRpcUtil.getRpcInstanceData(acceptorSessionAttributes));
                FixAdminRpc.InstanceDataResponse response = responseBuilder.build();
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
                Collection<FixSessionAttributeDescriptor> descriptors = brokerService.getFixSessionAttributeDescriptors();
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                brokerService.enableSession(new SessionID(fixSession.getSessionId()));
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                brokerService.disableSession(new SessionID(fixSession.getSessionId()));
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                brokerService.delete(new SessionID(fixSession.getSessionId()));
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                brokerService.startSession(new SessionID(fixSession.getSessionId()));
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
                if(fixSession == null) {
                    throw new IllegalArgumentException("No FIX session with name '" + inRequest.getName() + "'");
                }
                brokerService.stopSession(new SessionID(fixSession.getSessionId()));
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
                FixSession fixSession = brokerService.findFixSessionByName(inRequest.getName());
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
    }
    /**
     * provides access to core broker services
     */
    @Autowired
    private BrokerService brokerService;
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
     * description of the service
     */
    private static final String DESCRIPTION = "MATP FIX Admin RPC Service"; //$NON-NLS-1$
}

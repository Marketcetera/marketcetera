package org.marketcetera.cluster.rpc;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.cluster.rpc.ClusterRpc.InstanceDataRequest;
import org.marketcetera.cluster.rpc.ClusterRpc.InstanceDataResponse;
import org.marketcetera.cluster.rpc.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides a FIX admin RPC server implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,ClusterRpcServiceGrpc.ClusterRpcServiceImplBase>
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
    protected ClusterRpcServiceImplBase getService()
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
            extends ClusterRpcServiceGrpc.ClusterRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see com.marketcetera.fix.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            ClusterRpcService.this.doLogin(inRequest,
                                            inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            ClusterRpcService.this.doLogout(inRequest,
                                             inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            ClusterRpcService.this.doHeartbeat(inRequest,
                                                inResponseObserver);
        }
        /* (non-Javadoc)
         * @see com.marketcetera.fix.ClusterRpcServiceGrpc.ClusterRpcServiceImplBase#getInstanceData(com.marketcetera.fix.ClusterRpc.InstanceDataRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getInstanceData(InstanceDataRequest inRequest,
                                    StreamObserver<InstanceDataResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(ClusterRpcService.this,
                                       "Received get instance data {} from {}",
                                       inRequest,
                                       sessionHolder);
                authzService.authorize(sessionHolder.getUser(),
                                       AdminPermissions.ReadInstanceDataAction.name());
                ClusterRpc.InstanceDataResponse.Builder responseBuilder = ClusterRpc.InstanceDataResponse.newBuilder();
//                AcceptorSessionAttributes acceptorSessionAttributes = brokerService.getFixSettingsFor(inRequest.getAffinity());
//                responseBuilder.setInstanceData(ClusterRpcUtil.getRpcInstanceData(acceptorSessionAttributes));
//                ClusterRpc.InstanceDataResponse response = responseBuilder.build();
//                SLF4JLoggerProxy.trace(ClusterRpcService.this,
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
    }
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP FIX Admin RPC Service"; //$NON-NLS-1$
}

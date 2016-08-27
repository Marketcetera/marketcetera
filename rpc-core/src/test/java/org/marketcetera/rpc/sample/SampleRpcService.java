package org.marketcetera.rpc.sample;

import javax.annotation.PostConstruct;

import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.sample.SampleRpcServiceGrpc;
import org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase;
import org.marketcetera.rpc.server.AbstractRpcService;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides a sample RPC service implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SampleRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,SampleRpcServiceGrpc.SampleRpcServiceImplBase>
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
     * @see org.marketcetera.rpc.sample.client.AbstractRpcService#getService()
     */
    @Override
    protected SampleRpcServiceImplBase getService()
    {
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.client.AbstractRpcService#getServiceDescription()
     */
    @Override
    protected String getServiceDescription()
    {
        return description;
    }
    /**
     * Provides the RPC service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends SampleRpcServiceGrpc.SampleRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            SampleRpcService.this.doLogin(inRequest,
                                          inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            SampleRpcService.this.doLogout(inRequest,
                                           inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            SampleRpcService.this.doHeartbeat(inRequest,
                                              inResponseObserver);
        }
    }
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Sample RPC Service";
}

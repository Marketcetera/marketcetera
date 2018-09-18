package org.marketcetera.cluster;

import java.util.concurrent.Callable;

import org.marketcetera.cluster.rpc.ClusterRpc;
import org.marketcetera.cluster.rpc.ClusterRpcServiceGrpc;
import org.marketcetera.cluster.rpc.ClusterRpcServiceGrpc.ClusterRpcServiceBlockingStub;
import org.marketcetera.cluster.rpc.ClusterRpcServiceGrpc.ClusterRpcServiceStub;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import io.grpc.Channel;

/* $License$ */

/**
 * Provides an RPC-based {@link ClusterClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterRpcClient
        extends AbstractRpcClient<ClusterRpcServiceBlockingStub,ClusterRpcServiceStub,ClusterRpcClientParameters>
        implements ClusterClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ClusterClient#getInstanceData(int)
     */
    @Override
    public InstanceData getInstanceData(int inAffinity)
    {
        return executeCall(new Callable<InstanceData>() {
            @Override
            public InstanceData call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(ClusterRpcClient.this,
                                       "{} get instance data {}",
                                       getSessionId(),
                                       inAffinity);
                ClusterRpc.InstanceDataRequest.Builder requestBuilder = ClusterRpc.InstanceDataRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setAffinity(inAffinity);
                ClusterRpc.InstanceDataRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(ClusterRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                ClusterRpc.InstanceDataResponse response = getBlockingStub().getInstanceData(request);
                SLF4JLoggerProxy.trace(ClusterRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                InstanceData result = ClusterRpcUtil.getInstanceData(response.getInstanceData()).get();
                SLF4JLoggerProxy.trace(ClusterRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /**
     * Create a new ClusterRpcClient instance.
     *
     * @param inParameters a <code>ClusterRpcClientParameters</code> value
     */
    ClusterRpcClient(ClusterRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected ClusterRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return ClusterRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected ClusterRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return ClusterRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected BaseRpc.HeartbeatResponse executeHeartbeat(HeartbeatRequest inRequest)
    {
        return getBlockingStub().heartbeat(inRequest);
    }
    /**
     * The client's application ID: the application name.
     */
    public static final String APP_ID_NAME = "ClusterRpcClient"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(ClusterClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}

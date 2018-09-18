package org.marketcetera.fix;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokersStatus;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.fix.FixAdminRpc.AddBrokerStatusListenerRequest;
import org.marketcetera.fix.FixAdminRpc.BrokerStatusListenerResponse;
import org.marketcetera.fix.FixAdminRpc.BrokersStatusRequest;
import org.marketcetera.fix.FixAdminRpc.BrokersStatusResponse;
import org.marketcetera.fix.FixAdminRpc.RemoveBrokerStatusListenerRequest;
import org.marketcetera.fix.FixAdminRpc.RemoveBrokerStatusListenerResponse;
import org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceBlockingStub;
import org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceStub;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.collect.Lists;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC-based {@link FixAdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminRpcClient
        extends AbstractRpcClient<FixAdminRpcServiceBlockingStub,FixAdminRpcServiceStub,FixAdminRpcClientParameters>
        implements FixAdminClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#createFixSession(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession createFixSession(FixSession inFixSession)
    {
        return executeCall(new Callable<FixSession>() {
            @Override
            public FixSession call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} create FIX session {}",
                                       getSessionId(),
                                       inFixSession);
                FixAdminRpc.CreateFixSessionRequest.Builder requestBuilder = FixAdminRpc.CreateFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                FixRpcUtil.getRpcFixSession(inFixSession).ifPresent(rpcFixSession->requestBuilder.setFixSession(rpcFixSession));
                FixAdminRpc.CreateFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.CreateFixSessionResponse response = getBlockingStub().createFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                FixSession result = null;
                if(response.hasFixSession()) {
                    result = FixRpcUtil.getFixSession(response.getFixSession());
                }
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#readFixSessions()
     */
    @Override
    public List<ActiveFixSession> readFixSessions()
    {
        return Lists.newArrayList(readFixSessions(new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#readFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<ActiveFixSession>>() {
            @Override
            public CollectionPageResponse<ActiveFixSession> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} read FIX sessions",
                                       getSessionId());
                FixAdminRpc.ReadFixSessionsRequest.Builder requestBuilder = FixAdminRpc.ReadFixSessionsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                FixAdminRpc.ReadFixSessionsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.ReadFixSessionsResponse response = getBlockingStub().readFixSessions(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                CollectionPageResponse<ActiveFixSession> results = new CollectionPageResponse<>();
                for(FixAdminRpc.ActiveFixSession rpcFixSession : response.getFixSessionList()) {
                    results.getElements().add(FixRpcUtil.getActiveFixSession(rpcFixSession));
                }
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPage(),
                                              results);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateFixSession(java.lang.String, org.marketcetera.fix.FixSession)
     */
    @Override
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update FIX session {}: {}",
                                       getSessionId(),
                                       inIncomingName,
                                       inFixSession);
                FixAdminRpc.UpdateFixSessionRequest.Builder requestBuilder = FixAdminRpc.UpdateFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inIncomingName));
                FixRpcUtil.getRpcFixSession(inFixSession).ifPresent(rpcFixSession->requestBuilder.setFixSession(rpcFixSession));
                FixAdminRpc.UpdateFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateFixSessionResponse response = getBlockingStub().updateFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#enableFixSession(java.lang.String)
     */
    @Override
    public void enableFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} enable FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.EnableFixSessionRequest.Builder requestBuilder = FixAdminRpc.EnableFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.EnableFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.EnableFixSessionResponse response = getBlockingStub().enableFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#disableFixSession(java.lang.String)
     */
    @Override
    public void disableFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} disable FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.DisableFixSessionRequest.Builder requestBuilder = FixAdminRpc.DisableFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.DisableFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.DisableFixSessionResponse response = getBlockingStub().disableFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#deleteFixSession(java.lang.String)
     */
    @Override
    public void deleteFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} delete FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.DeleteFixSessionRequest.Builder requestBuilder = FixAdminRpc.DeleteFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.DeleteFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.DeleteFixSessionResponse response = getBlockingStub().deleteFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#stopFixSession(java.lang.String)
     */
    @Override
    public void stopFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} stop FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.StopFixSessionRequest.Builder requestBuilder = FixAdminRpc.StopFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.StopFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.StopFixSessionResponse response = getBlockingStub().stopFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#startFixSession(java.lang.String)
     */
    @Override
    public void startFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} start FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.StartFixSessionRequest.Builder requestBuilder = FixAdminRpc.StartFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.StartFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.StartFixSessionResponse response = getBlockingStub().startFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return executeCall(new Callable<Collection<FixSessionAttributeDescriptor>>() {
            @Override
            public Collection<FixSessionAttributeDescriptor> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} get fix session attribute descriptors",
                                       getSessionId());
                FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest.Builder requestBuilder = FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse response = getBlockingStub().readFixSessionAttributeDescriptors(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Collection<FixSessionAttributeDescriptor> results = Lists.newArrayList();
                for(FixAdminRpc.FixSessionAttributeDescriptor rpcFixSessionAttributeDescriptor : response.getFixSessionAttributeDescriptorsList()) {
                    results.add(FixRpcUtil.getFixSessionAttributeDescriptor(rpcFixSessionAttributeDescriptor));
                }
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateSequenceNumbers(java.lang.String, int, int)
     */
    @Override
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update sequence numbers for {} to {}/{}",
                                       getSessionId(),
                                       inSessionName,
                                       inSenderSequenceNumber,
                                       inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
                requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateSenderSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update sender sequence number for {} to {}",
                                       getSessionId(),
                                       inSessionName,
                                       inSenderSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
                requestBuilder.setTargetSequenceNumber(-1);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateTargetSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update target sequence number for {} to {}",
                                       getSessionId(),
                                       inSessionName,
                                       inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(-1);
                requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     * Create a new FixAdminRpcClient instance.
     *
     * @param inParameters a <code>FixAdminRpcClientParameters</code> value
     */
    FixAdminRpcClient(FixAdminRpcClientParameters inParameters)
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
    protected FixAdminRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return FixAdminRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected FixAdminRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return FixAdminRpcServiceGrpc.newStub(inChannel);
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
    public static final String APP_ID_NAME = "FixAdminRpcClient"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(FixAdminClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}

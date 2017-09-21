package org.marketcetera.dataflow.client.rpc;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.dataflow.client.DataFlowClient;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.dataflow.rpc.DataFlowRpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceBlockingStub;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceStub;
import org.marketcetera.dataflow.rpc.DataFlowRpcUtil;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.collect.Lists;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC {@link DataFlowClient} interface.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowRpcClient
        extends AbstractRpcClient<DataFlowRpcServiceBlockingStub,DataFlowRpcServiceStub,DataFlowRpcClientParameters>
        implements DataFlowClient
{
    /**
     * Create a new SERpcClient instance.
     *
     * @param inParameters an <code>SERpcClientParameters</code> value
     */
    DataFlowRpcClient(DataFlowRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getProviders()
     */
    @Override
    public List<ModuleURN> getProviders()
    {
        return executeCall(new Callable<List<ModuleURN>>(){
            @Override
            public List<ModuleURN> call()
                    throws Exception
            {
                DataFlowRpc.ProvidersRequest.Builder requestBuilder = DataFlowRpc.ProvidersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                DataFlowRpc.ProvidersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.ProvidersResponse response = getBlockingStub().getProviders(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<ModuleURN> providers = Lists.newArrayList();
                for(DataFlowRpc.ModuleURN provider : response.getProviderList()) {
                    providers.add(new ModuleURN(provider.getValue()));
                }
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       providers);
                return providers;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getInstances(org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ModuleURN inProviderURN)
    {
        return executeCall(new Callable<List<ModuleURN>>(){
            @Override
            public List<ModuleURN> call()
                    throws Exception
            {
                DataFlowRpc.InstancesRequest.Builder requestBuilder = DataFlowRpc.InstancesRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                if(inProviderURN != null) {
                    requestBuilder.setProvider(DataFlowRpcUtil.getRpcModuleUrn(inProviderURN));
                }
                DataFlowRpc.InstancesRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.InstancesResponse response = getBlockingStub().getInstances(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                List<ModuleURN> instances = Lists.newArrayList();
                for(DataFlowRpc.ModuleURN instance : response.getInstanceList()) {
                    instances.add(DataFlowRpcUtil.getModuleUrn(instance));
                }
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       instances);
                return instances;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getModuleInfo(org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ModuleURN inURN)
    {
        return executeCall(new Callable<ModuleInfo>(){
            @Override
            public ModuleInfo call()
                    throws Exception
            {
                DataFlowRpc.ModuleInfoRequest.Builder requestBuilder = DataFlowRpc.ModuleInfoRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setInstance(DataFlowRpcUtil.getRpcModuleUrn(inURN));
                DataFlowRpc.ModuleInfoRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.ModuleInfoResponse response = getBlockingStub().getModuleInfo(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                ModuleInfo info = null;
                if(response.hasInfo()) {
                    info = DataFlowRpcUtil.getModuleInfo(response.getInfo());
                }
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       info);
                return info;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#createModule(org.marketcetera.module.ModuleURN, java.lang.Object[])
     */
    @Override
    public ModuleURN createModule(ModuleURN inURN,
                                  Object... inParameters)
    {
        return executeCall(new Callable<ModuleURN>(){
            @Override
            public ModuleURN call()
                    throws Exception
            {
                DataFlowRpc.CreateModuleRequest.Builder requestBuilder = DataFlowRpc.CreateModuleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setProvider(DataFlowRpcUtil.getRpcModuleUrn(inURN));
                for(Object param : inParameters) {
                    requestBuilder.addParameters(DataFlowRpcUtil.getRpcParameter(param));
                }
                DataFlowRpc.CreateModuleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.CreateModuleResponse response = getBlockingStub().createModule(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                ModuleURN instanceUrn = null;
                if(response.hasInstance()) {
                    instanceUrn = DataFlowRpcUtil.getModuleUrn(response.getInstance());
                }
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       instanceUrn);
                return instanceUrn;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#start(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void startModule(final ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                DataFlowRpc.StartModuleRequest.Builder requestBuilder = DataFlowRpc.StartModuleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setInstance(DataFlowRpcUtil.getRpcModuleUrn(inURN));
                DataFlowRpc.StartModuleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.StartModuleResponse response = getBlockingStub().startModule(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#stop(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stopModule(ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                DataFlowRpc.StopModuleRequest.Builder requestBuilder = DataFlowRpc.StopModuleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setInstance(DataFlowRpcUtil.getRpcModuleUrn(inURN));
                DataFlowRpc.StopModuleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.StopModuleResponse response = getBlockingStub().stopModule(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#delete(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void deleteModule(ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                DataFlowRpc.DeleteModuleRequest.Builder requestBuilder = DataFlowRpc.DeleteModuleRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setInstance(DataFlowRpcUtil.getRpcModuleUrn(inURN));
                DataFlowRpc.DeleteModuleRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.DeleteModuleResponse response = getBlockingStub().deleteModule(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
    {
//        executeCall(new Callable<Void>(){
//            @Override
//            public Void call()
//                    throws Exception
//            {
//                // note that inData must be JAXB marshallable
//                getBlockingStub().sendData(DataFlowRpc.SendDataRequest.newBuilder().setSessionId(getSessionId().getValue())
//                                           .setPayload(marshal(new XmlValue(inData))).build());
//                return null;
//            }
//        });
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#createDataFlow(java.util.List, boolean)
     */
    @Override
    public DataFlowID createDataFlow(List<DataRequest> inDataRequests,
                                     boolean inAppendDataSink)
    {
        return executeCall(new Callable<DataFlowID>(){
            @Override
            public DataFlowID call()
                    throws Exception
            {
                DataFlowRpc.CreateDataFlowRequest.Builder requestBuilder = DataFlowRpc.CreateDataFlowRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setAppendDataSink(inAppendDataSink);
                for(DataRequest dataRequest : inDataRequests) {
                    requestBuilder.addDataRequests(DataFlowRpcUtil.getRpcDataRequest(dataRequest));
                }
                DataFlowRpc.CreateDataFlowRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.CreateDataFlowResponse response = getBlockingStub().createDataFlow(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                DataFlowID dataFlowId = DataFlowRpcUtil.getDataFlowId(response.getDataFlowId());
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       dataFlowId);
                return dataFlowId;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#createDataFlow(java.util.List)
     */
    @Override
    public DataFlowID createDataFlow(List<DataRequest> inDataRequest)
    {
        return createDataFlow(inDataRequest,
                              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#cancelDataFlow(org.marketcetera.module.DataFlowID)
     */
    @Override
    public void cancelDataFlow(DataFlowID inDataFlowId)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                DataFlowRpc.CancelDataFlowRequest.Builder requestBuilder = DataFlowRpc.CancelDataFlowRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setDataFlowId((DataFlowRpcUtil.getRpcDataFlowId(inDataFlowId)));
                DataFlowRpc.CancelDataFlowRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.CancelDataFlowResponse response = getBlockingStub().cancelDataFlow(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlowInfo(org.marketcetera.module.DataFlowID)
     */
    @Override
    public DataFlowInfo getDataFlowInfo(DataFlowID inDataFlowId)
    {
        return executeCall(new Callable<DataFlowInfo>(){
            @Override
            public DataFlowInfo call()
                    throws Exception
            {
                DataFlowRpc.GetDataFlowInfoRequest.Builder requestBuilder = DataFlowRpc.GetDataFlowInfoRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                DataFlowRpc.GetDataFlowInfoRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.GetDataFlowInfoResponse response = getBlockingStub().getDataFlowInfo(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                DataFlowInfo dataFlowInfo = DataFlowRpcUtil.getDataFlowInfo(response.getDataFlowInfo());
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       dataFlowInfo);
                return dataFlowInfo;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#addDataReceiver(org.marketcetera.SEClient.DataReceiver)
     */
    @Override
    public void addDataReceiver(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        synchronized (receivers) {
            receivers.addFirst(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#removeDataReciever(org.marketcetera.SEClient.DataReceiver)
     */
    @Override
    public void removeDataReceiver(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        synchronized (receivers) {
            receivers.removeFirstOccurrence(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected DataFlowRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return DataFlowRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected DataFlowRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return DataFlowRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected BaseRpc.LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected BaseRpc.LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected void executeHeartbeat(BaseRpc.HeartbeatRequest inRequest,
                                    StreamObserver<BaseRpc.HeartbeatResponse> inObserver)
    {
        getAsyncStub().heartbeat(inRequest,
                                 inObserver);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /**
     * receivers of remove data
     */
    private final Deque<DataReceiver> receivers = new LinkedList<DataReceiver>();
    /**
     * The client's application ID: the application name.
     */
    private static final String APP_ID_NAME = DataFlowRpcClient.class.getSimpleName();
    /**
     * The client's application ID: the version.
     */
    private static final VersionInfo APP_ID_VERSION = new VersionInfo(Version.pomversion);
    /**
     * The client's application ID: the ID.
     */
    private static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlows()
     */
    @Override
    public List<DataFlowID> getDataFlows()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlows(org.marketcetera.persist.PageRequest)
     */
    @Override
    public List<DataFlowID> getDataFlows(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlowHistory(org.marketcetera.persist.PageRequest)
     */
    @Override
    public List<DataFlowInfo> getDataFlowHistory(PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlowHistory()
     */
    @Override
    public List<DataFlowInfo> getDataFlowHistory()
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}

package org.marketcetera.dataflow.client.rpc;

import java.util.List;
import java.util.concurrent.Callable;

import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.dataflow.client.DataFlowClient;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.dataflow.rpc.DataFlowRpc;
import org.marketcetera.dataflow.rpc.DataFlowRpc.DataReceiverResponse;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceBlockingStub;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceStub;
import org.marketcetera.dataflow.rpc.DataFlowRpcUtil;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.base.BaseRpcUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import io.grpc.Channel;

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
                requestBuilder.setDataFlowId(DataFlowRpcUtil.getRpcDataFlowId(inDataFlowId));
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
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inReceiver)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inReceiver);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} adding report listener",
                                       getSessionId());
                DataFlowRpc.AddDataReceiverRequest.Builder requestBuilder = DataFlowRpc.AddDataReceiverRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                DataFlowRpc.AddDataReceiverRequest addDataReceiverRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addDataReceiverRequest);
                getAsyncStub().addDataReceiver(addDataReceiverRequest,
                                               (DataReceiverProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#removeDataReciever(org.marketcetera.SEClient.DataReceiver)
     */
    @Override
    public void removeDataReceiver(DataReceiver inDataReceiver)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxies.getIfPresent(inDataReceiver);
        listenerProxies.invalidate(inDataReceiver);
        if(proxy == null) {
            return;
        }
        listenerProxiesById.invalidate(proxy.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} removing report listener",
                                       getSessionId());
                DataFlowRpc.RemoveDataReceiverRequest.Builder requestBuilder = DataFlowRpc.RemoveDataReceiverRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                DataFlowRpc.RemoveDataReceiverRequest removeDataReceiverRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeDataReceiverRequest);
                DataFlowRpc.RemoveDataReceiverResponse response = getBlockingStub().removeDataReceiver(removeDataReceiverRequest);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlows()
     */
    @Override
    public List<DataFlowID> getDataFlows()
    {
        return Lists.newArrayList(getDataFlows(new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlows(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<DataFlowID> getDataFlows(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<DataFlowID>>(){
            @Override
            public CollectionPageResponse<DataFlowID> call()
                    throws Exception
            {
                DataFlowRpc.GetDataFlowsRequest.Builder requestBuilder = DataFlowRpc.GetDataFlowsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                DataFlowRpc.GetDataFlowsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.GetDataFlowsResponse response = getBlockingStub().getDataFlows(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                CollectionPageResponse<DataFlowID> results = new CollectionPageResponse<>();
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                List<DataFlowID> resultList = Lists.newArrayList();
                for(String rpcDataFlowId : response.getDataFlowIdsList()) {
                    resultList.add(DataFlowRpcUtil.getDataFlowId(rpcDataFlowId));
                }
                results.setElements(resultList);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlowHistory(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<DataFlowInfo> getDataFlowHistory(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<DataFlowInfo>>(){
            @Override
            public CollectionPageResponse<DataFlowInfo> call()
                    throws Exception
            {
                DataFlowRpc.GetDataFlowHistoryRequest.Builder requestBuilder = DataFlowRpc.GetDataFlowHistoryRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                DataFlowRpc.GetDataFlowHistoryRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                DataFlowRpc.GetDataFlowHistoryResponse response = getBlockingStub().getDataFlowHistory(request);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                CollectionPageResponse<DataFlowInfo> results = new CollectionPageResponse<>();
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                List<DataFlowInfo> resultList = Lists.newArrayList();
                for(DataFlowRpc.DataFlowInfo rpcDataFlowInfo : response.getDataFlowInfosList()) {
                    resultList.add(DataFlowRpcUtil.getDataFlowInfo(rpcDataFlowInfo));
                }
                results.setElements(resultList);
                SLF4JLoggerProxy.trace(DataFlowRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataFlowClient#getDataFlowHistory()
     */
    @Override
    public List<DataFlowInfo> getDataFlowHistory()
    {
        return Lists.newArrayList(getDataFlowHistory(new PageRequest(0,Integer.MAX_VALUE)).getElements());
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
    protected BaseRpc.HeartbeatResponse executeHeartbeat(BaseRpc.HeartbeatRequest inRequest)
    {
        return getBlockingStub().heartbeat(inRequest);
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
     * Creates the appropriate proxy for the given listener.
     *
     * @param inListener an <code>Object</code> value
     * @return an <code>AbstractListenerProxy&lt;?,?,?&gt;</code> value
     */
    private static AbstractClientListenerProxy<?,?,?> getListenerFor(Object inListener)
    {
        if(inListener instanceof DataReceiver) {
            return new DataReceiverProxy((DataReceiver)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Provides an interface between data receiver stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DataReceiverProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<DataFlowRpc.DataReceiverResponse,Object,DataReceiver>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected Object translateMessage(DataReceiverResponse inResponse)
        {
            return DataFlowRpcUtil.getParameter(inResponse.getData());
        }
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(DataReceiver inMessageListener,
                                   Object inMessage)
        {
            inMessageListener.receiveData(inMessage);
        }
        /**
         * Create a new DataReceiverProxy instance.
         *
         * @param inDataReceiver a <code>DataReceiver</code> value
         */
        protected DataReceiverProxy(DataReceiver inDataReceiver)
        {
            super(inDataReceiver);
        }
    }
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
    /**
     * holds listeners by their id
     */
    private final Cache<String,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * holds listener proxies keyed by the listener
     */
    private final LoadingCache<Object,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxies = CacheBuilder.newBuilder().build(new CacheLoader<Object,AbstractClientListenerProxy<?,?,?>>() {
        @Override
        public BaseRpcUtil.AbstractClientListenerProxy<?,?,?> load(Object inKey)
                throws Exception
        {
            BaseRpcUtil.AbstractClientListenerProxy<?,?,?> proxy = getListenerFor(inKey);
            listenerProxiesById.put(proxy.getId(),
                                    proxy);
            return proxy;
        }}
    );
}

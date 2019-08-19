package org.marketcetera.dataflow.server.rpc;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.client.DataBroadcaster;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.dataflow.rpc.DataFlowRpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase;
import org.marketcetera.dataflow.rpc.DataFlowRpcUtil;
import org.marketcetera.dataflow.service.DataFlowService;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.PageResponse;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides Data Flow RPC server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#start()
     */
    @Override
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
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#getService()
     */
    @Override
    protected DataFlowRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Data Flow RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(BaseRpc.LoginRequest inRequest,
                          StreamObserver<BaseRpc.LoginResponse> inResponseObserver)
        {
            DataFlowRpcService.this.doLogin(inRequest,
                                            inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(BaseRpc.LogoutRequest inRequest,
                           StreamObserver<BaseRpc.LogoutResponse> inResponseObserver)
        {
            DataFlowRpcService.this.doLogout(inRequest,
                                             inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(BaseRpc.HeartbeatRequest inRequest,
                              StreamObserver<BaseRpc.HeartbeatResponse> inResponseObserver)
        {
            DataFlowRpcService.this.doHeartbeat(inRequest,
                                                inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#getProviders(org.marketcetera.saclient.rpc.SEClientRpc.ProvidersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getProviders(DataFlowRpc.ProvidersRequest inRequest,
                                 StreamObserver<DataFlowRpc.ProvidersResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received getProviders request",
                                       getServiceDescription());
                List<ModuleURN> providers = moduleManager.getProviders();
                DataFlowRpc.ProvidersResponse.Builder responseBuilder = DataFlowRpc.ProvidersResponse.newBuilder();
                if(providers != null) {
                    for(ModuleURN provider : providers) {
                        responseBuilder.addProvider(DataFlowRpc.ModuleURN.newBuilder().setValue(provider.getValue()).build());
                    }
                }
                DataFlowRpc.ProvidersResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       providers);
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#getInstances(org.marketcetera.saclient.rpc.SEClientRpc.InstancesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getInstances(DataFlowRpc.InstancesRequest inRequest,
                                 StreamObserver<DataFlowRpc.InstancesResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN provider = null;
                if(inRequest.hasProvider()) {
                    provider = DataFlowRpcUtil.getModuleUrn(inRequest.getProvider());
                }
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received get instances request {}",
                                       getServiceDescription(),
                                       inRequest);
                List<ModuleURN> instances = moduleManager.getModuleInstances(provider);
                DataFlowRpc.InstancesResponse.Builder responseBuilder = DataFlowRpc.InstancesResponse.newBuilder();
                if(instances != null) {
                    for(ModuleURN instance : instances) {
                        responseBuilder.addInstance(DataFlowRpcUtil.getRpcModuleUrn(instance));
                    }
                }
                DataFlowRpc.InstancesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#getModuleInfo(org.marketcetera.saclient.rpc.SEClientRpc.ModuleInfoRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getModuleInfo(DataFlowRpc.ModuleInfoRequest inRequest,
                                  StreamObserver<DataFlowRpc.ModuleInfoResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received get module info request {}",
                                       getServiceDescription(),
                                       inRequest);
                ModuleURN instance = DataFlowRpcUtil.getModuleUrn(inRequest.getInstance());
                ModuleInfo info = moduleManager.getModuleInfo(instance);
                DataFlowRpc.ModuleInfoResponse.Builder responseBuilder = DataFlowRpc.ModuleInfoResponse.newBuilder();
                if(info != null) {
                    responseBuilder.setInfo(DataFlowRpcUtil.getRpcModuleInfo(info));
                }
                DataFlowRpc.ModuleInfoResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#start(org.marketcetera.saclient.rpc.SEClientRpc.StartModuleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void startModule(DataFlowRpc.StartModuleRequest inRequest,
                                StreamObserver<DataFlowRpc.StartModuleResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received start module request  {}",
                                       getServiceDescription(),
                                       inRequest);
                DataFlowRpc.StartModuleResponse.Builder responseBuilder = DataFlowRpc.StartModuleResponse.newBuilder();
                moduleManager.start(instance);
                DataFlowRpc.StartModuleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#StopModule(org.marketcetera.saclient.rpc.SEClientRpc.StopModuleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void stopModule(DataFlowRpc.StopModuleRequest inRequest,
                               StreamObserver<DataFlowRpc.StopModuleResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received stop module request {}",
                                       getServiceDescription(),
                                       inRequest);
                DataFlowRpc.StopModuleResponse.Builder responseBuilder = DataFlowRpc.StopModuleResponse.newBuilder();
                moduleManager.stop(instance);
                DataFlowRpc.StopModuleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#delete(org.marketcetera.saclient.rpc.SEClientRpc.DeleteRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteModule(DataFlowRpc.DeleteModuleRequest inRequest,
                                 StreamObserver<DataFlowRpc.DeleteModuleResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received delete module request {}",
                                       getServiceDescription(),
                                       inRequest);
                DataFlowRpc.DeleteModuleResponse.Builder responseBuilder = DataFlowRpc.DeleteModuleResponse.newBuilder();
                moduleManager.deleteModule(instance);
                DataFlowRpc.DeleteModuleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#sendData(org.marketcetera.saclient.rpc.SEClientRpc.SendDataRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void sendData(DataFlowRpc.SendDataRequest inRequest,
                             StreamObserver<DataFlowRpc.SendDataResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received send data request {}",
                                       getServiceDescription(),
                                       inRequest);
                Object data = DataFlowRpcUtil.getParameter(inRequest.getPayload());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} sending {}",
                                       getServiceDescription(),
                                       data);
                DataFlowRpc.SendDataResponse.Builder responseBuilder = DataFlowRpc.SendDataResponse.newBuilder();
                for(DataBroadcaster dataBroadcaster : dataBroadcasters) {
                    try {
                        dataBroadcaster.receiveData(data);
                    } catch (Exception e) {
                        PlatformServices.handleException(DataFlowRpcService.this,
                                                         "Error broadcasting " + data,
                                                         e);
                    }
                }
                DataFlowRpc.SendDataResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning from sendData call",
                                       getServiceDescription());
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
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#createModule(org.marketcetera.dataflow.rpc.DataFlowRpc.CreateModuleRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createModule(DataFlowRpc.CreateModuleRequest inRequest,
                                 StreamObserver<DataFlowRpc.CreateModuleResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received create module request {}",
                                       getServiceDescription(),
                                       inRequest);
                ModuleURN provider = DataFlowRpcUtil.getModuleUrn(inRequest.getProvider());
                List<Object> parameters = null;
                for(String param : inRequest.getParametersList()) {
                    if(parameters == null) {
                        parameters = Lists.newArrayList();
                    }
                    parameters.add(DataFlowRpcUtil.getParameter(param));
                }
                ModuleURN instance;
                if(parameters == null) {
                    instance = moduleManager.createModule(provider);
                } else {
                    instance = moduleManager.createModule(provider,
                                                          parameters.toArray(new Object[parameters.size()]));
                }
                DataFlowRpc.CreateModuleResponse.Builder responseBuilder = DataFlowRpc.CreateModuleResponse.newBuilder();
                responseBuilder.setInstance(DataFlowRpcUtil.getRpcModuleUrn(instance));
                DataFlowRpc.CreateModuleResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                inResponseObserver.onError(e);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#createDataFlow(org.marketcetera.dataflow.rpc.DataFlowRpc.CreateDataFlowRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createDataFlow(DataFlowRpc.CreateDataFlowRequest inRequest,
                                   StreamObserver<DataFlowRpc.CreateDataFlowResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received create data flow request {}",
                                       sessionHolder,
                                       inRequest);
                List<DataRequest> dataRequestBuilder = Lists.newArrayList();
                for(DataFlowRpc.DataRequest rpcDataRequest : inRequest.getDataRequestsList()) {
                    DataRequest dataRequest = DataFlowRpcUtil.getDataRequest(rpcDataRequest);
                    SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                           "{} adding data request {}",
                                           sessionHolder,
                                           dataRequest.getRequestURN());
                    dataRequestBuilder.add(dataRequest);
                }
                boolean appendToSink = inRequest.getAppendDataSink();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} issuing data request {}",
                                       sessionHolder,
                                       dataRequestBuilder);
                DataFlowID dataFlowId = moduleManager.createDataFlow(dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]),
                                                                     appendToSink);
                DataFlowRpc.CreateDataFlowResponse.Builder responseBuilder = DataFlowRpc.CreateDataFlowResponse.newBuilder();
                responseBuilder.setDataFlowId(DataFlowRpcUtil.getRpcDataFlowId(dataFlowId));
                DataFlowRpc.CreateDataFlowResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       sessionHolder,
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                inResponseObserver.onError(e);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#cancelDataFlow(org.marketcetera.dataflow.rpc.DataFlowRpc.CancelDataFlowRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void cancelDataFlow(DataFlowRpc.CancelDataFlowRequest inRequest,
                                   StreamObserver<DataFlowRpc.CancelDataFlowResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received cancel data flow request {}",
                                       getServiceDescription(),
                                       inRequest);
                DataFlowID dataFlowId = DataFlowRpcUtil.getDataFlowId(inRequest.getDataFlowId());
                DataFlowRpc.CancelDataFlowResponse.Builder responseBuilder = DataFlowRpc.CancelDataFlowResponse.newBuilder();
                moduleManager.cancel(dataFlowId);
                DataFlowRpc.CancelDataFlowResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       getServiceDescription(),
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
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#getDataFlowInfo(org.marketcetera.dataflow.rpc.DataFlowRpc.GetDataFlowInfoRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getDataFlowInfo(DataFlowRpc.GetDataFlowInfoRequest inRequest,
                                    StreamObserver<DataFlowRpc.GetDataFlowInfoResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received get data flow info request {}",
                                       sessionHolder,
                                       inRequest);
                DataFlowID dataFlowId = DataFlowRpcUtil.getDataFlowId(inRequest.getDataFlowId());
                DataFlowInfo dataFlowInfo = moduleManager.getDataFlowInfo(dataFlowId);
                DataFlowRpc.GetDataFlowInfoResponse.Builder responseBuilder = DataFlowRpc.GetDataFlowInfoResponse.newBuilder();
                responseBuilder.setDataFlowInfo(DataFlowRpcUtil.getRpcDataFlowInfo(dataFlowInfo));
                DataFlowRpc.GetDataFlowInfoResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       sessionHolder,
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                inResponseObserver.onError(e);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#getDataFlows(org.marketcetera.dataflow.rpc.DataFlowRpc.GetDataFlowsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getDataFlows(DataFlowRpc.GetDataFlowsRequest inRequest,
                                 StreamObserver<DataFlowRpc.GetDataFlowsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received get data flows request {}",
                                       sessionHolder,
                                       inRequest);
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                List<DataFlowID> dataFlows = moduleManager.getDataFlows(true); // TODO include this in the request
                List<DataFlowID> dataFlowsPage = PageResponse.getPage(dataFlows,
                                                                      pageRequest.getPageNumber()+1,
                                                                      pageRequest.getPageSize());
                DataFlowRpc.GetDataFlowsResponse.Builder responseBuilder = DataFlowRpc.GetDataFlowsResponse.newBuilder();
                dataFlowsPage.forEach(value->responseBuilder.addDataFlowIds(DataFlowRpcUtil.getRpcDataFlowId(value)));
                CollectionPageResponse<DataFlowID> fauxPage = new CollectionPageResponse<>();
                fauxPage.setElements(dataFlowsPage);
                fauxPage.setHasContent(!dataFlowsPage.isEmpty());
                fauxPage.setPageMaxSize(pageRequest.getPageSize());
                fauxPage.setPageNumber(pageRequest.getPageNumber());
                fauxPage.setPageSize(Math.min(pageRequest.getPageSize(),
                                              dataFlowsPage.size()));
                // TODO not sorting!
                fauxPage.setSortOrder(pageRequest.getSortOrder());
                int totalSize = dataFlows.size();
                fauxPage.setTotalPages(PageResponse.getNumberOfPages(pageRequest,
                                                                     totalSize));
                fauxPage.setTotalSize(totalSize);
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              fauxPage));
                DataFlowRpc.GetDataFlowsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       sessionHolder,
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                inResponseObserver.onError(e);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#getDataFlowHistory(org.marketcetera.dataflow.rpc.DataFlowRpc.GetDataFlowHistoryRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getDataFlowHistory(DataFlowRpc.GetDataFlowHistoryRequest inRequest,
                                       StreamObserver<DataFlowRpc.GetDataFlowHistoryResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received get data flow history request {}",
                                       sessionHolder,
                                       inRequest);
                PageRequest pageRequest = inRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inRequest.getPageRequest()):PageRequest.ALL;
                List<DataFlowInfo> dataFlowHistory = moduleManager.getDataFlowHistory();
                List<DataFlowInfo> dataFlowHistoryPage = PageResponse.getPage(dataFlowHistory,
                                                                              pageRequest.getPageNumber()+1,
                                                                              pageRequest.getPageSize());
                DataFlowRpc.GetDataFlowHistoryResponse.Builder responseBuilder = DataFlowRpc.GetDataFlowHistoryResponse.newBuilder();
                dataFlowHistoryPage.forEach(value->responseBuilder.addDataFlowInfos(DataFlowRpcUtil.getRpcDataFlowInfo(value)));
                CollectionPageResponse<DataFlowInfo> fauxPage = new CollectionPageResponse<>();
                fauxPage.setElements(dataFlowHistoryPage);
                fauxPage.setHasContent(!dataFlowHistoryPage.isEmpty());
                fauxPage.setPageMaxSize(pageRequest.getPageSize());
                fauxPage.setPageNumber(pageRequest.getPageNumber());
                fauxPage.setPageSize(Math.min(pageRequest.getPageSize(),
                                              dataFlowHistoryPage.size()));
                // TODO not sorting!
                fauxPage.setSortOrder(pageRequest.getSortOrder());
                int totalSize = dataFlowHistory.size();
                fauxPage.setTotalPages(PageResponse.getNumberOfPages(pageRequest,
                                                                     totalSize));
                fauxPage.setTotalSize(totalSize);
                DataFlowRpc.GetDataFlowHistoryResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} returning {}",
                                       sessionHolder,
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(DataFlowRpcService.this,
                                      e);
                inResponseObserver.onError(e);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#addDataReceiver(org.marketcetera.dataflow.rpc.DataFlowRpc.AddDataReceiverRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addDataReceiver(DataFlowRpc.AddDataReceiverRequest inRequest,
                                    StreamObserver<DataFlowRpc.DataReceiverResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "Received add data receiver request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> dataReceiverProxy = receiverProxiesById.getIfPresent(listenerId);
                if(dataReceiverProxy == null) {
                    dataReceiverProxy = new DataReceiverListenerProxy(listenerId,
                                                                      inResponseObserver);
                    receiverProxiesById.put(dataReceiverProxy.getId(),
                                            dataReceiverProxy);
                    dataFlowService.addDataReceiver((DataReceiver)dataReceiverProxy);
                }
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#removeDataReceiver(org.marketcetera.dataflow.rpc.DataFlowRpc.RemoveDataReceiverRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeDataReceiver(DataFlowRpc.RemoveDataReceiverRequest inRequest,
                                       StreamObserver<DataFlowRpc.RemoveDataReceiverResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "Received remove data receiver request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> dataReceiverProxy = receiverProxiesById.getIfPresent(listenerId);
                receiverProxiesById.invalidate(listenerId);
                if(dataReceiverProxy != null) {
                    dataFlowService.removeDataReceiver((DataReceiver)dataReceiverProxy);
                    dataReceiverProxy.close();
                }
                DataFlowRpc.RemoveDataReceiverResponse.Builder responseBuilder = DataFlowRpc.RemoveDataReceiverResponse.newBuilder();
                DataFlowRpc.RemoveDataReceiverResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
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
     * Wraps a {@link DataReceiver} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DataReceiverListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<DataFlowRpc.DataReceiverResponse>
            implements DataReceiver
    {
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.client.DataReceiver#receiveData(java.lang.Object)
         */
        @Override
        public void receiveData(Object inObject)
        {
            DataFlowRpcUtil.setData(inObject,
                                    responseBuilder);
            DataFlowRpc.DataReceiverResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(DataFlowRpcService.class,
                                   "{} received data flow object {}, sending {}",
                                   getId(),
                                   inObject,
                                   response);
            // TODO does the user have permissions (including supervisor) to view this object?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new DataReceiverListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;DataFlowRpc.DataReceiverResponse&gt;</code> value
         */
        private DataReceiverListenerProxy(String inId,
                                          StreamObserver<DataFlowRpc.DataReceiverResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final DataFlowRpc.DataReceiverResponse.Builder responseBuilder = DataFlowRpc.DataReceiverResponse.newBuilder();
    }
    /**
     * holds data broadcasters
     */
    @Autowired(required=false)
    private Collection<DataBroadcaster> dataBroadcasters = Lists.newArrayList();
    /**
     * provides access to data flow services
     */
    @Autowired
    private DataFlowService dataFlowService;
    /**
     * provides access to module services
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Data Flow RPC Service";
    /**
     * holds data listeners by id
     */
    private final Cache<String,BaseRpcUtil.AbstractServerListenerProxy<?>> receiverProxiesById = CacheBuilder.newBuilder().build();
}

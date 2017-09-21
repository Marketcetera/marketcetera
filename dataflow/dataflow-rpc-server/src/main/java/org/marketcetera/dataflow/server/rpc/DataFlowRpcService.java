package org.marketcetera.dataflow.server.rpc;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.dataflow.rpc.DataFlowRpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase;
import org.marketcetera.dataflow.rpc.DataFlowRpcUtil;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

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
//            try {
//                validateAndReturnSession(inRequest.getSessionId());
//                Object data;
//                try {
//                    data = ((XmlValue)unmarshall(inRequest.getPayload())).getValue();
//                } catch (JAXBException e) {
//                    throw new ServiceException(e);
//                }
//                SLF4JLoggerProxy.debug(this,
//                                       "{} received sendData for {} request",
//                                       getServiceDescription(),
//                                       data);
//                DataFlowRpc.SendDataResponse.Builder responseBuilder = DataFlowRpc.SendDataResponse.newBuilder();
//                for(DataBroadcaster dataBroadcaster : dataBroadcasters) {
//                    try {
//                        dataBroadcaster.receiveData(data);
//                    } catch (Exception e) {
//                        PlatformServices.handleException(DataFlowRpcService.this,
//                                                         "Error broadcasting " + data,
//                                                         e);
//                    }
//                }
//                DataFlowRpc.SendDataResponse response = responseBuilder.build();
//                SLF4JLoggerProxy.debug(this,
//                                       "{} returning from sendData call",
//                                       getServiceDescription());
//                inResponseObserver.onNext(response);
//                inResponseObserver.onCompleted();
//            } catch (Exception e) {
//                if(e instanceof StatusRuntimeException) {
//                    throw (StatusRuntimeException)e;
//                }
//                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
//            }
            throw new UnsupportedOperationException();
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
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} received create data flow request {}",
                                       getServiceDescription(),
                                       inRequest);
                List<DataRequest> dataRequestBuilder = Lists.newArrayList();
                for(DataFlowRpc.DataRequest rpcDataRequest : inRequest.getDataRequestsList()) {
                    dataRequestBuilder.add(DataFlowRpcUtil.getDataRequest(rpcDataRequest));
                }
                boolean appendToSink = inRequest.getAppendDataSink();
                SLF4JLoggerProxy.trace(DataFlowRpcService.this,
                                       "{} issuing data request {}",
                                       getServiceDescription(),
                                       dataRequestBuilder);
                DataFlowID dataFlowId = moduleManager.createDataFlow(dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]),
                                                                     appendToSink);
                DataFlowRpc.CreateDataFlowResponse.Builder responseBuilder = DataFlowRpc.CreateDataFlowResponse.newBuilder();
                responseBuilder.setDataFlowId(DataFlowRpcUtil.getRpcDataFlowId(dataFlowId));
                DataFlowRpc.CreateDataFlowResponse response = responseBuilder.build();
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
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#getDataFlows(org.marketcetera.dataflow.rpc.DataFlowRpc.GetDataFlowsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getDataFlows(DataFlowRpc.GetDataFlowsRequest inRequest,
                                 StreamObserver<DataFlowRpc.GetDataFlowsResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#getDataFlowHistory(org.marketcetera.dataflow.rpc.DataFlowRpc.GetDataFlowHistoryRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getDataFlowHistory(DataFlowRpc.GetDataFlowHistoryRequest inRequest,
                                       StreamObserver<DataFlowRpc.GetDataFlowHistoryResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#addDataReceiver(org.marketcetera.dataflow.rpc.DataFlowRpc.AddDataReceiverRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addDataReceiver(DataFlowRpc.AddDataReceiverRequest inRequest,
                                    StreamObserver<DataFlowRpc.DataReceiverResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
        }
        /* (non-Javadoc)
         * @see org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase#removeDataReceiver(org.marketcetera.dataflow.rpc.DataFlowRpc.RemoveDataReceiverRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeDataReceiver(DataFlowRpc.RemoveDataReceiverRequest inRequest,
                                       StreamObserver<DataFlowRpc.RemoveDataReceiverResponse> inResponseObserver)
        {
            throw new UnsupportedOperationException(); // TODO
        }
    }
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
}

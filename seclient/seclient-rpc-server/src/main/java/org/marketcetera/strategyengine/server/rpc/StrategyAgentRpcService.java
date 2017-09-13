package org.marketcetera.strategyengine.server.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.seclient.rpc.SEClientRpc;
import org.marketcetera.seclient.rpc.SEClientRpc.DeleteRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.DeleteResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.InstancesRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.InstancesResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.ModuleInfoRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.ModuleInfoResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.ProvidersRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.ProvidersResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.SendDataRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.SendDataResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.StartRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.StartResponse;
import org.marketcetera.seclient.rpc.SEClientRpc.StopRequest;
import org.marketcetera.seclient.rpc.SEClientRpc.StopResponse;
import org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc;
import org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SEClientServiceRpcImplBase;
import org.marketcetera.strategyengine.client.DataBroadcaster;
import org.marketcetera.strategyengine.client.XmlValue;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.protobuf.ServiceException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides SE RPC server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,SEClientServiceRpcGrpc.SEClientServiceRpcImplBase>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#start()
     */
    @Override
    public void start()
            throws Exception
    {
        service = new Service();
        synchronized(contextLock) {
            context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        }
        super.start();
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
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
    protected SEClientServiceRpcImplBase getService()
    {
        return service;
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private String marshal(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(contextLock) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings({ "unchecked" })
    private <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * Strategy Agent RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends SEClientServiceRpcGrpc.SEClientServiceRpcImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doLogin(inRequest,
                                                       inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doLogout(inRequest,
                                                        inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doHeartbeat(inRequest,
                                                           inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#getProviders(org.marketcetera.saclient.rpc.SEClientRpc.ProvidersRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getProviders(ProvidersRequest inRequest,
                                 StreamObserver<ProvidersResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.debug(this,
                                       "{} received getProviders request",
                                       getServiceDescription());
                List<ModuleURN> providers = moduleManager.getProviders();
                SEClientRpc.ProvidersResponse.Builder responseBuilder = SEClientRpc.ProvidersResponse.newBuilder();
                if(providers != null) {
                    for(ModuleURN provider : providers) {
                        responseBuilder.addProvider(SEClientRpc.ModuleURN.newBuilder().setValue(provider.getValue()).build());
                    }
                }
                SEClientRpc.ProvidersResponse response = responseBuilder.build();
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
        public void getInstances(InstancesRequest inRequest,
                                 StreamObserver<InstancesResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN provider = new ModuleURN(inRequest.getProvider().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received getInstances for {} request",
                                       getServiceDescription(),
                                       provider);
                List<ModuleURN> instances = moduleManager.getModuleInstances(provider);
                SEClientRpc.InstancesResponse.Builder responseBuilder = SEClientRpc.InstancesResponse.newBuilder();
                if(instances != null) {
                    for(ModuleURN instance : instances) {
                        responseBuilder.addInstance(SEClientRpc.ModuleURN.newBuilder().setValue(instance.getValue()).build());
                    }
                }
                SEClientRpc.InstancesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       instances);
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
        public void getModuleInfo(ModuleInfoRequest inRequest,
                                  StreamObserver<ModuleInfoResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received getModuleInfo for {} request",
                                       getServiceDescription(),
                                       instance);
                ModuleInfo info = moduleManager.getModuleInfo(instance);
                SEClientRpc.ModuleInfoResponse.Builder responseBuilder = SEClientRpc.ModuleInfoResponse.newBuilder();
                if(info != null) {
                    try {
                        responseBuilder.setInfo(SEClientRpc.ModuleInfo.newBuilder().setPayload(marshal(info)));
                    } catch (JAXBException e) {
                        throw new ServiceException(e);
                    }
                }
                SEClientRpc.ModuleInfoResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#start(org.marketcetera.saclient.rpc.SEClientRpc.StartRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void start(StartRequest inRequest,
                          StreamObserver<StartResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received start for {} request",
                                       getServiceDescription(),
                                       instance);
                SEClientRpc.StartResponse.Builder responseBuilder = SEClientRpc.StartResponse.newBuilder();
                moduleManager.start(instance);
                SEClientRpc.StartResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
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
         * @see org.marketcetera.seclient.rpc.SEClientServiceRpcGrpc.SAClientServiceRpcImplBase#stop(org.marketcetera.saclient.rpc.SEClientRpc.StopRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void stop(StopRequest inRequest,
                         StreamObserver<StopResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received stop for {} request",
                                       getServiceDescription(),
                                       instance);
                SEClientRpc.StopResponse.Builder responseBuilder = SEClientRpc.StopResponse.newBuilder();
                moduleManager.stop(instance);
                SEClientRpc.StopResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
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
        public void delete(DeleteRequest inRequest,
                           StreamObserver<DeleteResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received delete for {} request",
                                       getServiceDescription(),
                                       instance);
                SEClientRpc.DeleteResponse.Builder responseBuilder = SEClientRpc.DeleteResponse.newBuilder();
                moduleManager.deleteModule(instance);
                SEClientRpc.DeleteResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
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
        public void sendData(SendDataRequest inRequest,
                             StreamObserver<SendDataResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                Object data;
                try {
                    data = ((XmlValue)unmarshall(inRequest.getPayload())).getValue();
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                SLF4JLoggerProxy.debug(this,
                                       "{} received sendData for {} request",
                                       getServiceDescription(),
                                       data);
                SEClientRpc.SendDataResponse.Builder responseBuilder = SEClientRpc.SendDataResponse.newBuilder();
                for(DataBroadcaster dataBroadcaster : dataBroadcasters) {
                    try {
                        dataBroadcaster.receiveData(data);
                    } catch (Exception e) {
                        PlatformServices.handleException(StrategyAgentRpcService.this,
                                                         "Error broadcasting " + data,
                                                         e);
                    }
                }
                SEClientRpc.SendDataResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
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
    }
    /**
     * provides context classes for marshalling/unmarshalling, may be <code>null</code>
     */
    private ContextClassProvider contextClassProvider;
    /**
     * guards access to JAXB context objects
     */
    private final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private Unmarshaller unmarshaller;
    /**
     * provides access to module services
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * holds data broadcasters
     */
    @Autowired(required=false)
    private Collection<DataBroadcaster> dataBroadcasters = Lists.newArrayList();
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "StrategyEngine RPC Service";
}

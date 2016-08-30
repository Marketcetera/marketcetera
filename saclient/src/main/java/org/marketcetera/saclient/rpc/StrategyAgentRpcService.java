package org.marketcetera.saclient.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.rpc.SAClientRpc.CreateStrategyRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.CreateStrategyResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.DeleteRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.DeleteResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.GetPropertiesRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.GetPropertiesResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.InstancesRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.InstancesResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.ModuleInfoRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.ModuleInfoResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.ProvidersRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.ProvidersResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.SendDataRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.SendDataResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.SetPropertiesRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.SetPropertiesResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.StartRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.StartResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.StopRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.StopResponse;
import org.marketcetera.saclient.rpc.SAClientRpc.StrategyCreateParmsRequest;
import org.marketcetera.saclient.rpc.SAClientRpc.StrategyCreateParmsResponse;
import org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.google.protobuf.ServiceException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,SAClientServiceRpcGrpc.SAClientServiceRpcImplBase>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#start()
     */
    @Override
    public void start()
            throws Exception
    {
        Validate.notNull(serviceAdapter,
                         "Strategy Agent service required");
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
    /**
     * Get the serviceAdapter value.
     *
     * @return a <code>SAServiceAdapter</code> value
     */
    public SAServiceAdapter getServiceAdapter()
    {
        return serviceAdapter;
    }
    /**
     * Sets the serviceAdapter value.
     *
     * @param a <code>SAServiceAdapter</code> value
     */
    public void setServiceAdapter(SAServiceAdapter inServiceAdapter)
    {
        serviceAdapter = inServiceAdapter;
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
    protected SAClientServiceRpcImplBase getService()
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
            extends SAClientServiceRpcGrpc.SAClientServiceRpcImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doLogin(inRequest,
                                                       inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doLogout(inRequest,
                                                        inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            StrategyAgentRpcService.this.doHeartbeat(inRequest,
                                                           inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#getProviders(org.marketcetera.saclient.rpc.SAClientRpc.ProvidersRequest, io.grpc.stub.StreamObserver)
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
                List<ModuleURN> providers = serviceAdapter.getProviders();
                SAClientRpc.ProvidersResponse.Builder responseBuilder = SAClientRpc.ProvidersResponse.newBuilder();
                if(providers != null) {
                    for(ModuleURN provider : providers) {
                        responseBuilder.addProvider(SAClientRpc.ModuleURN.newBuilder().setValue(provider.getValue()).build());
                    }
                }
                SAClientRpc.ProvidersResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#getInstances(org.marketcetera.saclient.rpc.SAClientRpc.InstancesRequest, io.grpc.stub.StreamObserver)
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
                List<ModuleURN> instances = serviceAdapter.getInstances(provider);
                SAClientRpc.InstancesResponse.Builder responseBuilder = SAClientRpc.InstancesResponse.newBuilder();
                if(instances != null) {
                    for(ModuleURN instance : instances) {
                        responseBuilder.addInstance(SAClientRpc.ModuleURN.newBuilder().setValue(instance.getValue()).build());
                    }
                }
                SAClientRpc.InstancesResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#getModuleInfo(org.marketcetera.saclient.rpc.SAClientRpc.ModuleInfoRequest, io.grpc.stub.StreamObserver)
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
                ModuleInfo info = serviceAdapter.getModuleInfo(instance);
                SAClientRpc.ModuleInfoResponse.Builder responseBuilder = SAClientRpc.ModuleInfoResponse.newBuilder();
                if(info != null) {
                    try {
                        responseBuilder.setInfo(SAClientRpc.ModuleInfo.newBuilder().setPayload(marshal(info)));
                    } catch (JAXBException e) {
                        throw new ServiceException(e);
                    }
                }
                SAClientRpc.ModuleInfoResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#start(org.marketcetera.saclient.rpc.SAClientRpc.StartRequest, io.grpc.stub.StreamObserver)
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
                SAClientRpc.StartResponse.Builder responseBuilder = SAClientRpc.StartResponse.newBuilder();
                serviceAdapter.start(instance);
                SAClientRpc.StartResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#stop(org.marketcetera.saclient.rpc.SAClientRpc.StopRequest, io.grpc.stub.StreamObserver)
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
                SAClientRpc.StopResponse.Builder responseBuilder = SAClientRpc.StopResponse.newBuilder();
                serviceAdapter.stop(instance);
                SAClientRpc.StopResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#delete(org.marketcetera.saclient.rpc.SAClientRpc.DeleteRequest, io.grpc.stub.StreamObserver)
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
                SAClientRpc.DeleteResponse.Builder responseBuilder = SAClientRpc.DeleteResponse.newBuilder();
                serviceAdapter.delete(instance);
                SAClientRpc.DeleteResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#getProperties(org.marketcetera.saclient.rpc.SAClientRpc.GetPropertiesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getProperties(GetPropertiesRequest inRequest,
                                  StreamObserver<GetPropertiesResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received getProperties for {} request",
                                       getServiceDescription(),
                                       instance);
                Map<String,Object> properties = serviceAdapter.getProperties(instance);
                SAClientRpc.GetPropertiesResponse.Builder responseBuilder = SAClientRpc.GetPropertiesResponse.newBuilder();
                SAClientRpc.Properties.Builder propertiesBuilder = SAClientRpc.Properties.newBuilder();
                if(properties != null) {
                    for(Map.Entry<String,Object> entry : properties.entrySet()) {
                        SAClientRpc.Entry.Builder entryBuilder = SAClientRpc.Entry.newBuilder();
                        entryBuilder.setKey(entry.getKey());
                        // note that this assumes that all values are marshallable
                        try {
                            entryBuilder.setValue(marshal(new XmlValue(entry.getValue())));
                        } catch (JAXBException e) {
                            throw new ServiceException(e);
                        }
                        propertiesBuilder.addEntry(entryBuilder.build());
                    }
                }
                responseBuilder.setProperties(propertiesBuilder.build());
                SAClientRpc.GetPropertiesResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#setProperties(org.marketcetera.saclient.rpc.SAClientRpc.SetPropertiesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void setProperties(SetPropertiesRequest inRequest,
                                  StreamObserver<SetPropertiesResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                Map<String,Object> properties = Maps.newHashMap();
                for(SAClientRpc.Entry entry : inRequest.getProperties().getEntryList()) {
                    try {
                        // note that this assumes that all values are marshallable
                        properties.put(entry.getKey(),
                                       ((XmlValue)unmarshall(entry.getValue())).getValue());
                    } catch (JAXBException e) {
                        throw new ServiceException(e);
                    }
                }
                SLF4JLoggerProxy.debug(this,
                                       "{} received setProperties to {} for {} request",
                                       getServiceDescription(),
                                       properties,
                                       instance);
                properties = serviceAdapter.setProperties(instance,
                                                          properties);
                SAClientRpc.SetPropertiesResponse.Builder responseBuilder = SAClientRpc.SetPropertiesResponse.newBuilder();
                SAClientRpc.Properties.Builder propertiesBuilder = SAClientRpc.Properties.newBuilder();
                if(properties != null) {
                    for(Map.Entry<String,Object> entry : properties.entrySet()) {
                        SAClientRpc.Entry.Builder entryBuilder = SAClientRpc.Entry.newBuilder();
                        entryBuilder.setKey(entry.getKey());
                        // note that this assumes that all values are marshallable
                        try {
                            entryBuilder.setValue(marshal(new XmlValue(entry.getValue())));
                        } catch (JAXBException e) {
                            throw new ServiceException(e);
                        }
                        propertiesBuilder.addEntry(entryBuilder.build());
                    }
                }
                responseBuilder.setProperties(propertiesBuilder.build());
                SAClientRpc.SetPropertiesResponse response = responseBuilder.build();
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#createStrategy(org.marketcetera.saclient.rpc.SAClientRpc.CreateStrategyRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createStrategy(CreateStrategyRequest inRequest,
                                   StreamObserver<CreateStrategyResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                CreateStrategyParameters parameters;
                try {
                    parameters = unmarshall(inRequest.getCreateStrategyParameters().getPayload());
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                SLF4JLoggerProxy.debug(this,
                                       "{} received createStrategy with {} request",
                                       getServiceDescription(),
                                       parameters);
                ModuleURN instance = serviceAdapter.createStrategy(parameters);
                SAClientRpc.CreateStrategyResponse.Builder responseBuilder = SAClientRpc.CreateStrategyResponse.newBuilder();
                responseBuilder.setInstance(SAClientRpc.ModuleURN.newBuilder().setValue(instance.getValue()));
                SAClientRpc.CreateStrategyResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       instance);
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#getStrategyCreateParms(org.marketcetera.saclient.rpc.SAClientRpc.StrategyCreateParmsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getStrategyCreateParms(StrategyCreateParmsRequest inRequest,
                                           StreamObserver<StrategyCreateParmsResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
                SLF4JLoggerProxy.debug(this,
                                       "{} received createStrategy with {} request",
                                       getServiceDescription(),
                                       instance);
                CreateStrategyParameters parameters = serviceAdapter.getStrategyCreateParms(instance);
                SAClientRpc.StrategyCreateParmsResponse.Builder responseBuilder = SAClientRpc.StrategyCreateParmsResponse.newBuilder();
                try {
                    responseBuilder.setCreateStrategyParameters(SAClientRpc.CreateStrategyParameters.newBuilder().setPayload(marshal(parameters)));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                SAClientRpc.StrategyCreateParmsResponse response = responseBuilder.build();
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {}",
                                       getServiceDescription(),
                                       parameters);
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
         * @see org.marketcetera.saclient.rpc.SAClientServiceRpcGrpc.SAClientServiceRpcImplBase#sendData(org.marketcetera.saclient.rpc.SAClientRpc.SendDataRequest, io.grpc.stub.StreamObserver)
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
                SAClientRpc.SendDataResponse.Builder responseBuilder = SAClientRpc.SendDataResponse.newBuilder();
                serviceAdapter.sendData(data);
                SAClientRpc.SendDataResponse response = responseBuilder.build();
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
     * provides access to strategy agent services
     */
    @Autowired
    private SAServiceAdapter serviceAdapter;
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "StrategyAgent RPC Service";
}

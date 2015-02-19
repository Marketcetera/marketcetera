package org.marketcetera.saclient.rpc;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.rpc.RpcSAClient.CreateStrategyRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.CreateStrategyResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.DeleteRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.DeleteResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.GetPropertiesRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.GetPropertiesResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.HeartbeatRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.HeartbeatResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.InstancesRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.InstancesResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.LoginRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.LoginResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.LogoutRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.LogoutResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.ModuleInfoRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.ModuleInfoResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.ProvidersRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.ProvidersResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService;
import org.marketcetera.saclient.rpc.RpcSAClient.SendDataRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.SendDataResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.SetPropertiesRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.SetPropertiesResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.StartRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.StartResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.StopRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.StopResponse;
import org.marketcetera.saclient.rpc.RpcSAClient.StrategyCreateParmsRequest;
import org.marketcetera.saclient.rpc.RpcSAClient.StrategyCreateParmsResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.rpc.RpcCredentials;
import org.marketcetera.util.rpc.RpcServerServices;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.tags.SessionId;

import com.google.common.collect.Maps;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/* $License$ */

/**
 * Provides server-side RPC services for <code>SAClient</code> connections.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class SAClientRpcService<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,RpcSAClientService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#login(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.LoginRequest)
     */
    @Override
    public LoginResponse login(RpcController inController,
                               LoginRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received authentication request for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               inRequest.getUsername());
        try {
            SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
                                                                          inRequest.getPassword(),
                                                                          inRequest.getAppId(),
                                                                          inRequest.getClientId(),
                                                                          inRequest.getVersionId(),
                                                                          new Locale(inRequest.getLocale().getLanguage(),
                                                                                     inRequest.getLocale().getCountry(),
                                                                                     inRequest.getLocale().getVariant())));
            return LoginResponse.newBuilder().setSessionId(sessionId.getValue()).build();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#logout(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.LogoutRequest)
     */
    @Override
    public LogoutResponse logout(RpcController inController,
                                 LogoutRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received logout request for {}", //$NON-NLS-1$
                               DESCRIPTION,
                               inRequest.getSessionId());
        serverServices.logout(inRequest.getSessionId());
        return LogoutResponse.newBuilder().setStatus(true).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#heartbeat(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.HeartbeatRequest)
     */
    @Override
    public HeartbeatResponse heartbeat(RpcController inController,
                                       HeartbeatRequest inRequest)
            throws ServiceException
    {
        return RpcSAClient.HeartbeatResponse.newBuilder().setId(inRequest.getId()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#getProviders(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.ProvidersRequest)
     */
    @Override
    public ProvidersResponse getProviders(RpcController inController,
                                          ProvidersRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        SLF4JLoggerProxy.debug(this,
                               "{} received getProviders request",
                               DESCRIPTION);
        List<ModuleURN> providers = serviceAdapter.getProviders();
        RpcSAClient.ProvidersResponse.Builder responseBuilder = RpcSAClient.ProvidersResponse.newBuilder();
        if(providers != null) {
            for(ModuleURN provider : providers) {
                responseBuilder.addProvider(RpcSAClient.ModuleURN.newBuilder().setValue(provider.getValue()).build());
            }
        }
        RpcSAClient.ProvidersResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               providers);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#getInstances(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.InstancesRequest)
     */
    @Override
    public InstancesResponse getInstances(RpcController inController,
                                          InstancesRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN provider = new ModuleURN(inRequest.getProvider().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received getInstances for {} request",
                               DESCRIPTION,
                               provider);
        List<ModuleURN> instances = serviceAdapter.getInstances(provider);
        RpcSAClient.InstancesResponse.Builder responseBuilder = RpcSAClient.InstancesResponse.newBuilder();
        if(instances != null) {
            for(ModuleURN instance : instances) {
                responseBuilder.addInstance(RpcSAClient.ModuleURN.newBuilder().setValue(instance.getValue()).build());
            }
        }
        RpcSAClient.InstancesResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               instances);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#getModuleInfo(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.ModuleInfoRequest)
     */
    @Override
    public ModuleInfoResponse getModuleInfo(RpcController inController,
                                            ModuleInfoRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received getModuleInfo for {} request",
                               DESCRIPTION,
                               instance);
        ModuleInfo info = serviceAdapter.getModuleInfo(instance);
        RpcSAClient.ModuleInfoResponse.Builder responseBuilder = RpcSAClient.ModuleInfoResponse.newBuilder();
        if(info != null) {
            try {
                responseBuilder.setInfo(RpcSAClient.ModuleInfo.newBuilder().setPayload(serverServices.marshal(info)));
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
        }
        RpcSAClient.ModuleInfoResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               info);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#start(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.StartRequest)
     */
    @Override
    public StartResponse start(RpcController inController,
                               StartRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received start for {} request",
                               DESCRIPTION,
                               instance);
        RpcSAClient.StartResponse.Builder responseBuilder = RpcSAClient.StartResponse.newBuilder();
        serviceAdapter.start(instance);
        SLF4JLoggerProxy.debug(this,
                               "{} returning from start call",
                               DESCRIPTION);
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#stop(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.StopRequest)
     */
    @Override
    public StopResponse stop(RpcController inController,
                             StopRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received stop for {} request",
                               DESCRIPTION,
                               instance);
        RpcSAClient.StopResponse.Builder responseBuilder = RpcSAClient.StopResponse.newBuilder();
        serviceAdapter.stop(instance);
        SLF4JLoggerProxy.debug(this,
                               "{} returning from stop call",
                               DESCRIPTION);
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#delete(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.DeleteRequest)
     */
    @Override
    public DeleteResponse delete(RpcController inController,
                                 DeleteRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received delete for {} request",
                               DESCRIPTION,
                               instance);
        RpcSAClient.DeleteResponse.Builder responseBuilder = RpcSAClient.DeleteResponse.newBuilder();
        serviceAdapter.delete(instance);
        SLF4JLoggerProxy.debug(this,
                               "{} returning from delete call",
                               DESCRIPTION);
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#getProperties(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.GetPropertiesRequest)
     */
    @Override
    public GetPropertiesResponse getProperties(RpcController inController,
                                               GetPropertiesRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received getProperties for {} request",
                               DESCRIPTION,
                               instance);
        Map<String,Object> properties = serviceAdapter.getProperties(instance);
        RpcSAClient.GetPropertiesResponse.Builder responseBuilder = RpcSAClient.GetPropertiesResponse.newBuilder();
        RpcSAClient.Properties.Builder propertiesBuilder = RpcSAClient.Properties.newBuilder();
        if(properties != null) {
            for(Map.Entry<String,Object> entry : properties.entrySet()) {
                RpcSAClient.Entry.Builder entryBuilder = RpcSAClient.Entry.newBuilder();
                entryBuilder.setKey(entry.getKey());
                // note that this assumes that all values are marshallable
                try {
                    entryBuilder.setValue(serverServices.marshal(new XmlValue(entry.getValue())));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                propertiesBuilder.addEntry(entryBuilder.build());
            }
        }
        responseBuilder.setProperties(propertiesBuilder.build());
        RpcSAClient.GetPropertiesResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               properties);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#setProperties(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.SetPropertiesRequest)
     */
    @Override
    public SetPropertiesResponse setProperties(RpcController inController,
                                               SetPropertiesRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        Map<String,Object> properties = Maps.newHashMap();
        for(RpcSAClient.Entry entry : inRequest.getProperties().getEntryList()) {
            try {
                // note that this assumes that all values are marshallable
                properties.put(entry.getKey(),
                               ((XmlValue)serverServices.unmarshall(entry.getValue())).getValue());
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "{} received setProperties to {} for {} request",
                               DESCRIPTION,
                               properties,
                               instance);
        properties = serviceAdapter.setProperties(instance,
                                                  properties);
        RpcSAClient.SetPropertiesResponse.Builder responseBuilder = RpcSAClient.SetPropertiesResponse.newBuilder();
        RpcSAClient.Properties.Builder propertiesBuilder = RpcSAClient.Properties.newBuilder();
        if(properties != null) {
            for(Map.Entry<String,Object> entry : properties.entrySet()) {
                RpcSAClient.Entry.Builder entryBuilder = RpcSAClient.Entry.newBuilder();
                entryBuilder.setKey(entry.getKey());
                // note that this assumes that all values are marshallable
                try {
                    entryBuilder.setValue(serverServices.marshal(new XmlValue(entry.getValue())));
                } catch (JAXBException e) {
                    throw new ServiceException(e);
                }
                propertiesBuilder.addEntry(entryBuilder.build());
            }
        }
        responseBuilder.setProperties(propertiesBuilder.build());
        RpcSAClient.SetPropertiesResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               properties);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#createStrategy(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.CreateStrategyRequest)
     */
    @Override
    public CreateStrategyResponse createStrategy(RpcController inController,
                                                 CreateStrategyRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        CreateStrategyParameters parameters;
        try {
            parameters = serverServices.unmarshall(inRequest.getCreateStrategyParameters().getPayload());
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }
        SLF4JLoggerProxy.debug(this,
                               "{} received createStrategy with {} request",
                               DESCRIPTION,
                               parameters);
        ModuleURN instance = serviceAdapter.createStrategy(parameters);
        RpcSAClient.CreateStrategyResponse.Builder responseBuilder = RpcSAClient.CreateStrategyResponse.newBuilder();
        responseBuilder.setInstance(RpcSAClient.ModuleURN.newBuilder().setValue(instance.getValue()));
        RpcSAClient.CreateStrategyResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               instance);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#getStrategyCreateParms(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.StrategyCreateParmsRequest)
     */
    @Override
    public StrategyCreateParmsResponse getStrategyCreateParms(RpcController inController,
                                                              StrategyCreateParmsRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        ModuleURN instance = new ModuleURN(inRequest.getInstance().getValue());
        SLF4JLoggerProxy.debug(this,
                               "{} received createStrategy with {} request",
                               DESCRIPTION,
                               instance);
        CreateStrategyParameters parameters = serviceAdapter.getStrategyCreateParms(instance);
        RpcSAClient.StrategyCreateParmsResponse.Builder responseBuilder = RpcSAClient.StrategyCreateParmsResponse.newBuilder();
        try {
            responseBuilder.setCreateStrategyParameters(RpcSAClient.CreateStrategyParameters.newBuilder().setPayload(serverServices.marshal(parameters)));
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }
        RpcSAClient.StrategyCreateParmsResponse response = responseBuilder.build();
        SLF4JLoggerProxy.debug(this,
                               "{} returning {}",
                               DESCRIPTION,
                               parameters);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.RpcSAClient.RpcSAClientService.BlockingInterface#sendData(com.google.protobuf.RpcController, org.marketcetera.saclient.rpc.RpcSAClient.SendDataRequest)
     */
    @Override
    public SendDataResponse sendData(RpcController inController,
                                     SendDataRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        Object data;
        try {
            data = ((XmlValue)serverServices.unmarshall(inRequest.getPayload())).getValue();
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }
        SLF4JLoggerProxy.debug(this,
                               "{} received sendData for {} request",
                               DESCRIPTION,
                               data);
        RpcSAClient.SendDataResponse.Builder responseBuilder = RpcSAClient.SendDataResponse.newBuilder();
        serviceAdapter.sendData(data);
        SLF4JLoggerProxy.debug(this,
                               "{} returning from sendData call",
                               DESCRIPTION);
        return responseBuilder.build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#getDescription()
     */
    @Override
    public String getDescription()
    {
        return DESCRIPTION;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#generateService()
     */
    @Override
    public BlockingService generateService()
    {
        return RpcSAClientService.newReflectiveBlockingService(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#setRpcServerServices(org.marketcetera.util.rpc.RpcServerServices)
     */
    @Override
    public void setRpcServerServices(RpcServerServices<SessionClazz> inServerServices)
    {
        serverServices = inServerServices;
    }
    /**
     * Sets the service adapter value.
     *
     * @param inServiceAdapter an <code>SAServiceAdapter</code> value
     */
    public void setServiceAdapter(SAServiceAdapter inServiceAdapter)
    {
        serviceAdapter = inServiceAdapter;
    }
    /**
     * provides the SAServices
     */
    private SAServiceAdapter serviceAdapter;
    /**
     * provides common RPC server services
     */
    private RpcServerServices<SessionClazz> serverServices;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP Strategy Engine RPC Service"; //$NON-NLS-1$
}

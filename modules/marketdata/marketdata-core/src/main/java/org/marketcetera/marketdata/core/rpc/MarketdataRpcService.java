package org.marketcetera.marketdata.core.rpc;

import java.util.Deque;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.CancelRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.CancelResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.EventsRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.EventsResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.HeartbeatRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.HeartbeatResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LastUpdateRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LastUpdateResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LogoutRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.LogoutResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.MarketDataResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.rpc.RpcCredentials;
import org.marketcetera.util.rpc.RpcServerServices;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.tags.SessionId;

import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/* $License$ */

/**
 * Provides market data RPC server-side services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketdataRpcService<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,RpcClientService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#login(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginRequest)
     */
    @Override
    public LoginResponse login(RpcController inController,
                               LoginRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received authentication request for {}",
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
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#logout(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LogoutRequest)
     */
    @Override
    public LogoutResponse logout(RpcController inController,
                                 LogoutRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received logout request for {}",
                               DESCRIPTION,
                               inRequest.getSessionId());
        serverServices.logout(inRequest.getSessionId());
        return LogoutResponse.newBuilder().setStatus(true).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#heartbeat(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.HeartbeatRequest)
     */
    @Override
    public HeartbeatResponse heartbeat(RpcController inController,
                                       HeartbeatRequest inRequest)
            throws ServiceException
    {
        return RpcMarketdata.HeartbeatResponse.newBuilder().setId(inRequest.getId()).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#request(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.MarketDataRequest)
     */
    @Override
    public MarketDataResponse request(RpcController inController,
                                      MarketDataRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        return RpcMarketdata.MarketDataResponse.newBuilder().setId(serviceAdapter.request(org.marketcetera.marketdata.MarketDataRequestBuilder.newRequestFromString(inRequest.getRequest()),
                                                                                          inRequest.getStreamEvents())).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#getLastUpdate(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LastUpdateRequest)
     */
    @Override
    public LastUpdateResponse getLastUpdate(RpcController inController,
                                            LastUpdateRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        return RpcMarketdata.LastUpdateResponse.newBuilder().setTimestamp(serviceAdapter.getLastUpdate(inRequest.getId())).build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#cancel(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.CancelRequest)
     */
    @Override
    public CancelResponse cancel(RpcController inController,
                                 CancelRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        serviceAdapter.cancel(inRequest.getId());
        return RpcMarketdata.CancelResponse.newBuilder().build();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcClientService.BlockingInterface#getEvents(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.EventsRequest)
     */
    @Override
    public EventsResponse getEvents(RpcController inController,
                                    EventsRequest inRequest)
            throws ServiceException
    {
        serverServices.validateAndReturnSession(inRequest.getSessionId());
        Deque<Event> events = serviceAdapter.getEvents(inRequest.getId());
        RpcMarketdata.EventsResponse.Builder responseBuilder = RpcMarketdata.EventsResponse.newBuilder();
        for(Event event : events) {
            try {
                responseBuilder.addPayload(serverServices.marshall(event));
            } catch (JAXBException e) {
                throw new ServiceException(e);
            }
        }
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
        return RpcClientService.newReflectiveBlockingService(this);
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
     * Get the serviceAdapter value.
     *
     * @return a <code>MarketDataServiceAdapter</code> value
     */
    public MarketDataServiceAdapter getServiceAdapter()
    {
        return serviceAdapter;
    }
    /**
     * Sets the serviceAdapter value.
     *
     * @param inServiceAdapter a <code>MarketDataServiceAdapter</code> value
     */
    public void setServiceAdapter(MarketDataServiceAdapter inServiceAdapter)
    {
        serviceAdapter = inServiceAdapter;
    }
    /**
     * 
     */
    private MarketDataServiceAdapter serviceAdapter;
    /**
     * 
     */
    private RpcServerServices<SessionClazz> serverServices;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP Marketdata RPC Service";
}

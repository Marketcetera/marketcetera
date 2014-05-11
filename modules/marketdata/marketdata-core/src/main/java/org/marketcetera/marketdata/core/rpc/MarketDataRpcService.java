package org.marketcetera.marketdata.core.rpc;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.AllEventsRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.AllEventsResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.AvailableCapabilityRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.AvailableCapabilityResponse;
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
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotPageRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotPageResponse;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotRequest;
import org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotResponse;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.trade.Instrument;
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
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class MarketDataRpcService<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,RpcMarketDataService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#login(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LoginRequest)
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
        RpcMarketdata.LoginResponse.Builder responseBuilder = RpcMarketdata.LoginResponse.newBuilder();
        try {
            SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
                                                                       inRequest.getPassword(),
                                                                       inRequest.getAppId(),
                                                                       inRequest.getClientId(),
                                                                       inRequest.getVersionId(),
                                                                       new Locale(inRequest.getLocale().getLanguage(),
                                                                                  inRequest.getLocale().getCountry(),
                                                                                  inRequest.getLocale().getVariant())));
            return responseBuilder.setSessionId(sessionId.getValue()).build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()==null?"":e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#logout(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LogoutRequest)
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
        RpcMarketdata.LogoutResponse.Builder responseBuilder = RpcMarketdata.LogoutResponse.newBuilder();
        try {
            serverServices.logout(inRequest.getSessionId());
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#heartbeat(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.HeartbeatRequest)
     */
    @Override
    public HeartbeatResponse heartbeat(RpcController inController,
                                       HeartbeatRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.HeartbeatResponse.Builder responseBuilder = RpcMarketdata.HeartbeatResponse.newBuilder();
        try {
            return responseBuilder.setId(inRequest.getId()).build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#request(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.MarketDataRequest)
     */
    @Override
    public MarketDataResponse request(RpcController inController,
                                      MarketDataRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.MarketDataResponse.Builder responseBuilder = RpcMarketdata.MarketDataResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            return responseBuilder.setId(serviceAdapter.request(org.marketcetera.marketdata.MarketDataRequestBuilder.newRequestFromString(inRequest.getRequest()),
                                                                inRequest.getStreamEvents())).build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setId(-1).setMessage(ExceptionUtils.getRootCauseMessage(e)).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getLastUpdate(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.LastUpdateRequest)
     */
    @Override
    public LastUpdateResponse getLastUpdate(RpcController inController,
                                            LastUpdateRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.LastUpdateResponse.Builder responseBuilder = RpcMarketdata.LastUpdateResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            return responseBuilder.setTimestamp(serviceAdapter.getLastUpdate(inRequest.getId())).build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setTimestamp(-1).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#cancel(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.CancelRequest)
     */
    @Override
    public CancelResponse cancel(RpcController inController,
                                 CancelRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.CancelResponse.Builder responseBuilder = RpcMarketdata.CancelResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            serviceAdapter.cancel(inRequest.getId());
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getEvents(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.EventsRequest)
     */
    @Override
    public EventsResponse getEvents(RpcController inController,
                                    EventsRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.EventsResponse.Builder responseBuilder = RpcMarketdata.EventsResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Deque<Event> events = serviceAdapter.getEvents(inRequest.getId());
            responseBuilder.setId(inRequest.getId());
            for(Event event : events) {
                responseBuilder.addPayload(serverServices.marshal(event));
            }
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setId(-1).setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getAllEvents(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.AllEventsRequest)
     */
    @Override
    public AllEventsResponse getAllEvents(RpcController inController,
                                          AllEventsRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.AllEventsResponse.Builder responseBuilder = RpcMarketdata.AllEventsResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Map<Long,LinkedList<Event>> events = serviceAdapter.getAllEvents(inRequest.getIdList());
            for(Map.Entry<Long,LinkedList<Event>> entry : events.entrySet()) {
                RpcMarketdata.EventsResponse.Builder entryBuilder = RpcMarketdata.EventsResponse.newBuilder().setId(entry.getKey());
                for(Event event : entry.getValue()) {
                    entryBuilder.addPayload(serverServices.marshal(event));
                }
                responseBuilder.addEvents(entryBuilder.build());
            }
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getSnapshot(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotRequest)
     */
    @Override
    public SnapshotResponse getSnapshot(RpcController inController,
                                        SnapshotRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.SnapshotResponse.Builder responseBuilder = RpcMarketdata.SnapshotResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Instrument instrument = serverServices.unmarshall(inRequest.getInstrument().getPayload());
            Content content = Content.valueOf(inRequest.getContent().name());
            String provider = null;
            if(inRequest.hasProvider()) {
                provider = inRequest.getProvider();
            }
            Deque<Event> events = serviceAdapter.getSnapshot(instrument,
                                                             content,
                                                             provider);
            for(Event event : events) {
                responseBuilder.addPayload(serverServices.marshal(event));
            }
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getSnapshotPage(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.SnapshotPageRequest)
     */
    @Override
    public SnapshotPageResponse getSnapshotPage(RpcController inController,
                                                SnapshotPageRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.SnapshotPageResponse.Builder responseBuilder = RpcMarketdata.SnapshotPageResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Instrument instrument = serverServices.unmarshall(inRequest.getInstrument().getPayload());
            Content content = Content.valueOf(inRequest.getContent().name());
            String provider = null;
            if(inRequest.hasProvider()) {
                provider = inRequest.getProvider();
            }
            Deque<Event> events = serviceAdapter.getSnapshotPage(instrument,
                                                                 content,
                                                                 provider,
                                                                 new PageRequest(inRequest.getPage().getPage(),
                                                                                 inRequest.getPage().getSize()));
            for(Event event : events) {
                responseBuilder.addPayload(serverServices.marshal(event));
            }
            return responseBuilder.build();
        } catch (JAXBException e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.RpcMarketdata.RpcMarketDataService.BlockingInterface#getAvailableCapability(com.google.protobuf.RpcController, org.marketcetera.marketdata.core.rpc.RpcMarketdata.AvailableCapabilityRequest)
     */
    @Override
    public AvailableCapabilityResponse getAvailableCapability(RpcController inController,
                                                              AvailableCapabilityRequest inRequest)
            throws ServiceException
    {
        RpcMarketdata.AvailableCapabilityResponse.Builder responseBuilder = RpcMarketdata.AvailableCapabilityResponse.newBuilder();
        try {
            serverServices.validateAndReturnSession(inRequest.getSessionId());
            Set<Capability> events = serviceAdapter.getAvailableCapability();
            for(Capability event : events) {
                responseBuilder.addCapability(RpcMarketdata.ContentAndCapability.valueOf(event.name()));
            }
            return responseBuilder.build();
        } catch (Exception e) {
            return responseBuilder.setFailed(true).setMessage(e.getMessage()==null?"":e.getMessage()).build();
        }
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
        return RpcMarketDataService.newReflectiveBlockingService(this);
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
     * Validates this object.
     *
     * @throws IllegalArgumentException if a validation error occurs
     */
    @PostConstruct
    public void validate()
    {
        Validate.notNull(serviceAdapter);
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
     * provides a link to the service provider for market data services
     */
    private MarketDataServiceAdapter serviceAdapter;
    /**
     * provides RPC Server services
     */
    private RpcServerServices<SessionClazz> serverServices;
    /**
     * description of the service
     */
    private static final String DESCRIPTION = "MATP Marketdata RPC Service"; //$NON-NLS-1$
}

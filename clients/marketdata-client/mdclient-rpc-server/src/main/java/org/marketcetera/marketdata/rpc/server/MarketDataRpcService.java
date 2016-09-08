package org.marketcetera.marketdata.rpc.server;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.PageRequest;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.AllEventsRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.AllEventsResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.AvailableCapabilityRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.AvailableCapabilityResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.CancelRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.CancelResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.EventsRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.EventsResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.LastUpdateRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.LastUpdateResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.MarketDataRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.MarketDataResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotPageRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotPageResponse;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotRequest;
import org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotResponse;
import org.marketcetera.mdclient.MarketDataService;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.ws.ContextClassProvider;
import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC market data service implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcService<SessionClazz>
        extends AbstractRpcService<SessionClazz,MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.server.AbstractRpcService#start()
     */
    @Override
    public void start()
            throws Exception
    {
        Validate.notNull(marketDataService,
                         "Market data service required");
        service = new Service();
        synchronized(contextLock) {
            context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        }
        super.start();
    }
    /**
     * Get the market data service service value.
     *
     * @return a <code>MarketDataService</code> value
     */
    public MarketDataService getServiceAdapter()
    {
        return marketDataService;
    }
    /**
     * Sets the market data service value.
     *
     * @param inMarketDataService a <code>MarketDataService</code> value
     */
    public void setServiceAdapter(MarketDataService inMarketDataService)
    {
        marketDataService = inMarketDataService;
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
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
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
    protected MarketDataRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Marketdata RPC Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(LoginRequest inRequest,
                          StreamObserver<LoginResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doLogin(inRequest,
                                              inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(LogoutRequest inRequest,
                           StreamObserver<LogoutResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doLogout(inRequest,
                                               inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(HeartbeatRequest inRequest,
                              StreamObserver<HeartbeatResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doHeartbeat(inRequest,
                                                  inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#request(org.marketcetera.marketdata.core.rpc.MarketdataRpc.MarketDataRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void request(MarketDataRequest inRequest,
                            StreamObserver<MarketDataResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.MarketDataResponse.Builder responseBuilder = MarketdataRpc.MarketDataResponse.newBuilder();
                MarketdataRpc.MarketDataResponse response = responseBuilder.setId(marketDataService.request(org.marketcetera.marketdata.MarketDataRequestBuilder.newRequestFromString(inRequest.getRequest()),
                                                                                                         inRequest.getStreamEvents())).build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getLastUpdate(org.marketcetera.marketdata.core.rpc.MarketdataRpc.LastUpdateRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getLastUpdate(LastUpdateRequest inRequest,
                                  StreamObserver<LastUpdateResponse> inResponseObserver)
        {
            MarketdataRpc.LastUpdateResponse.Builder responseBuilder = MarketdataRpc.LastUpdateResponse.newBuilder();
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.LastUpdateResponse response = responseBuilder.setTimestamp(marketDataService.getLastUpdate(inRequest.getId())).build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#cancel(org.marketcetera.marketdata.core.rpc.MarketdataRpc.CancelRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void cancel(CancelRequest inRequest,
                           StreamObserver<CancelResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.CancelResponse.Builder responseBuilder = MarketdataRpc.CancelResponse.newBuilder();
                marketDataService.cancel(inRequest.getId());
                inResponseObserver.onNext(responseBuilder.build());
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                if(e instanceof StatusRuntimeException) {
                    throw (StatusRuntimeException)e;
                }
                throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withCause(e).withDescription(ExceptionUtils.getRootCauseMessage(e)));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getEvents(org.marketcetera.marketdata.core.rpc.MarketdataRpc.EventsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getEvents(EventsRequest inRequest,
                              StreamObserver<EventsResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.EventsResponse.Builder responseBuilder = MarketdataRpc.EventsResponse.newBuilder();
                Deque<Event> events = marketDataService.getEvents(inRequest.getId());
                responseBuilder.setId(inRequest.getId());
                for(Event event : events) {
                    responseBuilder.addPayload(marshal(event));
                }
                MarketdataRpc.EventsResponse response = responseBuilder.build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getAllEvents(org.marketcetera.marketdata.core.rpc.MarketdataRpc.AllEventsRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAllEvents(AllEventsRequest inRequest,
                                 StreamObserver<AllEventsResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.AllEventsResponse.Builder responseBuilder = MarketdataRpc.AllEventsResponse.newBuilder();
                Map<Long,LinkedList<Event>> events = marketDataService.getAllEvents(inRequest.getIdList());
                for(Map.Entry<Long,LinkedList<Event>> entry : events.entrySet()) {
                    MarketdataRpc.EventsResponse.Builder entryBuilder = MarketdataRpc.EventsResponse.newBuilder().setId(entry.getKey());
                    for(Event event : entry.getValue()) {
                        entryBuilder.addPayload(marshal(event));
                    }
                    responseBuilder.addEvents(entryBuilder.build());
                }
                MarketdataRpc.AllEventsResponse response = responseBuilder.build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getSnapshot(org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getSnapshot(SnapshotRequest inRequest,
                                StreamObserver<SnapshotResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.SnapshotResponse.Builder responseBuilder = MarketdataRpc.SnapshotResponse.newBuilder();
                Instrument instrument = unmarshall(inRequest.getInstrument().getPayload());
                Content content = Content.valueOf(inRequest.getContent().name());
                Deque<Event> events = marketDataService.getSnapshot(instrument,
                                                                 content,
                                                                 inRequest.getProvider());
                for(Event event : events) {
                    responseBuilder.addPayload(marshal(event));
                }
                MarketdataRpc.SnapshotResponse response = responseBuilder.build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getSnapshotPage(org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotPageRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getSnapshotPage(SnapshotPageRequest inRequest,
                                    StreamObserver<SnapshotPageResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.SnapshotPageResponse.Builder responseBuilder = MarketdataRpc.SnapshotPageResponse.newBuilder();
                Instrument instrument = unmarshall(inRequest.getInstrument().getPayload());
                Content content = Content.valueOf(inRequest.getContent().name());
                Deque<Event> events = marketDataService.getSnapshotPage(instrument,
                                                                     content,
                                                                     inRequest.getProvider(),
                                                                     new PageRequest(inRequest.getPage().getPage(),
                                                                                     inRequest.getPage().getSize()));
                for(Event event : events) {
                    responseBuilder.addPayload(marshal(event));
                }
                MarketdataRpc.SnapshotPageResponse response = responseBuilder.build();
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
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getAvailableCapability(org.marketcetera.marketdata.core.rpc.MarketdataRpc.AvailableCapabilityRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAvailableCapability(AvailableCapabilityRequest inRequest,
                                           StreamObserver<AvailableCapabilityResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                MarketdataRpc.AvailableCapabilityResponse.Builder responseBuilder = MarketdataRpc.AvailableCapabilityResponse.newBuilder();
                Set<Capability> events = marketDataService.getAvailableCapability();
                for(Capability event : events) {
                    responseBuilder.addCapability(MarketdataRpc.ContentAndCapability.valueOf(event.name()));
                }
                MarketdataRpc.AvailableCapabilityResponse response = responseBuilder.build();
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
     * provides access to market data services
     */
    @Autowired
    private MarketDataService marketDataService;
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Marketdata RPC Service";
}

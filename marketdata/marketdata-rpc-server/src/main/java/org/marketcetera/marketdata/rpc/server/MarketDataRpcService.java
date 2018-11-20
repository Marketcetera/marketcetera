package org.marketcetera.marketdata.rpc.server;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.marketdata.rpc.MarketDataRpcUtil;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trading.rpc.TradeRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC market data service implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcService.java 17251 2016-09-08 23:18:29Z colin $
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
     * @version $Id: MarketDataRpcService.java 17251 2016-09-08 23:18:29Z colin $
     * @since $Release$
     */
    private class Service
            extends MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void login(BaseRpc.LoginRequest inRequest,
                          StreamObserver<BaseRpc.LoginResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doLogin(inRequest,
                                              inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void logout(BaseRpc.LogoutRequest inRequest,
                           StreamObserver<BaseRpc.LogoutResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doLogout(inRequest,
                                               inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void heartbeat(BaseRpc.HeartbeatRequest inRequest,
                              StreamObserver<BaseRpc.HeartbeatResponse> inResponseObserver)
        {
            MarketDataRpcService.this.doHeartbeat(inRequest,
                                                  inResponseObserver);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#request(org.marketcetera.marketdata.core.rpc.MarketdataRpc.MarketDataRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void request(MarketDataRpc.MarketDataRequest inRequest,
                            StreamObserver<MarketDataRpc.EventsResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received market data request {}",
                                       inRequest);
                authzService.authorize(sessionHolder.getUser(),
                                       MarketDataPermissions.RequestMarketDataAction.name());
                MarketDataRequest request = MarketDataRequestBuilder.newRequestFromString(inRequest.getRequest());
                // the client is obligated to provide a request id that is unique to her. we need to make sure that that uniqueness is guaranteed as long as she does her part,
                //  so we're going to build a compound request id that includes the session id. however, the client doesn't know about that so we need to make sure that we
                //  can deliver her original request id, too.
                String clientRequestId = request.getRequestId();
                String serverRequestId = buildRequestId(inRequest.getSessionId(),
                                                        clientRequestId);
                BaseRpcUtil.AbstractServerListenerProxy<?> marketDataListenerProxy = listenerProxiesById.getIfPresent(serverRequestId);
                if(marketDataListenerProxy == null) {
                    marketDataListenerProxy = new MarketDataListenerProxy(serverRequestId,
                                                                          clientRequestId,
                                                                          inResponseObserver);
                    listenerProxiesById.put(serverRequestId,
                                            marketDataListenerProxy);
                    // we're going to remap the request id from the client request id to the server request id here
                    final MarketDataListener listenerProxy = (MarketDataListener)marketDataListenerProxy;
                    MarketDataRpcUtil.getMarketDataRequest(inRequest.getRequest(),
                                                           serverRequestId,
                                                           clientRequestId).ifPresent(value->marketDataService.request(value,
                                                                                                                       listenerProxy));
                } else {
                    throw new IllegalArgumentException("Duplicate market data request id: " + clientRequestId);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#cancel(org.marketcetera.marketdata.core.rpc.MarketdataRpc.CancelRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void cancel(MarketDataRpc.CancelRequest inRequest,
                           StreamObserver<MarketDataRpc.CancelResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received market data cancel request {}",
                                       inRequest);
                authzService.authorize(sessionHolder.getUser(),
                                       MarketDataPermissions.RequestMarketDataAction.name());
                MarketDataRpc.CancelResponse.Builder responseBuilder = MarketDataRpc.CancelResponse.newBuilder();
                String clientRequestId = inRequest.getRequestId();
                String serverRequestId = buildRequestId(inRequest.getSessionId(),
                                                        clientRequestId);
                BaseRpcUtil.AbstractServerListenerProxy<?> marketDataListenerProxy = listenerProxiesById.getIfPresent(serverRequestId);
                if(marketDataListenerProxy == null) {
                    throw new IllegalArgumentException("Unknown market data request id: " + clientRequestId);
                }
                listenerProxiesById.invalidate(serverRequestId);
                marketDataService.cancel(serverRequestId);
                if(marketDataListenerProxy != null) {
                    marketDataListenerProxy.close();
                }
                MarketDataRpc.CancelResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Sending response: {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getSnapshot(org.marketcetera.marketdata.core.rpc.MarketdataRpc.SnapshotRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getSnapshot(MarketDataRpc.SnapshotRequest inRequest,
                                StreamObserver<MarketDataRpc.SnapshotResponse> inResponseObserver)
        {
            try {
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received snapshot request {}",
                                       inRequest);
                authzService.authorize(sessionHolder.getUser(),
                                       MarketDataPermissions.RequestMarketDataSnapshotAction.name());
                MarketDataRpc.SnapshotResponse.Builder responseBuilder = MarketDataRpc.SnapshotResponse.newBuilder();
                Instrument instrument = TradeRpcUtil.getInstrument(inRequest.getInstrument()).orElse(null);
                Content content = MarketDataRpcUtil.getContent(inRequest.getContent());
                PageRequest pageRequest = inRequest.hasPage()?PagingRpcUtil.getPageRequest(inRequest.getPage()):PageRequest.ALL;
                CollectionPageResponse<Event> eventPage = marketDataService.getSnapshot(instrument,
                                                                                        content,
                                                                                        pageRequest);
                eventPage.getElements().forEach(value->MarketDataRpcUtil.getRpcEventHolder(value).ifPresent(innerValue->responseBuilder.addEvent(innerValue)));
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,
                                                                              eventPage));
                MarketDataRpc.SnapshotResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Sending response: {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#getAvailableCapability(org.marketcetera.marketdata.core.rpc.MarketdataRpc.AvailableCapabilityRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void getAvailableCapability(MarketDataRpc.AvailableCapabilityRequest inRequest,
                                           StreamObserver<MarketDataRpc.AvailableCapabilityResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received available capability request {}",
                                       inRequest);
                MarketDataRpc.AvailableCapabilityResponse.Builder responseBuilder = MarketDataRpc.AvailableCapabilityResponse.newBuilder();
                Set<Capability> capabilities = marketDataService.getAvailableCapability();
                for(Capability capability : capabilities) {
                    responseBuilder.addCapability(MarketDataTypesRpc.ContentAndCapability.valueOf(capability.name()));
                }
                MarketDataRpc.AvailableCapabilityResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Sending response: {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#addMarketDataStatusListener(org.marketcetera.marketdata.core.rpc.MarketDataRpc.AddMarketDataStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addMarketDataStatusListener(MarketDataRpc.AddMarketDataStatusListenerRequest inRequest,
                                                StreamObserver<MarketDataRpc.MarketDataStatusListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received add market data status listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> marketDataStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(marketDataStatusListenerProxy == null) {
                    marketDataStatusListenerProxy = new MarketDataStatusListenerProxy(listenerId,
                                                                                      inResponseObserver);
                    listenerProxiesById.put(marketDataStatusListenerProxy.getId(),
                                            marketDataStatusListenerProxy);
                    marketDataService.addMarketDataStatusListener((MarketDataStatusListener)marketDataStatusListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase#removeMarketDataStatusListener(org.marketcetera.marketdata.core.rpc.MarketDataRpc.RemoveMarketDataStatusListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeMarketDataStatusListener(MarketDataRpc.RemoveMarketDataStatusListenerRequest inRequest,
                                                   StreamObserver<MarketDataRpc.RemoveMarketDataStatusListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Received market data status listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> marketDataStatusListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(marketDataStatusListenerProxy != null) {
                    marketDataService.removeMarketDataStatusListener((MarketDataStatusListener)marketDataStatusListenerProxy);
                    marketDataStatusListenerProxy.close();
                }
                MarketDataRpc.RemoveMarketDataStatusListenerResponse.Builder responseBuilder = MarketDataRpc.RemoveMarketDataStatusListenerResponse.newBuilder();
                MarketDataRpc.RemoveMarketDataStatusListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcService.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
                inResponseObserver.onCompleted();
            }
        }
        /**
         * Build a request id from the given attributes.
         *
         * @param inSessionId a <code>String</code> value
         * @param inRequestId a <code>String</code> value
         * @return a <code>String</code> value
         */
        private String buildRequestId(String inSessionId,
                                      String inRequestId)
        {
            return new StringBuilder().append(inSessionId).append('-').append(inRequestId).toString();
        }
    }
    /**
     * Wraps a {@link MarketDataStatusListener} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MarketDataStatusListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<MarketDataRpc.MarketDataStatusListenerResponse>
            implements MarketDataStatusListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataStatusListener#receiveMarketDataStatus(org.marketcetera.marketdata.MarketDataStatus)
         */
        @Override
        public void receiveMarketDataStatus(MarketDataStatus inMarketDataStatus)
        {
            MarketDataRpcUtil.getRpcMarketDataStatus(inMarketDataStatus).ifPresent(value->responseBuilder.setMarketDataStatus(value));
            MarketDataRpc.MarketDataStatusListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(MarketDataRpcService.class,
                                   "{} received market data status {}, sending {}",
                                   getId(),
                                   inMarketDataStatus,
                                   response);
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new MarketDataStatusListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;MarketDataRpc.MarketDataStatusListenerResponse&gt;</code> value
         */
        private MarketDataStatusListenerProxy(String inId,
                                              StreamObserver<MarketDataRpc.MarketDataStatusListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final MarketDataRpc.MarketDataStatusListenerResponse.Builder responseBuilder = MarketDataRpc.MarketDataStatusListenerResponse.newBuilder();
    }
    /**
     * Wraps a {@link MarketDataListener} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MarketDataListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<MarketDataRpc.EventsResponse>
            implements MarketDataListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
         */
        @Override
        public void receiveMarketData(Event inEvent)
        {
            MarketDataRpcUtil.getRpcEventHolder(inEvent).ifPresent(value->responseBuilder.setEvent(value));
            responseBuilder.setRequestId(clientRequestId);
            MarketDataRpc.EventsResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(MarketDataRpcService.class,
                                   "{} received event {}, sending {}",
                                   getId(),
                                   inEvent,
                                   response);
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new MarketDataListenerProxy instance.
         *
         * @param inServerRequestId a <code>String</code> value
         * @param inClientRequestId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;MarketDataListenerResponse&gt;</code> value
         */
        private MarketDataListenerProxy(String inServerRequestId,
                                        String inClientRequestId,
                                        StreamObserver<MarketDataRpc.EventsResponse> inObserver)
        {
            super(inServerRequestId,
                  inObserver);
            clientRequestId = inClientRequestId;
        }
        /**
         * client-side id
         */
        private final String clientRequestId;
        /**
         * builder used to construct messages
         */
        private final MarketDataRpc.EventsResponse.Builder responseBuilder = MarketDataRpc.EventsResponse.newBuilder();
    }
    /**
     * provides access to market data services
     */
    @Autowired
    private MarketDataService marketDataService;
    /**
     * provides authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * service instance
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Marketdata RPC Service";
    /**
     * holds trade message listeners by id
     */
    private final Cache<String,BaseRpcUtil.AbstractServerListenerProxy<?>> listenerProxiesById = CacheBuilder.newBuilder().build();
}

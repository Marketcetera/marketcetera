package org.marketcetera.marketdata.rpc.client;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Callable;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc.MarketDataStatusListenerResponse;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceBlockingStub;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceStub;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.marketdata.rpc.MarketDataRpcUtil;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.base.BaseRpcUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trading.rpc.TradeRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC {@link MarketDataClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcClient.java 17251 2016-09-08 23:18:29Z colin $
 * @since $Release$
 */
public class MarketDataRpcClient
        extends AbstractRpcClient<MarketDataRpcServiceBlockingStub,MarketDataRpcServiceStub,MarketDataRpcClientParameters>
        implements MarketDataClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#request(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    public String request(MarketDataRequest inRequest,
                          MarketDataListener inMarketDataListener)
    {
        AbstractClientListenerProxy<?,?,?> proxy = listenerProxiesById.getIfPresent(inRequest.getRequestId());
        if(proxy != null) {
            throw new IllegalArgumentException("Duplicate market data request id: " + inRequest.getRequestId());
        }
        proxy = getListenerFor(inMarketDataListener);
        listenerProxiesById.put(inRequest.getRequestId(),
                                proxy);
        final AbstractClientListenerProxy<?,?,?> listener = proxy;
        return executeCall(new Callable<String>(){
            @Override
            @SuppressWarnings("unchecked")
            public String call()
                    throws Exception
            {
                MarketDataRpc.MarketDataRequest.Builder requestBuilder = MarketDataRpc.MarketDataRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setRequest(inRequest.toString());
                MarketDataRpc.MarketDataRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                getAsyncStub().request(request,
                                       (StreamObserver<MarketDataRpc.EventsResponse>)listener);
                return inRequest.getRequestId();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#cancel(long)
     */
    @Override
    public void cancel(String inRequestId)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxiesById.getIfPresent(inRequestId);
        if(proxy == null) {
            throw new IllegalArgumentException("Unknown market data request id: " + inRequestId);
        }
        listenerProxiesById.invalidate(inRequestId);
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "Cancel: {}", //$NON-NLS-1$
                                       inRequestId);
                MarketDataRpc.CancelRequest.Builder requestBuilder = MarketDataRpc.CancelRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setRequestId(inRequestId);
                MarketDataRpc.CancelResponse response = getBlockingStub().cancel(requestBuilder.build());
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "Cancel Response: {}", //$NON-NLS-1$
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent)
    {
        return Lists.newLinkedList(getSnapshot(inInstrument,
                                               inContent,
                                               PageRequest.ALL).getElements());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                                     Content inContent,
                                                     PageRequest inPage)
    {
        return executeCall(new Callable<CollectionPageResponse<Event>>(){
            @Override
            public CollectionPageResponse<Event> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} getting snapshot for {} {}",
                                       getSessionId(),
                                       inInstrument,
                                       inContent);
                MarketDataRpc.SnapshotRequest.Builder requestBuilder = MarketDataRpc.SnapshotRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setContent(MarketDataRpcUtil.getRpcContent(inContent));
                TradeRpcUtil.getRpcInstrument(inInstrument).ifPresent(instrument->requestBuilder.setInstrument(instrument));
                requestBuilder.setPage(PagingRpcUtil.buildPageRequest(inPage));
                MarketDataRpc.SnapshotRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                MarketDataRpc.SnapshotResponse response = getBlockingStub().getSnapshot(request);
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Deque<Event> events = Lists.newLinkedList();
                for(MarketDataTypesRpc.EventHolder rpcEvent : response.getEventList()) {
                    MarketDataRpcUtil.getEvent(rpcEvent).ifPresent(value->events.add(value));
                }
                CollectionPageResponse<Event> eventPage = new CollectionPageResponse<>();
                eventPage.setElements(events);
                PagingRpcUtil.setPageResponse(inPage,
                                              response.getPageResponse(),
                                              eventPage);
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       eventPage);
                return eventPage;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void addMarketDataStatusListener(MarketDataStatusListener inListener)
    {
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} adding status listener",
                                       getSessionId());
                MarketDataRpc.AddMarketDataStatusListenerRequest.Builder requestBuilder = MarketDataRpc.AddMarketDataStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                MarketDataRpc.AddMarketDataStatusListenerRequest addMarketDataStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addMarketDataStatusListenerRequest);
                getAsyncStub().addMarketDataStatusListener(addMarketDataStatusListenerRequest,
                                                           (MarketDataStatusListenerProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void removeMarketDataStatusListener(MarketDataStatusListener inListener)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxies.getIfPresent(inListener);
        listenerProxies.invalidate(inListener);
        if(proxy == null) {
            return;
        }
        listenerProxiesById.invalidate(proxy.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} removing market data status listener",
                                       getSessionId());
                MarketDataRpc.RemoveMarketDataStatusListenerRequest.Builder requestBuilder = MarketDataRpc.RemoveMarketDataStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                MarketDataRpc.RemoveMarketDataStatusListenerRequest removeMarketDataStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeMarketDataStatusListenerRequest);
                MarketDataRpc.RemoveMarketDataStatusListenerResponse response = getBlockingStub().removeMarketDataStatusListener(removeMarketDataStatusListenerRequest);
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        return executeCall(new Callable<Set<Capability>>(){
            @Override
            public Set<Capability> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} getting available capability",
                                       getSessionId());
                MarketDataRpc.AvailableCapabilityRequest.Builder requestBuilder = MarketDataRpc.AvailableCapabilityRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                MarketDataRpc.AvailableCapabilityRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                MarketDataRpc.AvailableCapabilityResponse response = getBlockingStub().getAvailableCapability(request);
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Set<Capability> capabilities = Sets.newHashSet();
                for(MarketDataTypesRpc.ContentAndCapability capability : response.getCapabilityList()) {
                    capabilities.add(Capability.valueOf(capability.name()));
                }
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       capabilities);
                return capabilities;
            }
        });
    }
    /**
     * Create a new MarketDataRpcClient instance.
     *
     * @param inParameters a <code>MarketDataRpcClientParameters</code> value
     */
    MarketDataRpcClient(MarketDataRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected MarketDataRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return MarketDataRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected MarketDataRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return MarketDataRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected BaseRpc.HeartbeatResponse executeHeartbeat(HeartbeatRequest inRequest)
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
        if(inListener instanceof MarketDataListener) {
            return new MarketDataListenerProxy((MarketDataListener)inListener);
        } else if(inListener instanceof MarketDataStatusListener) {
            return new MarketDataStatusListenerProxy((MarketDataStatusListener)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Provides an interface between market data status stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MarketDataStatusListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<MarketDataRpc.MarketDataStatusListenerResponse,MarketDataStatus,MarketDataStatusListener>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected MarketDataStatus translateMessage(MarketDataStatusListenerResponse inResponse)
        {
            return MarketDataRpcUtil.getMarketDataStatus(inResponse.getMarketDataStatus());
        }
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(MarketDataStatusListener inMessageListener,
                                   MarketDataStatus inMessage)
        {
            inMessageListener.receiveMarketDataStatus(inMessage);
        }
        /**
         * Create a new MarketDataStatusListenerProxy instance.
         *
         * @param inMessageListener a <code>MarketDataStatusListener</code> value
         */
        private MarketDataStatusListenerProxy(MarketDataStatusListener inMessageListener)
        {
            super(inMessageListener);
        }
    }
    /**
     * Provides an interface between market data message stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MarketDataListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<MarketDataRpc.EventsResponse,Event,MarketDataListener>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inT)
        {
            super.onError(inT);
            PlatformServices.handleException(MarketDataRpcClient.class,
                                             "Market Data Error",
                                             inT);
            getMessageListener().onError(inT);
        }
        /**
         * Create a new MarketDataListenerProxy instance.
         *
         * @param inMessageListener a <code>MarketDataListener</code> value
         */
        protected MarketDataListenerProxy(MarketDataListener inMessageListener)
        {
            super(inMessageListener);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.MarketDataRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected Event translateMessage(MarketDataRpc.EventsResponse inResponse)
        {
            if(inResponse.hasEvent()) {
                return MarketDataRpcUtil.getEvent(inResponse.getEvent()).orElse(null);
            }
            return null;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.MarketDataRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(MarketDataListener inMessageListener,
                                   Event inMessage)
        {
            inMessageListener.receiveMarketData(inMessage);
        }
    }
    /**
     * The client's application ID: the application name.
     */
    private static final String APP_ID_NAME = MarketDataRpcClient.class.getSimpleName();
    /**
     * The client's application ID: the version.
     */
    private static final VersionInfo APP_ID_VERSION = new VersionInfo(Version.pomversion);
    /**
     * The client's application ID: the ID.
     */
    private static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
    /**
     * holds report listeners by their id or the market data request id
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

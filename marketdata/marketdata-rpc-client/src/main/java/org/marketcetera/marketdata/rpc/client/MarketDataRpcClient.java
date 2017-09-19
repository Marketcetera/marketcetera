package org.marketcetera.marketdata.rpc.client;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Callable;

import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRpcUtil;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc.MarketDataStatusListenerResponse;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceBlockingStub;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceStub;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseUtil;
import org.marketcetera.rpc.base.BaseUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(final Instrument inInstrument,
                                    final Content inContent,
                                    final String inProvider)
    {
//        return executeCall(new Callable<Deque<Event>>(){
//            @Override
//            public Deque<Event> call()
//                    throws Exception
//            {
//                MarketDataRpc.SnapshotRequest.Builder requestBuilder = MarketDataRpc.SnapshotRequest.newBuilder().setSessionId(getSessionId().getValue());
////                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
////                    .setInstrument(TradingTypesRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)));
//                if(inProvider != null){
//                    requestBuilder.setProvider(inProvider);
//                }
//                MarketDataRpc.SnapshotResponse response = getBlockingStub().getSnapshot(requestBuilder.build());
//                Deque<Event> events = Lists.newLinkedList();
//                for(String payload : response.getPayloadList()) {
//                    events.add((Event)unmarshall(payload));
//                }
//                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
//                                       "GetSnapshotResponse: {}", //$NON-NLS-1$
//                                       events);
//                return events;
//            }
//        });
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.marketdata.core.webservice.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(final Instrument inInstrument,
                                        final Content inContent,
                                        final String inProvider,
                                        final PageRequest inPage)
    {
//        return executeCall(new Callable<Deque<Event>>(){
//            @Override
//            public Deque<Event> call()
//                    throws Exception
//            {
//                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
//                                       "GetSnapshotPage: {}/{}/{}/{}", //$NON-NLS-1$
//                                       inInstrument,
//                                       inContent,
//                                       inProvider,
//                                       inPage);
//                MarketDataRpc.SnapshotPageRequest.Builder requestBuilder = MarketDataRpc.SnapshotPageRequest.newBuilder().setSessionId(getSessionId().getValue());
////                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
////                .setInstrument(MarketdataRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)))
////                .setPage(PagingUtil.buildPageRequest(inPage.getPageNumber(),
////                                                     inPage.getPageSize()));
//                if(inProvider != null){
//                    requestBuilder.setProvider(inProvider);
//                }
//                MarketDataRpc.SnapshotPageResponse response = getBlockingStub().getSnapshotPage(requestBuilder.build());
//                Deque<Event> events = Lists.newLinkedList();
//                for(String payload : response.getPayloadList()) {
//                    events.add((Event)unmarshall(payload));
//                }
//                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
//                                       "GetSnapshotPageResponse: {}", //$NON-NLS-1$
//                                       events);
//                return events;
//            }
//        });
        throw new UnsupportedOperationException();
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
                SLF4JLoggerProxy.debug(this,
                                       "GetAvailableCapability"); //$NON-NLS-1$
                MarketDataRpc.AvailableCapabilityResponse response = getBlockingStub().getAvailableCapability(MarketDataRpc.AvailableCapabilityRequest.newBuilder().setSessionId(getSessionId().getValue()).build());
                Set<Capability> capabilities = Sets.newHashSet();
                for(MarketDataRpc.ContentAndCapability capability : response.getCapabilityList()) {
                    capabilities.add(Capability.valueOf(capability.name()));
                }
                SLF4JLoggerProxy.debug(this,
                                       "GetAvailableCapability: {}", //$NON-NLS-1$
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
    protected void executeHeartbeat(HeartbeatRequest inRequest,
                                    StreamObserver<BaseRpc.HeartbeatResponse> inObserver)
    {
        getAsyncStub().heartbeat(inRequest,
                                 inObserver);
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
            extends BaseUtil.AbstractClientListenerProxy<MarketDataRpc.MarketDataStatusListenerResponse,MarketDataStatus,MarketDataStatusListener>
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
            extends BaseUtil.AbstractClientListenerProxy<MarketDataRpc.EventsResponse,Event,MarketDataListener>
    {
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
            return MarketDataRpcUtil.getEvent(inResponse).orElse(null);
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
    private final Cache<String,BaseUtil.AbstractClientListenerProxy<?,?,?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * holds listener proxies keyed by the listener
     */
    private final LoadingCache<Object,BaseUtil.AbstractClientListenerProxy<?,?,?>> listenerProxies = CacheBuilder.newBuilder().build(new CacheLoader<Object,AbstractClientListenerProxy<?,?,?>>() {
        @Override
        public BaseUtil.AbstractClientListenerProxy<?,?,?> load(Object inKey)
                throws Exception
        {
            BaseUtil.AbstractClientListenerProxy<?,?,?> proxy = getListenerFor(inKey);
            listenerProxiesById.put(proxy.getId(),
                                    proxy);
            return proxy;
        }}
    );
}

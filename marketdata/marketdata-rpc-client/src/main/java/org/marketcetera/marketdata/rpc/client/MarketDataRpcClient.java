package org.marketcetera.marketdata.rpc.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc.MarketDataResponse;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceBlockingStub;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceStub;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseUtil;
import org.marketcetera.rpc.base.BaseUtil.AbstractListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
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
        final AbstractListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inMarketDataListener);
        return executeCall(new Callable<String>(){
            @Override
            @SuppressWarnings("unchecked")
            public String call()
                    throws Exception
            {
                MarketDataRpc.MarketDataRequest.Builder requestBuilder = MarketDataRpc.MarketDataRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setRequest(inRequest.toString());
                requestBuilder.setListenerId(listener.getId());
                MarketDataRpc.MarketDataRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(MarketDataRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                getAsyncStub().request(request,
                                       (StreamObserver<MarketDataResponse>)listener);
                return listener.getId();
            }
        });
/*
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inTradeMessageListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inTradeMessageListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} adding report listener",
                                       getSessionId());
                TradingRpc.AddTradeMessageListenerRequest.Builder requestBuilder = TradingRpc.AddTradeMessageListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                TradingRpc.AddTradeMessageListenerRequest addTradeMessageListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(TradingRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addTradeMessageListenerRequest);
                getAsyncStub().addTradeMessageListener(addTradeMessageListenerRequest,
                                                       (TradeMessageListenerProxy)listener);
                return null;
            }
        });
 */
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#cancel(long)
     */
    @Override
    public void cancel(String inRequestId)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
//                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
//                                       "Cancel: {}", //$NON-NLS-1$
//                                       inRequestId);
//                MarketDataRpc.CancelResponse response = getBlockingStub().cancel(MarketDataRpc.CancelRequest.newBuilder().setSessionId(getSessionId().getValue())
//                                                                                 .setId(inRequestId).build());
//                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
//                                       "Cancel Response: {}", //$NON-NLS-1$
//                                       response);
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
        return executeCall(new Callable<Deque<Event>>(){
            @Override
            public Deque<Event> call()
                    throws Exception
            {
                MarketDataRpc.SnapshotRequest.Builder requestBuilder = MarketDataRpc.SnapshotRequest.newBuilder().setSessionId(getSessionId().getValue());
//                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
//                    .setInstrument(TradingTypesRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)));
                if(inProvider != null){
                    requestBuilder.setProvider(inProvider);
                }
                MarketDataRpc.SnapshotResponse response = getBlockingStub().getSnapshot(requestBuilder.build());
                Deque<Event> events = Lists.newLinkedList();
                for(String payload : response.getPayloadList()) {
                    events.add((Event)unmarshall(payload));
                }
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "GetSnapshotResponse: {}", //$NON-NLS-1$
                                       events);
                return events;
            }
        });
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
        return executeCall(new Callable<Deque<Event>>(){
            @Override
            public Deque<Event> call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "GetSnapshotPage: {}/{}/{}/{}", //$NON-NLS-1$
                                       inInstrument,
                                       inContent,
                                       inProvider,
                                       inPage);
                MarketDataRpc.SnapshotPageRequest.Builder requestBuilder = MarketDataRpc.SnapshotPageRequest.newBuilder().setSessionId(getSessionId().getValue());
//                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
//                .setInstrument(MarketdataRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)))
//                .setPage(PagingUtil.buildPageRequest(inPage.getPageNumber(),
//                                                     inPage.getPageSize()));
                if(inProvider != null){
                    requestBuilder.setProvider(inProvider);
                }
                MarketDataRpc.SnapshotPageResponse response = getBlockingStub().getSnapshotPage(requestBuilder.build());
                Deque<Event> events = Lists.newLinkedList();
                for(String payload : response.getPayloadList()) {
                    events.add((Event)unmarshall(payload));
                }
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "GetSnapshotPageResponse: {}", //$NON-NLS-1$
                                       events);
                return events;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void addMarketDataStatusListener(MarketDataStatusListener inListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void removeMarketDataStatusListener(MarketDataStatusListener inListener)
    {
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
     * Validate and start the object.
     */
    @Override
    public void start()
            throws Exception
    {
        super.start();
        synchronized(contextLock) {
            context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        }
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
    /**
     * Create a new MarketDataRpcClient instance.
     *
     * @param inParameters a <code>MarketDataRpcClientParameters</code> value
     */
    MarketDataRpcClient(MarketDataRpcClientParameters inParameters)
    {
        super(inParameters);
        contextClassProvider = inParameters.getContextClassProvider();
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
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private String marshall(Object inObject)
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
    @SuppressWarnings("unchecked")
    private <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * Creates the appropriate proxy for the given listener.
     *
     * @param inListener an <code>Object</code> value
     * @return an <code>AbstractListenerProxy&lt;?,?,?&gt;</code> value
     */
    private static AbstractListenerProxy<?,?,?> getListenerFor(Object inListener)
    {
        if(inListener instanceof MarketDataListener) {
            return new MarketDataListenerProxy((MarketDataListener)inListener);
        } else {
            throw new UnsupportedOperationException();
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
            extends BaseUtil.AbstractListenerProxy<MarketDataRpc.EventsResponse,Event,MarketDataListener>
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
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected Event translateMessage(MarketDataRpc.EventsResponse inResponse)
        {
            return MarketDataRpcUtil.getEvent(inResponse);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.TradingRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(MarketDataListener inMessageListener,
                                   Event inMessage)
        {
            inMessageListener.receiveMarketData(inMessage);
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
     * holds report listeners by their id
     */
    private final Cache<String,BaseUtil.AbstractListenerProxy<?,?,?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * holds listener proxies keyed by the listener
     */
    private final LoadingCache<Object,BaseUtil.AbstractListenerProxy<?,?,?>> listenerProxies = CacheBuilder.newBuilder().build(new CacheLoader<Object,AbstractListenerProxy<?,?,?>>() {
        @Override
        public BaseUtil.AbstractListenerProxy<?,?,?> load(Object inKey)
                throws Exception
        {
            BaseUtil.AbstractListenerProxy<?,?,?> proxy = getListenerFor(inKey);
            listenerProxiesById.put(proxy.getId(),
                                    proxy);
            return proxy;
        }}
    );
}

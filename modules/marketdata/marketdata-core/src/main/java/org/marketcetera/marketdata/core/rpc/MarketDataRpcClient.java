package org.marketcetera.marketdata.core.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceBlockingStub;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc.MarketDataRpcServiceStub;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingUtil;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC {@link MarketDataClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcClient
        extends AbstractRpcClient<MarketDataRpcServiceBlockingStub,MarketDataRpcServiceStub>
        implements MarketDataServiceClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(MarketDataRequest inRequest,
                        boolean inStreamEvents)
    {
        return executeCall(new Callable<Long>(){
            @Override
            public Long call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "MarketDataRequest: {}", //$NON-NLS-1$
                                       inRequest);
                MarketdataRpc.MarketDataResponse response = getBlockingStub().request(MarketdataRpc.MarketDataRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                      .setRequest(inRequest.toString())
                                                                                      .setStreamEvents(inStreamEvents).build());
                SLF4JLoggerProxy.debug(this,
                                       "MarketDataResponse: {}", //$NON-NLS-1$
                                       response.getId());
                return response.getId();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getLastUpdate(long)
     */
    @Override
    public long getLastUpdate(final long inRequestId)
    {
        return executeCall(new Callable<Long>(){
            @Override
            public Long call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "GetLastUpdate: {}", //$NON-NLS-1$
                                       inRequestId);
                MarketdataRpc.LastUpdateResponse response = getBlockingStub().getLastUpdate(MarketdataRpc.LastUpdateRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                            .setId(inRequestId).build());
                SLF4JLoggerProxy.debug(this,
                                       "GetLastUpdateResponse: {}", //$NON-NLS-1$
                                       response.getTimestamp());
                return response.getTimestamp();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#cancel(long)
     */
    @Override
    public void cancel(long inRequestId)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "Cancel: {}", //$NON-NLS-1$
                                       inRequestId);
                MarketdataRpc.CancelResponse response = getBlockingStub().cancel(MarketdataRpc.CancelRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                 .setId(inRequestId).build());
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "Cancel Response: {}", //$NON-NLS-1$
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getEvents(long)
     */
    @Override
    public Deque<Event> getEvents(long inRequestId)
    {
        return executeCall(new Callable<Deque<Event>>(){
            @Override
            public Deque<Event> call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                       "GetEvents: {}", //$NON-NLS-1$
                                       inRequestId);
                    MarketdataRpc.EventsResponse response = getBlockingStub().getEvents(MarketdataRpc.EventsRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                        .setId(inRequestId).build());
                    Deque<Event> events = Lists.newLinkedList();
                    for(String payload : response.getPayloadList()) {
                        events.add((Event)unmarshall(payload));
                    }
                    SLF4JLoggerProxy.debug(MarketDataRpcClient.this,
                                           "GetEventsResponse: {}", //$NON-NLS-1$
                                           events);
                    return events;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getAllEvents(java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds)
    {
        return executeCall(new Callable<Map<Long,LinkedList<Event>>>(){
            @Override
            public Map<Long,LinkedList<Event>> call()
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "GetAllEvents: {}", //$NON-NLS-1$
                                       inRequestIds);
                MarketdataRpc.AllEventsResponse response = getBlockingStub().getAllEvents(MarketdataRpc.AllEventsRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                          .addAllId(inRequestIds).build());
                Map<Long,LinkedList<Event>> events = Maps.newHashMap();
                for(MarketdataRpc.EventsResponse eventResponse : response.getEventsList()) {
                    LinkedList<Event> eventList = new LinkedList<>();
                    for(String payload : eventResponse.getPayloadList()) {
                        eventList.add((Event)unmarshall(payload));
                    }
                    events.put(eventResponse.getId(),
                               eventList);
                }
                SLF4JLoggerProxy.debug(this,
                                       "GetAllEventsResponse: {}", //$NON-NLS-1$
                                       events);
                return events;
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
                MarketdataRpc.SnapshotRequest.Builder requestBuilder = MarketdataRpc.SnapshotRequest.newBuilder().setSessionId(getSessionId().getValue());
                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
                    .setInstrument(MarketdataRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)));
                if(inProvider != null){
                    requestBuilder.setProvider(inProvider);
                }
                MarketdataRpc.SnapshotResponse response = getBlockingStub().getSnapshot(requestBuilder.build());
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
                MarketdataRpc.SnapshotPageRequest.Builder requestBuilder = MarketdataRpc.SnapshotPageRequest.newBuilder().setSessionId(getSessionId().getValue());
                requestBuilder.setContent(MarketdataRpc.ContentAndCapability.valueOf(inContent.name()))
                .setInstrument(MarketdataRpc.Instrument.newBuilder().setPayload(marshall(inInstrument)))
                .setPage(PagingUtil.buildPageRequest(inPage.getPage(),
                                                     inPage.getSize()));
                if(inProvider != null){
                    requestBuilder.setProvider(inProvider);
                }
                MarketdataRpc.SnapshotPageResponse response = getBlockingStub().getSnapshotPage(requestBuilder.build());
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
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#addServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
        synchronized(serverStatusSubscribers) {
            ISubscriber subscriberProxy = new ISubscriber() {
                @Override
                public boolean isInteresting(Object inData)
                {
                    return inData instanceof Boolean;
                }
                @Override
                public void publishTo(Object inData)
                {
                    inListener.receiveServerStatus((Boolean)inData);
                }
            };
            serverStatusSubscribers.put(inListener,
                                        subscriberProxy);
        }
        inListener.receiveServerStatus(isRunning());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#removeServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        synchronized(serverStatusSubscribers) {
            ISubscriber subscriberProxy = serverStatusSubscribers.remove(inListener);
            if(subscriberProxy != null) {
                publisher.unsubscribe(subscriberProxy);
            }
        }
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
                MarketdataRpc.AvailableCapabilityResponse response = getBlockingStub().getAvailableCapability(MarketdataRpc.AvailableCapabilityRequest.newBuilder().setSessionId(getSessionId().getValue()).build());
                Set<Capability> capabilities = Sets.newHashSet();
                for(MarketdataRpc.ContentAndCapability capability : response.getCapabilityList()) {
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
     * @see org.marketcetera.rpc.client.AbstractRpcClient#onStatusChange(boolean)
     */
    @Override
    protected void onStatusChange(boolean inIsConnected)
    {
        synchronized(serverStatusSubscribers) {
            for(ISubscriber subscriber : serverStatusSubscribers.values()) {
                if(subscriber.isInteresting(inIsConnected)) {
                    subscriber.publishTo(inIsConnected);
                }
            }
        }
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
     * tracks subscribers to connection status changes
     */
    @GuardedBy("serverStatusSubscribers")
    private final Map<ServerStatusListener,ISubscriber> serverStatusSubscribers = Maps.newHashMap();
    /**
     * publishes notifications of connection status changes
     */
    private final PublisherEngine publisher = new PublisherEngine(true);
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
}

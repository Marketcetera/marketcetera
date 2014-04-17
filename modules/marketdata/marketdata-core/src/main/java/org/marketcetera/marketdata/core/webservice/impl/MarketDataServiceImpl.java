package org.marketcetera.marketdata.core.webservice.impl;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.MarketDataService;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.marketdata.core.webservice.UnknownRequestException;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServerProvider;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides Market Data Nexus services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketDataServiceImpl
        extends ServiceBaseImpl<Object>
        implements MarketDataService,Lifecycle,MarketDataServiceAdapter
{
    /**
     * Create a new MarketDataWebServiceImpl instance.
     *
     * @param inSessionManager a <code>SessionManager&lt;Object&gt;</code> value
     */
    public MarketDataServiceImpl(SessionManager<Object> inSessionManager)
    {
        super(inSessionManager);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#getAvailableCapability(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public Set<Capability> getAvailableCapability(ClientContext inContext)
            throws RemoteException
    {
        return new RemoteCaller<Object,Set<Capability>>(getSessionManager()) {
            @Override
            protected Set<Capability> call(ClientContext inContext,
                                           SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting market data capabilities",
                                       inContext.getSessionId());
                checkConnection();
                return marketDataManager.getAvailableCapability();
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        return marketDataManager.getAvailableCapability();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#request(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(ClientContext inContext,
                        final MarketDataRequest inRequest,
                        final boolean inStreamEvents)
            throws RemoteException
    {
        return new RemoteCaller<Object,Long>(getSessionManager()) {
            @Override
            protected Long call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting {}",
                                       inContext.getSessionId(),
                                       inRequest);
                checkConnection();
                long requestId = doRequest(inRequest,
                                           inStreamEvents);
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {} for {}",
                                       inContext,
                                       requestId,
                                       inRequest);
                return requestId;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#request(org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(MarketDataRequest inRequest,
                        boolean inStreamEvents)
    {
        return doRequest(inRequest,
                         inStreamEvents);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#getAllEvents(org.marketcetera.util.ws.stateful.ClientContext, java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(ClientContext inContext,
                                                    final List<Long> inRequestIds)
            throws RemoteException
    {
        return new RemoteCaller<Object,Map<Long,LinkedList<Event>>>(getSessionManager()) {
            @Override
            protected Map<Long,LinkedList<Event>> call(ClientContext inContext,
                                                       SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting events for {}",
                                       inContext.getSessionId(),
                                       inRequestIds);
                checkConnection();
                Map<Long,LinkedList<Event>> eventsToReturn = Maps.newLinkedHashMap();
                for(Long requestId : inRequestIds) {
                    LinkedList<Event> events = Lists.newLinkedList(doGetEvents(requestId));
                    eventsToReturn.put(requestId,
                                       events);
                }
                return eventsToReturn;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getAllEvents(java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds)
    {
        Map<Long,LinkedList<Event>> eventsToReturn = Maps.newLinkedHashMap();
        for(Long requestId : inRequestIds) {
            LinkedList<Event> events = Lists.newLinkedList(doGetEvents(requestId));
            eventsToReturn.put(requestId,
                               events);
        }
        return eventsToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataWebService#getEvents(org.marketcetera.util.ws.stateful.ClientContext, long)
     */
    @Override
    public Deque<Event> getEvents(ClientContext inContext,
                                  final long inRequestId)
            throws RemoteException
    {
        return new RemoteCaller<Object,Deque<Event>>(getSessionManager()) {
            @Override
            protected Deque<Event> call(ClientContext inContext,
                                        SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting events for {}",
                                       inContext.getSessionId(),
                                       inRequestId);
                checkConnection();
                Deque<Event> eventsToReturn = doGetEvents(inRequestId);
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {} for {}",
                                       inContext.getSessionId(),
                                       eventsToReturn,
                                       inRequestId);
                return eventsToReturn;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getEvents(long)
     */
    @Override
    public Deque<Event> getEvents(long inRequestId)
    {
        return doGetEvents(inRequestId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#getLastUpdate(org.marketcetera.util.ws.stateful.ClientContext, long)
     */
    @Override
    public long getLastUpdate(ClientContext inContext,
                              final long inRequestId)
            throws RemoteException
    {
        return new RemoteCaller<Object,Long>(getSessionManager()) {
            @Override
            protected Long call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting update timestamp for {}",
                                       inContext.getSessionId(),
                                       inRequestId);
                checkConnection();
                long timestamp = doGetLastUpdate(inRequestId);
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {} for {}",
                                       inContext.getSessionId(),
                                       timestamp,
                                       inRequestId);
                return timestamp;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getLastUpdate(long)
     */
    @Override
    public long getLastUpdate(long inId)
    {
        return doGetLastUpdate(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#heartbeat(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public void heartbeat(ClientContext inContext)
            throws RemoteException
    {
        new RemoteCaller<Object,Void>(getSessionManager()) {
            @Override
            protected Void call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                return null;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#getSnapshot(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(ClientContext inContext,
                                    final Instrument inInstrument,
                                    final Content inContent,
                                    final String inProvider)
            throws RemoteException
    {
        return new RemoteCaller<Object,Deque<Event>>(getSessionManager()) {
            @Override
            protected Deque<Event> call(ClientContext inContext,
                                        SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting snapshot for {} {} from {}",
                                       inContext.getSessionId(),
                                       inContent,
                                       inInstrument,
                                       inProvider);
                checkConnection();
                return doGetSnapshot(inInstrument,
                                     inContent,
                                     inProvider);
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent,
                                    String inProvider)
    {
        return doGetSnapshot(inInstrument,
                             inContent,
                             inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#getSnapshotPage(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.springframework.data.domain.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(ClientContext inContext,
                                        final Instrument inInstrument,
                                        final Content inContent,
                                        final String inProvider,
                                        final PageRequest inPage)
            throws RemoteException
    {
        return new RemoteCaller<Object,Deque<Event>>(getSessionManager()) {
            @Override
            protected Deque<Event> call(ClientContext inContext,
                                        SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting snapshot page {} for {} {} from {}",
                                       inContext.getSessionId(),
                                       inPage,
                                       inContent,
                                       inInstrument,
                                       inProvider);
                checkConnection();
                return doGetSnapshotPage(inInstrument,
                                         inContent,
                                         inProvider,
                                         inPage);
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.marketdata.core.webservice.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(Instrument inInstrument,
                                        Content inContent,
                                        String inProvider,
                                        PageRequest inPageRequest)
    {
        return doGetSnapshotPage(inInstrument,
                                 inContent,
                                 inProvider,
                                 inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataWebService#cancel(org.marketcetera.util.ws.stateful.ClientContext, long)
     */
    @Override
    public void cancel(ClientContext inContext,
                       final long inRequestId)
            throws RemoteException
    {
        new RemoteCaller<Object,Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                doCancel(inRequestId);
                return null;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter#cancel(long)
     */
    @Override
    public void cancel(long inRequestId)
    {
        doCancel(inRequestId);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(isRunning()) {
            stop();
        }
        Validate.notNull(marketDataManager);
        Validate.notNull(serverProvider);
        reaper = Executors.newScheduledThreadPool(1);
        reaper.scheduleAtFixedRate(new Reaper(),
                                   reaperInterval,
                                   reaperInterval,
                                   TimeUnit.MILLISECONDS);
        remoteService = serverProvider.getServer().publish(this,
                                                           MarketDataService.class);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        if(reaper != null) {
            reaper.shutdownNow();
            reaper = null;
        }
        try {
            remoteService.stop();
        } catch (RuntimeException ignored) {
        } finally {
            remoteService = null;
            subscribersByRequestId.clear();
            running.set(false);
        }
    }
    /**
     * Get the server value.
     *
     * @return a <code>ServerProvider&lt;?&gt;</code> value
     */
    public ServerProvider<?> getServer()
    {
        return serverProvider;
    }
    /**
     * Sets the server value.
     *
     * @param inServer a <code>ServerProvider&lt;?&gt;</code> value
     */
    public void setServer(ServerProvider<?> inServer)
    {
        serverProvider = inServer;
    }
    /**
     * Get the marketDataManager value.
     *
     * @return a <code>MarketDataManager</code> value
     */
    public MarketDataManager getMarketDataManager()
    {
        return marketDataManager;
    }
    /**
     * Sets the marketDataManager value.
     *
     * @param inMarketDataManager a <code>MarketDataManager</code> value
     */
    public void setMarketDataManager(MarketDataManager inMarketDataManager)
    {
        marketDataManager = inMarketDataManager;
    }
    /**
     * Get the reaperInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getReaperInterval()
    {
        return reaperInterval;
    }
    /**
     * Sets the reaperInterval value.
     *
     * @param inReaperInterval a <code>long</code> value
     */
    public void setReaperInterval(long inReaperInterval)
    {
        reaperInterval = inReaperInterval;
    }
    /**
     * Get the maxSubscriptionInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getMaxSubscriptionInterval()
    {
        return maxSubscriptionInterval;
    }
    /**
     * Sets the maxSubscriptionInterval value.
     *
     * @param inMaxSubscriptionInterval a <code>long</code> value
     */
    public void setMaxSubscriptionInterval(long inMaxSubscriptionInterval)
    {
        maxSubscriptionInterval = inMaxSubscriptionInterval;
    }
    /**
     * Executes the given market data request.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inStreamEvents a <code>boolean</code> value
     * @return a <code>long</code> value
     */
    private long doRequest(MarketDataRequest inRequest,
                           boolean inStreamEvents)
    {
        ServiceSubscriber subscriber = new ServiceSubscriber(inStreamEvents);
        long requestId = marketDataManager.requestMarketData(inRequest,
                                                             subscriber);
        subscriber.setRequestId(requestId);
        subscribersByRequestId.put(requestId,
                                   subscriber);
        return requestId;
    }
    /**
     * Executes a get last update call.
     *
     * @param inRequestId a <code>long</code> value
     * @return a <code>long</code> value
     */
    private long doGetLastUpdate(long inRequestId)
    {
        ServiceSubscriber subscriber = subscribersByRequestId.get(inRequestId);
        if(subscriber == null) {
            throw new UnknownRequestException(inRequestId);
        }
        long timestamp = subscriber.getUpdateTimestamp();
        return timestamp;
    }
    /**
     * Retrieves the events for the given request.
     *
     * @param inRequestId a <code>long</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     * @throws UnknownRequestException if the given request is invalid
     */
    private Deque<Event> doGetEvents(long inRequestId)
    {
        ServiceSubscriber subscriber = subscribersByRequestId.get(inRequestId);
        if(subscriber == null) {
            throw new UnknownRequestException(inRequestId);
        }
        return subscriber.getEvents();
    }
    /**
     * Cancels the market data request with the given id.
     *
     * @param inRequestId a <code>long</code> value
     */
    private void doCancel(long inRequestId)
    {
        ServiceSubscriber subscriber = subscribersByRequestId.remove(inRequestId);
        if(subscriber != null) {
            marketDataManager.cancelMarketDataRequest(inRequestId);
            subscriber.cancel();
        }
    }
    /**
     * Gets the most recent snapshot for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    private Deque<Event> doGetSnapshot(Instrument inInstrument,
                                       Content inContent,
                                       String inProvider)
    {
        Event event = marketDataManager.requestMarketDataSnapshot(inInstrument,
                                                                  inContent,
                                                                  inProvider);
        if(event == null) {
            return new LinkedList<>();
        }
        Deque<Event> eventsToReturn = Lists.newLinkedList();
        if(event instanceof AggregateEvent) {
            eventsToReturn.addAll(((AggregateEvent)event).decompose());
        } else {
            eventsToReturn.add(event);
        }
        return eventsToReturn;
    }
    /**
     * Gets the requested page of the most recent snapshot for the given attribute.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    private Deque<Event> doGetSnapshotPage(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider,
                                           PageRequest inPageRequest)
    {
        Event event = marketDataManager.requestMarketDataSnapshot(inInstrument,
                                                                  inContent,
                                                                  inProvider);
        if(event == null) {
            return new LinkedList<>();
        }
        Deque<Event> eventsToReturn = Lists.newLinkedList();
        if(event instanceof AggregateEvent) {
            eventsToReturn.addAll(((AggregateEvent)event).decompose());
        } else {
            eventsToReturn.add(event);
        }
        // TODO pick out page
        return eventsToReturn;
    }
    /**
     * Checks that the connection is active.
     *
     * @throws ConnectionException if the connection is not active
     */
    private void checkConnection()
    {
        if(!isRunning()) {
            throw new ConnectionException();
        }
    }
    /**
     * Manages a request subscription.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ThreadSafe
    @ClassVersion("$Id$")
    private class ServiceSubscriber
            implements ISubscriber
    {
        /**
         * Create a new ServiceSubscriber instance.
         *
         * @param inStreamEvents a <code>boolean</code> value
         */
        public ServiceSubscriber(boolean inStreamEvents)
        {
            storeEvents = inStreamEvents;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            try(CloseableLock publishEventLock = CloseableLock.create(lock.writeLock())) {
                publishEventLock.lock();
                updateTimestamp = System.currentTimeMillis();
                if(!storeEvents) {
                    return;
                }
                if(inData instanceof Event) {
                    events.addFirst((Event)inData);
                } else if(inData instanceof AggregateEvent) {
                    for(Event event : ((AggregateEvent)inData).decompose()) {
                        events.addFirst(event);
                    }
                } else if(inData instanceof Collection<?>) {
                    Collection<?> collectionData = (Collection<?>)inData;
                    for(Object data : collectionData) {
                        publishTo(data);
                    }
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          "Unknown data type: " + inData.getClass().getName()); // TODO message
                    throw new UnsupportedOperationException();
                }
            }
        }
        /**
         * Performs the actions necessary to clean up this subscriber when it is no longer needed.
         */
        private void cancel()
        {
            try(CloseableLock publishEventLock = CloseableLock.create(lock.writeLock())) {
                publishEventLock.lock();
                events.clear();
            }
        }
        /**
         * Get the events value.
         *
         * @return a <code>Deque&lt;Event&gt;</code> value
         */
        private Deque<Event> getEvents()
        {
            retrieveTimestamp = System.currentTimeMillis();
            Deque<Event> eventsToReturn = Lists.newLinkedList();
            try(CloseableLock getEventLock = CloseableLock.create(lock.writeLock())) {
                getEventLock.lock();
                eventsToReturn.addAll(events);
                events.clear();
            }
            return eventsToReturn;
        }
        /**
         * Get the updateTimestamp value.
         *
         * @return a <code>long</code> value
         */
        private long getUpdateTimestamp()
        {
            return updateTimestamp;
        }
        /**
         * Get the requestId value.
         *
         * @return a <code>long</code> value
         */
        private long getRequestId()
        {
            return requestId;
        }
        /**
         * Sets the requestId value.
         *
         * @param inRequestId a <code>long</code> value
         */
        private void setRequestId(long inRequestId)
        {
            requestId = inRequestId;
        }
        /**
         * market data request id
         */
        private volatile long requestId;
        /**
         * last time this subscription was harvested
         */
        private volatile long retrieveTimestamp = System.currentTimeMillis();
        /**
         * indicates whether the subscriber should store updates or not
         */
        private final boolean storeEvents;
        /**
         * tracks the time of the most recent update to the subscription
         */
        private volatile long updateTimestamp;
        /**
         * controls access to critical data
         */
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        /**
         * contains events not yet seen for this subscriber
         */
        @GuardedBy("lock")
        private final Deque<Event> events = Lists.newLinkedList();
    }
    /**
     * Retires market data subscriptions that have not been checked in a while.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private class Reaper
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                // we're accessing this list concurrently, so the contents might change. let's use a copy of the list
                Collection<ServiceSubscriber> subscribers = Lists.newArrayList(subscribersByRequestId.values());
                SLF4JLoggerProxy.debug(MarketDataServiceImpl.this,
                                       "Reaper examining {} subscription(s)",
                                       subscribers.size());
                for(ServiceSubscriber subscriber : subscribers) {
                    if(subscriber.storeEvents && subscriber.retrieveTimestamp < System.currentTimeMillis()-maxSubscriptionInterval) {
                        SLF4JLoggerProxy.debug(MarketDataServiceImpl.this,
                                               "Reaper canceling {}",
                                               subscriber);
                        doCancel(subscriber.getRequestId());
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(MarketDataServiceImpl.this,
                                      e);
            }
        }
    }
    /**
     * interval at which subscriptions are checked
     */
    private long reaperInterval = 10000;
    /**
     * max life of a subscription that has not been harvested
     */
    private long maxSubscriptionInterval = 10000;
    /**
     * executes repear jobs
     */
    private ScheduledExecutorService reaper;
    /**
     * handle to the remote web service.
     */
    private ServiceInterface remoteService;
    /**
     * provides the running web server to publish this service on
     */
    @Autowired
    private ServerProvider<?> serverProvider;
    /**
     * provides access to market data services
     */
    @Autowired
    private MarketDataManager marketDataManager;
    /**
     * tracks subscribers by request id
     */
    private final Map<Long,ServiceSubscriber> subscribersByRequestId = Maps.newConcurrentMap();
    /**
     * indicates if the service is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}

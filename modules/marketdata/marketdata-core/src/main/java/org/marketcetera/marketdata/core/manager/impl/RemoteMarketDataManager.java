package org.marketcetera.marketdata.core.manager.impl;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Provides a remote-capable {@link MarketDataManager} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RemoteMarketDataManager
        implements MarketDataManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketData(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received market data request {}",
                               inRequest);
        verifyClientRunning();
        long requestId = marketDataClient.request(inRequest,
                                                  inSubscriber != null);
        if(inSubscriber != null) {
            EventSubscriber eventSubscriber = new EventSubscriber(requestId,
                                                                  inSubscriber);
            subscribersByRequestId.put(requestId,
                                       eventSubscriber);
            eventSubscriber.start();
        }
        SLF4JLoggerProxy.debug(this,
                               "Created market data request id: {}",
                               requestId);
        return requestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketDataSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Event requestMarketDataSnapshot(final Instrument inInstrument,
                                           Content inContent,
                                           String inProvider)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received market data snapshot request {} {} {}",
                               inInstrument,
                               inContent,
                               inProvider);
        verifyClientRunning();
        Deque<Event> events = marketDataClient.getSnapshot(inInstrument,
                                                           inContent,
                                                           inProvider);
        if(events == null || events.isEmpty()) {
            SLF4JLoggerProxy.trace(this,
                                   "No snapshot results");
            return null;
        }
        if(events.size() > 1) {
            SLF4JLoggerProxy.warn(this,
                                  "Snapshot {} has more than one event",
                                  events);
        }
        Event event = events.getFirst();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}",
                               event);
        return event;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#cancelMarketDataRequest(long)
     */
    @Override
    public void cancelMarketDataRequest(long inRequestId)
    {
        try {
            verifyClientRunning();
            marketDataClient.cancel(inRequestId);
            EventSubscriber subscriber = subscribersByRequestId.getIfPresent(inRequestId);
            if(subscriber != null) {
                subscriber.stop();
            }
        } finally {
            subscribersByRequestId.invalidate(inRequestId);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        verifyClientRunning();
        return marketDataClient.getAvailableCapability();
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        marketDataClient = marketDataClientFactory.create(username,
                                                          password,
                                                          hostname,
                                                          port,
                                                          contextClassProvider);
        marketDataClient.start();
        threadPool = Executors.newScheduledThreadPool(threadPoolSize);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        if(threadPool != null) {
            try {
                threadPool.shutdownNow();
            } catch (Exception ignored) {}
            threadPool = null;
        }
        if(marketDataClient != null) {
            marketDataClient.stop();
            marketDataClient = null;
        }
    }
    /**
     * Get the threadPoolSize value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolSize()
    {
        return threadPoolSize;
    }
    /**
     * Sets the threadPoolSize value.
     *
     * @param inThreadPoolSize an <code>int</code> value
     */
    public void setThreadPoolSize(int inThreadPoolSize)
    {
        threadPoolSize = inThreadPoolSize;
    }
    /**
     * Get the eventSubscriptionInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getEventSubscriptionInterval()
    {
        return eventSubscriptionInterval;
    }
    /**
     * Sets the eventSubscriptionInterval value.
     *
     * @param inEventSubscriptionInterval a <code>long</code> value
     */
    public void setEventSubscriptionInterval(long inEventSubscriptionInterval)
    {
        eventSubscriptionInterval = inEventSubscriptionInterval;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the host value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
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
     * Verify that the client is running.
     */
    private void verifyClientRunning()
    {
        if(marketDataClient == null || !marketDataClient.isRunning()) {
            SLF4JLoggerProxy.warn(this,
                                  "The market data remote client is not connected");
            throw new IllegalStateException();
        }
    }
    /**
     * Subscribes to events.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class EventSubscriber
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                SLF4JLoggerProxy.trace(RemoteMarketDataManager.this,
                                       "Retrieving market data events for {}",
                                       requestId);
                verifyClientRunning();
                Deque<Event> events = marketDataClient.getEvents(requestId);
                if(events != null && !events.isEmpty()) {
                    for(Event event : events) {
                        try {
                            if(subscriber.isInteresting(event)) {
                                SLF4JLoggerProxy.trace(RemoteMarketDataManager.this,
                                                       "Publishing {} to {}",
                                                       event,
                                                       requestId);
                                subscriber.publishTo(event);
                            }
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(RemoteMarketDataManager.this,
                                                  e);
                        }
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(RemoteMarketDataManager.this,
                                      e);
            }
        } 
        /**
         * Create a new EventSubscriber instance.
         *
         * @param inRequestId a <code>long</code> value
         * @param inSubscriber an <code>ISubscriber</code> value
         */
        private EventSubscriber(long inRequestId,
                                ISubscriber inSubscriber)
        {
            requestId = inRequestId;
            subscriber = inSubscriber;
        }
        /**
         * Stop the subscription.
         */
        private void stop()
        {
            if(subscriptionToken != null) {
                subscriptionToken.cancel(true);
            }
            subscriptionToken = null;
        }
        /**
         * Start the subscription.
         */
        private void start()
        {
            stop();
            subscriptionToken = threadPool.scheduleAtFixedRate(this,
                                                               eventSubscriptionInterval,
                                                               eventSubscriptionInterval,
                                                               TimeUnit.MILLISECONDS);
        }
        /**
         * request id value
         */
        private final long requestId;
        /**
         * subscriber value
         */
        private final ISubscriber subscriber;
        /**
         * holds the token for the event subscription, if active
         */
        private Future<?> subscriptionToken;
    }
    /**
     * provides context classes for marshalling and unmarshalling
     */
    private ContextClassProvider contextClassProvider;
    /**
     * remote client username
     */
    private String username;
    /**
     * remote client password
     */
    private String password;
    /**
     * remote client host
     */
    private String hostname;
    /**
     * remote client port
     */
    private int port;
    /**
     * thread pool used to retrieve subscription events
     */
    private ScheduledExecutorService threadPool;
    /**
     * size of threadpool
     */
    private int threadPoolSize = 10;
    /**
     * interval in ms at which subscription events are retrieved
     */
    private long eventSubscriptionInterval = 1000;
    /**
     * provides access to market data
     */
    private MarketDataServiceClient marketDataClient;
    /**
     * create the market data client
     */
    @Autowired
    private MarketDataServiceClientFactory marketDataClientFactory;
    /**
     * holds subscribers by request id
     */
    private final Cache<Long,EventSubscriber> subscribersByRequestId = CacheBuilder.newBuilder().build();
}

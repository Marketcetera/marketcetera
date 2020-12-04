package org.marketcetera.marketdata.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.Event;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.event.GeneratedMarketDataEvent;
import org.marketcetera.marketdata.event.MarketDataRequestEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataRequestEvent;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.PageResponse;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Provides market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class MarketDataServiceImpl
        implements MarketDataService,MarketDataCacheManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusPublisher#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void addMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener)
    {
        marketDataStatusListeners.add(inMarketDataStatusListener);
        for(MarketDataStatus cachedStatus : cachedMarketDataStatus.asMap().values()) {
            try {
                inMarketDataStatusListener.receiveMarketDataStatus(cachedStatus);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Reporting market data status",
                                                 e);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusPublisher#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void removeMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener)
    {
        marketDataStatusListeners.remove(inMarketDataStatusListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusBroadcaster#reportMarketDataStatus(org.marketcetera.marketdata.MarketDataStatus)
     */
    @Override
    public void reportMarketDataStatus(MarketDataStatus inMarketDataStatus)
    {
        cachedMarketDataStatus.put(inMarketDataStatus.getProvider(),
                                   inMarketDataStatus);
        for(MarketDataStatusListener listener : marketDataStatusListeners) {
            try {
                listener.receiveMarketDataStatus(inMarketDataStatus);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Reporting market data status",
                                                 e);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataCapabilityBroadcaster#reportCapability(java.util.Collection)
     */
    @Override
    public void reportCapability(Collection<Capability> inCapabilities)
    {
        capabilities.addAll(inCapabilities);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#request(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    public String request(MarketDataRequest inRequest,
                          MarketDataListener inMarketDataListener)
    {
        String provider = inRequest.getProvider();
        String requestId = UUID.randomUUID().toString();
        MarketDataRequestEvent requestEvent = new SimpleMarketDataRequestEvent(inRequest,
                                                                               requestId,
                                                                               provider,
                                                                               inMarketDataListener);
        RequestMetaData requestMetaData = new RequestMetaData(inMarketDataListener);
        requestsByRequestId.put(requestId,
                                requestMetaData);
        SLF4JLoggerProxy.debug(this,
                               "Requesting market data: {}",
                               requestEvent);
        eventBusService.post(requestEvent);
        return requestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#cancel(java.lang.String)
     */
    @Override
    public void cancel(String inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received a cancel request for {}",
                               inRequestId);
        RequestMetaData requestMetaData = requestsByRequestId.getIfPresent(inRequestId);
        requestsByRequestId.invalidate(inRequestId);
        if(requestMetaData == null) {
            throw new IllegalArgumentException("Unknown request: " + inRequestId);
        }
        requestMetaData.setIsActive(false);
        try {
//            SLF4JLoggerProxy.debug(this,
//                                   "Canceling market data request: {}",
//                                   requestMetaData.getDataFlowId());
//            moduleManager.cancel(requestMetaData.getDataFlowId());
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Cancel market data request",
                                             e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent)
    {
        return Lists.newLinkedList(getSnapshot(inInstrument,
                                               inContent,
                                               new PageRequest(0,Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                                     Content inContent,
                                                     PageRequest inPageRequest)
    {
        List<Event> events = Lists.newArrayList();
        synchronized(cacheProviders) {
            for(MarketDataCacheProvider cacheProvider : cacheProviders) {
                Event event = cacheProvider.getSnapshot(inInstrument,
                                                        inContent);
                if(event != null) {
                    if(event instanceof AggregateEvent) {
                        events.addAll(((AggregateEvent)event).decompose());
                    } else {
                        events.add(event);
                    }
                    break;
                }
            }
        }
        CollectionPageResponse<Event> response = new CollectionPageResponse<>();
        List<Event> eventsPage = PageResponse.getPage(events,
                                                      inPageRequest.getPageNumber()+1,
                                                      inPageRequest.getPageSize());
        response.setElements(eventsPage);
        response.setHasContent(!eventsPage.isEmpty());
        response.setPageMaxSize(inPageRequest.getPageSize());
        response.setPageNumber(inPageRequest.getPageNumber());
        response.setPageSize(eventsPage.size());
        // TODO events not sorted!
        response.setSortOrder(inPageRequest.getSortOrder());
        int totalSize = events.size();
        response.setTotalPages(PageResponse.getNumberOfPages(inPageRequest,
                                                             totalSize));
        response.setTotalSize(totalSize);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        return Collections.unmodifiableSet(capabilities);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataCacheManager#addMarketDataCacheProvider(org.marketcetera.marketdata.service.MarketDataCacheProvider)
     */
    @Override
    public void addMarketDataCacheProvider(MarketDataCacheProvider inMarketDataCacheProvider)
    {
        synchronized(cacheProviders) {
            cacheProviders.add(inMarketDataCacheProvider);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataCacheManager#removeMarketDataCacheProvider(org.marketcetera.marketdata.service.MarketDataCacheProvider)
     */
    @Override
    public void removeMarketDataCacheProvider(MarketDataCacheProvider inMarketDataCacheProvider)
    {
        synchronized(cacheProviders) {
            cacheProviders.remove(inMarketDataCacheProvider);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        synchronized(cacheProviders) {
            cacheProviders.forEach(value->value.clear());
        }
    }
    /**
     * Receives generated market data events.
     *
     * @param inMarketDataEvent a <code>GeneratedMarketDataEvent</code> value
     */
    @Subscribe
    public void onGeneratedMarketDataEvent(GeneratedMarketDataEvent inMarketDataEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received {}",
                               inMarketDataEvent);
        RequestMetaData requestMetaData = requestsByRequestId.getIfPresent(inMarketDataEvent.getMarketDataRequestId());
        if(requestMetaData == null) {
            SLF4JLoggerProxy.trace(this,
                                   "No request for {} in {}",
                                   inMarketDataEvent.getMarketDataRequestId(),
                                   requestsByRequestId);
        } else {
            Event event = inMarketDataEvent.getEvent();
            try {
                requestMetaData.doPublish(event);
                SLF4JLoggerProxy.trace(this,
                                       "Published {}",
                                       event);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Cannot publish {}",
                                      event);
            }
        }
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        eventBusService.register(this);
    }
    /**
     * Holds data about the market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RequestMetaData
    {
        /**
         * Publish the given data.
         *
         * @param inData an <code>Object</code> value
         */
        private void doPublish(Object inData)
        {
            if(isActive()) {
                marketDataListener.receiveMarketData((Event)inData);
            }
        }
        /**
         * Get the isActive value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isActive()
        {
            return isActive;
        }
        /**
         * Sets the isActive value.
         *
         * @param inIsActive a <code>boolean</code> value
         */
        private void setIsActive(boolean inIsActive)
        {
            isActive = inIsActive;
        }
        /**
         * Create a new RequestMetaData instance.
         *
         * @param inListener a <code>MarketDataListener</code> value
         */
        private RequestMetaData(MarketDataListener inListener)
        {
            marketDataListener = inListener;
            isActive = true;
        }
        /**
         * indicates if the listener is active or not
         */
        private volatile boolean isActive;
        /**
         * listener value
         */
        private final MarketDataListener marketDataListener;
    }
    /**
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
    /**
     * holds reported capabilities
     */
    private final Set<Capability> capabilities = Sets.newHashSet();
    /**
     * request data by request id
     */
    private final Cache<String,RequestMetaData> requestsByRequestId = CacheBuilder.newBuilder().build();
    /**
     * holds market data status listeners
     */
    private final Set<MarketDataStatusListener> marketDataStatusListeners = Sets.newConcurrentHashSet();
    /**
     * caches last-known market data status values
     */
    private final Cache<String,MarketDataStatus> cachedMarketDataStatus = CacheBuilder.newBuilder().build();
    /**
     * holds market data cache providers
     */
    private final Set<MarketDataCacheProvider> cacheProviders = Sets.newConcurrentHashSet();
}

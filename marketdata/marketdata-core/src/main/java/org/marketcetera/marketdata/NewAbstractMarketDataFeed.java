package org.marketcetera.marketdata;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.management.ObjectName;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.notifications.NotificationExecutor;
import org.marketcetera.event.Event;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.marketdata.event.CancelMarketDataRequestEvent;
import org.marketcetera.marketdata.event.MarketDataRequestEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataFeedStatusEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataRequestAcceptedEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataRequestCanceledEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataRequestRejectedEvent;
import org.marketcetera.metrics.MetricService;
import org.marketcetera.module.DisplayName;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Provides common behaviors for market data feeds.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class NewAbstractMarketDataFeed
        implements MarketDataModuleMXBean
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public final void start()
    {
        SLF4JLoggerProxy.info(this,
                              "{} starting",
                              serviceName);
        try {
            onStart();
            ManagementFactory.getPlatformMBeanServer().registerMBean(this,
                                                                     new ObjectName(JMX_NAME));
            eventBusService.register(this);
            onUpdatedFeedStatus(FeedStatus.AVAILABLE);
            SLF4JLoggerProxy.info(this,
                                  "{} started",
                                  serviceName);
        } catch (Exception e) {
            onUpdatedFeedStatus(FeedStatus.ERROR);
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "{} failed to start",
                                  serviceName);
        }
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public final void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              serviceName);
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(JMX_NAME));
        } catch (Exception ignored) {}
        try {
            eventBusService.unregister(this);
        } catch (Exception ignored) {}
        for(String metricName : metricNames) {
            try {
                metricsService.getMetrics().remove(metricName);
            } catch (Exception ignored) {}
        }
        metricNames.clear();
        try {
            onStop();
        } catch (Exception e) {
            
        }
        SLF4JLoggerProxy.info(this,
                              "{} stopped",
                              serviceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleMXBean#getFeedStatus()
     */
    @Override
    @DisplayName("The feed status for the market data feed")
    public String getFeedStatus()
    {
        if(feedStatus == null) {
            return FeedStatus.UNKNOWN.name();
        }
        return feedStatus.name();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleMXBean#disconnect()
     */
    @Override
    @DisplayName("Causes the feed to disconnect")
    public void disconnect()
    {
        stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleMXBean#reconnect()
     */
    @Override
    @DisplayName("Causes the feed to resubmit existing queries")
    public void reconnect()
    {
        stop();
        start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleMXBean#getCapabilities()
     */
    @Override
    @DisplayName("The set of capabilities for this feed")
    public Set<Capability> getCapabilities()
    {
        return doGetCapabilities();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleMXBean#getAssetClasses()
     */
    @Override
    @DisplayName("The set of supported asset classes for this feed")
    public Set<AssetClass> getAssetClasses()
    {
        return doGetAssetClasses();
    }
    /**
     * Handle the given market data request.
     *
     * @param inEvent a <code>MarketDataRequestEvent</code> value
     */
    @Subscribe
    public void onMarketDataRequest(MarketDataRequestEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Processing market data request: {}",
                               inEvent);
        boolean accepted = true;
        String reason = null;
        MarketDataRequest request = inEvent.getMarketDataRequest();
        String requestId = inEvent.getMarketDataRequestId();
        MarketDataListener listener = inEvent.getMarketDataListener();
        String thisProvider = getProviderName();
        try {
            if(inEvent.getMarketDataRequestProvider().isPresent()) {
                String requestedProvider = inEvent.getMarketDataRequestProvider().get();
                if(thisProvider.equalsIgnoreCase(requestedProvider)) {
                    SLF4JLoggerProxy.debug(this,
                                           "Request provider {} matches our provider {}, accepted",
                                           requestedProvider,
                                           thisProvider);
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "Request provider {} does not match our provider {}, rejected",
                                           requestedProvider,
                                           thisProvider);
                    accepted = false;
                    reason = "request not from " + thisProvider;
                    return;
                }
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Request has no specific provider, accepted");
            }
            if(feedStatus.isRunning()) {
                final RequestData requestData = new RequestData(listener);
                synchronized(requestDataByRequestId) {
                    requestDataByRequestId.put(requestId,
                                               requestData);
                }
                // TODO have to handle symbols vs underlying symbols for options and dividends
                for(String symbol : request.getSymbols()) {
                    Instrument instrument = symbolResolverService.resolveSymbol(symbol);
                    if(instrument == null) {
                        I18NBoundMessage message = new I18NBoundMessage1P(Messages.INVALID_SYMBOLS,
                                                                          symbol);
                        message.warn(this);
                        listener.onError(message);
                    } else {
                        for(Content content : request.getContent()) {
                            Capability requiredCapability = content.getAsCapability();
                            if(getCapabilities().contains(requiredCapability)) {
                                String exchange = request.getExchange();
                                MarketDataSubRequest subRequest = new MarketDataSubRequest(instrument,
                                                                                           content,
                                                                                           exchange);
                                requestDataBySubRequest.getUnchecked(subRequest).add(requestData);
                                // TODO reference counting
                                try {
                                    SLF4JLoggerProxy.debug(this,
                                                           "Submitting {} to {}",
                                                           subRequest,
                                                           getProviderName());
                                    synchronized(requestData) {
                                        requestData.getSubRequests().add(subRequest);
                                    }
                                    doMarketDataRequest(request,
                                                        subRequest);
                                } catch (Exception e) {
                                    SLF4JLoggerProxy.warn(this,
                                                          e,
                                                          "Unable to submit market data sub-request {} for request {} to {}",
                                                          subRequest,
                                                          request,
                                                          getProviderName());
                                    listener.onError(e);
                                    synchronized(requestData) {
                                        requestData.getSubRequests().remove(subRequest);
                                    }
                                }
                            } else {
                                I18NBoundMessage message = new I18NBoundMessage1P(Messages.INVALID_CONTENT,
                                                                                  content);
                                message.warn(this);
                                listener.onError(message);
                            }
                        }
                    }
                }
            } else {
                throw new MarketDataException(new I18NBoundMessage1P(Messages.FEED_NOT_AVAILABLE,
                                                                     feedStatus));
            }
        } catch (Exception e) {
            reason = ExceptionUtils.getRootCauseMessage(e);
            accepted = false;
            SLF4JLoggerProxy.warn(this,
                                  "Market data request failed: {}",
                                  ExceptionUtils.getRootCauseMessage(e));
            throw new RequestDataException(e);
        } finally {
            if(accepted) {
                eventBusService.post(new SimpleMarketDataRequestAcceptedEvent(request,
                                                                              requestId,
                                                                              thisProvider));
            } else {
                eventBusService.post(new SimpleMarketDataRequestRejectedEvent(request,
                                                                              requestId,
                                                                              thisProvider,
                                                                              reason));
            }
        }
    }
    /**
     * Cancel the given market data request.
     *
     * @param inEvent a <code>CanceclMarketDataRequestEvent</code> value
     */
    @Subscribe
    public void onCancel(CancelMarketDataRequestEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} processing market data cancel: {}",
                               serviceName,
                               inEvent);
        final String requestId = inEvent.getMarketDataRequestId();
        final RequestData requestData;
        synchronized(requestDataByRequestId) {
            requestData = requestDataByRequestId.remove(requestId);
        }
        if(requestData == null) {
            SLF4JLoggerProxy.warn(this,
                                  "{} no request data for {}",
                                  serviceName,
                                  requestId);
            return;
        }
        try {
            final Collection<MarketDataSubRequest> subRequests;
            synchronized(requestData) {
                subRequests = Lists.newArrayList(requestData.getSubRequests());
                requestData.getSubRequests().clear();
            }
            for(MarketDataSubRequest subRequest : subRequests) {
                try {
                    final Set<RequestData> requestDataForSubRequest = requestDataBySubRequest.getIfPresent(subRequest);
                    if(requestDataForSubRequest == null) {

                    } else {
                        synchronized(requestDataForSubRequest) {
                            requestDataForSubRequest.remove(requestData);
                        }
                    }
                    SLF4JLoggerProxy.debug(this,
                                           "{} cancelling {} for {}",
                                           serviceName,
                                           subRequest,
                                           requestId);
                    doCancel(subRequest);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "{} error processing cancel of {} for {}, ignoring",
                                          serviceName,
                                          subRequest,
                                          requestId);
                }
            }
        } finally {
            eventBusService.post(new SimpleMarketDataRequestCanceledEvent(requestId,
                                                                          getProviderName()));
        }
    }
    /**
     * Get the symbolResolverService value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    public SymbolResolverService getSymbolResolverService()
    {
        return symbolResolverService;
    }
    /**
     * Sets the symbolResolverService value.
     *
     * @param inSymbolResolverService a <code>SymbolResolverService</code> value
     */
    public void setSymbolResolverService(SymbolResolverService inSymbolResolverService)
    {
        symbolResolverService = inSymbolResolverService;
    }
    /**
     * Indicates that the feed status has changed.
     *
     * @param inNewFeedStatus a <code>FeedStatus</code> value
     */
    protected void onUpdatedFeedStatus(FeedStatus inNewFeedStatus)
    {
        if(inNewFeedStatus == feedStatus) {
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Setting feed status from {} to {}",
                               feedStatus,
                               inNewFeedStatus);
        feedStatus = inNewFeedStatus;
        sendFeedStatus();
    }
    /**
     * Execute the given market data sub-request with the market data request provided as context.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inMarketDataSubRequest a <code>MarketDataSubRequest</code> value
     */
    protected abstract void doMarketDataRequest(MarketDataRequest inMarketDataRequest,
                                                MarketDataSubRequest inMarketDataSubRequest);
    /**
     * Cancel the request identified by this sub-request.
     *
     * @param inMarketDataSubRequest a <code>MarketDataSubRequest</code> value
     */
    protected abstract void doCancel(MarketDataSubRequest inMarketDataSubRequest);
    /**
     * Get the capabilities of this feed.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    protected abstract Set<Capability> doGetCapabilities();
    /**
     * Get the asset classes supported by this feed.
     *
     * @return a <code>Set&lt;AssetClass&gt;</code> value
     */
    protected abstract Set<AssetClass> doGetAssetClasses();
    /**
     * Get the human-readable unique provider name.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getProviderName();
    /**
     * Post events created for the given sub-request.
     *
     * @param inMarketDataSubRequest a <code>MarketDataSubRequest</code> value
     * @param inEvents an <code>Event...</code> value
     */
    protected void postEvents(MarketDataSubRequest inMarketDataSubRequest,
                              Event...inEvents)
    {
        final Set<RequestData> requestersToNotify = Sets.newHashSet();
        Set<RequestData> interestedRequesters = requestDataBySubRequest.getIfPresent(inMarketDataSubRequest);
        if(interestedRequesters != null) {
            requestersToNotify.addAll(interestedRequesters);
        }
        inMarketDataSubRequest.setExchange(MarketDataSubRequest.ALL_EXCHANGES);
        Set<RequestData> interestedRequestersNoExchange = requestDataBySubRequest.getIfPresent(inMarketDataSubRequest);
        if(interestedRequestersNoExchange != null) {
            requestersToNotify.addAll(interestedRequestersNoExchange);
        }
        if(requestersToNotify.isEmpty()) {
            SLF4JLoggerProxy.warn(this,
                                  "{} received {} event(s) for {}, but no subscribers are interested",
                                  serviceName,
                                  inEvents.length,
                                  inMarketDataSubRequest);
            return;
        }
        for(RequestData requestData : requestersToNotify) {
            MarketDataListener listener = requestData.getMarketDataListener();
            for(Event event : inEvents) {
                try {
                    listener.receiveMarketData(event);
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        }
    }
    /**
     * Called when the feed stops.
     *
     * @throws Exception if an error occurs stopping the feed
     */
    protected void onStop()
            throws Exception {}
    /**
     * Called when the feed starts.
     *
     * @throws Exception if the feed cannot be started
     */
    protected void onStart()
            throws Exception {}
    /**
     * Send the feed status to all interested subscribers.
     */
    private void sendFeedStatus()
    {
        eventBusService.post(new SimpleMarketDataFeedStatusEvent(feedStatus,
                                                                 getProviderName()));
    }
    /**
     * Stores meta data for each request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected class RequestData
    {
        /**
         * Create a new RequestData instance.
         *
         * @param inMarketDataListener
         */
        private RequestData(MarketDataListener inMarketDataListener)
        {
            marketDataListener = inMarketDataListener;
        }
        /**
         * Market data listener to which to deliver events.
         *
         * @return a <code>MarketDataListener</code> value
         */
        private MarketDataListener getMarketDataListener()
        {
            return marketDataListener;
        }
        /**
         * Get the sub-requests for this request.
         *
         * @return a <code>Collection&lt;MarketDataSubRequest&gt;</code> value
         */
        private Collection<MarketDataSubRequest> getSubRequests()
        {
            return subRequests;
        }
        /**
         * listener for market data
         */
        private final MarketDataListener marketDataListener;
        /**
         * sub-requests for this request
         */
        private final Collection<MarketDataSubRequest> subRequests = Lists.newArrayList();
    }
    /**
     * stores requesters by the sub-request in which they are interested
     */
    private final LoadingCache<MarketDataSubRequest,Set<RequestData>> requestDataBySubRequest = CacheBuilder.newBuilder().build(new CacheLoader<MarketDataSubRequest,Set<RequestData>>() {
        @Override
        public Set<RequestData> load(MarketDataSubRequest inKey)
                throws Exception
        {
            return Sets.newHashSet();
        }}
    );
    /**
     * stores request data by market data request id
     */
    private final Map<String,RequestData> requestDataByRequestId = Maps.newHashMap();
    /**
     * provides a unique JMX {@link ObjectName} for each market data provider
     */
    private final String JMX_NAME = getClass().getCanonicalName() + ":type=mdata";
    /**
     * current status of the feed
     */
    private volatile FeedStatus feedStatus = FeedStatus.OFFLINE;
    /**
     * human-readable name of this service
     */
    protected final String serviceName = PlatformServices.getServiceName(getClass());
    /**
     * names of metricsService created by this feed
     */
    protected final List<String> metricNames = new ArrayList<>();
    /**
     * resolves symbols to instruments
     */
    @Autowired
    protected SymbolResolverService symbolResolverService;
    /**
     * provides notifications, if specified
     */
    @Autowired(required=false)
    protected NotificationExecutor notificationExecutor;
    /**
     * provides metrics services
     */
    @Autowired
    protected MetricService metricsService;
    /**
     * provides access to event bus services
     */
    @Autowired
    protected EventBusService eventBusService;
}

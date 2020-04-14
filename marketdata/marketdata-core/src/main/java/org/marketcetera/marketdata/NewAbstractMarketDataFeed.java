package org.marketcetera.marketdata;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import javax.management.ObjectName;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.notifications.NotificationExecutor;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.marketdata.event.CancelMarketDataRequestEvent;
import org.marketcetera.marketdata.event.MarketDataRequestEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataFeedStatusEvent;
import org.marketcetera.marketdata.event.SimpleMarketDataRequestAcceptedEvent;
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
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
                Map<String,RequestData> requestDataByRequestId = Maps.newHashMap();
                RequestData requestData = new RequestData(request,
                                                          requestId,
                                                          listener);
                requestDataByRequestId.put(requestId,
                                           requestData);
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
                                MarketDataRequestKey requestKey = new MarketDataRequestKey(instrument,
                                                                                           content,
                                                                                           exchange);
                            } else {
                                I18NBoundMessage message = new I18NBoundMessage1P(Messages.INVALID_CONTENT,
                                                                                  content);
                                message.warn(this);
                                listener.onError(message);
                            }
                        }
                    }
                }
                doMarketDataRequest(request,
                                    requestId,
                                    listener);
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
                               "Processing market data cancel: {}",
                               inEvent);
        doCancel(inEvent.getMarketDataRequestId());
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
     * Send the feed status to all interested subscribers.
     */
    private void sendFeedStatus()
    {
        eventBusService.post(new SimpleMarketDataFeedStatusEvent(feedStatus,
                                                                 getProviderName()));
    }
    protected abstract void doMarketDataRequest(MarketDataRequest inMarketDataRequest,
                                                String inMarketDataRequestId,
                                                MarketDataListener inMarketDataListener);
    protected abstract void doCancel(String inMarketDataRequestId);
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
    protected void onStop()
            throws Exception
    {
        
    }
    protected void onStart()
            throws Exception
    {
        
    }
    protected abstract Set<Capability> doGetCapabilities();
    protected abstract Set<AssetClass> doGetAssetClasses();
    protected abstract String getProviderName();
    @GuardedBy("requestDataByKey")
    private final Multimap<MarketDataRequestKey,RequestData> requestDataByKey = HashMultimap.create();
    protected class RequestData
    {
        /**
         * Create a new RequestData instance.
         *
         * @param inMarketDataRequest
         * @param inMarketDataRequestId
         * @param inMarketDataListener
         */
        private RequestData(MarketDataRequest inMarketDataRequest,
                            String inMarketDataRequestId,
                            MarketDataListener inMarketDataListener)
        {
            marketDataRequest = inMarketDataRequest;
            marketDataRequestId = inMarketDataRequestId;
            marketDataListener = inMarketDataListener;
        }
        private final MarketDataRequest marketDataRequest;
        private final String marketDataRequestId;
        private final MarketDataListener marketDataListener;
    }
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

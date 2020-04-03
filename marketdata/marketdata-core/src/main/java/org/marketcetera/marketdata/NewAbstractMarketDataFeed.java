package org.marketcetera.marketdata;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 *
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

package org.marketcetera.marketdata.core.manager;

import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Provides a common module for remote market data flows.
 *
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Requester</td></tr>
 * <tr><th>Stops data flows</th><td>Yes</td></tr>
 * <tr><th>Start Operation</th><td>None</td></tr>
 * <tr><th>Stop Operation</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>&nbsp;</td></tr>
 * <tr><th>MX Notification</th><td>&nbsp;</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRemoteModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowId,
                       RequestID inRequestId)
    {
        RequestSubscriber subscriber = subscriberCache.getIfPresent(inFlowId);
        if(subscriber != null) {
            subscriber.stop();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        super.preStart();
        Validate.notNull(config);
        threadpool = Executors.newScheduledThreadPool(config.getThreadpoolSize());
        subscriberCache.invalidateAll();
        marketDataServiceClient = marketDataServiceClientFactory.create(config.getUsername(),
                                                                        config.getPassword(),
                                                                        config.getHostname(),
                                                                        config.getPort(),
                                                                        config.getContextClassProvider());
        marketDataServiceClient.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        if(marketDataServiceClient != null) {
            try {
                marketDataServiceClient.stop();
            } catch (Exception ignored) {}
            marketDataServiceClient = null;
        }
        super.preStop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onRequestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected void onRequestData(DataRequest inRequest,
                                 DataEmitterSupport inSupport)
            throws RequestDataException
    {
        try {
            Object payload = inRequest.getData();
            SLF4JLoggerProxy.debug(this,
                                   "Received market data request payload: {}",
                                   payload);
            MarketDataRequest marketDataRequest;
            if(payload instanceof MarketDataRequest) {
                marketDataRequest = (MarketDataRequest)payload;
            } else if(payload instanceof String) {
                marketDataRequest = MarketDataRequestBuilder.newRequestFromString(String.valueOf(payload));
            } else {
                throw new IllegalRequestParameterValue(new I18NBoundMessage1P(Messages.UNKNOWN_MARKETDATA_CONTENT,
                                                                              String.valueOf(payload)));
            }
            SLF4JLoggerProxy.debug(this,
                                   "Submitting market data request: {}",
                                   marketDataRequest);
            long requestId = marketDataServiceClient.request(marketDataRequest,
                                                             true);
            SLF4JLoggerProxy.debug(this,
                                   "Market data request returned id: {}",
                                   requestId);
            RequestSubscriber subscriber = new RequestSubscriber(inSupport,
                                                                 requestId);
            subscriberCache.put(inSupport.getFlowID(),
                                subscriber);
            subscriber.start();
        } catch (RequestDataException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw e;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new RequestDataException(e);
        }
    }
    /**
     * Create a new MarketDataManagerModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    MarketDataRemoteModule(ModuleURN inURN)
    {
        super(inURN,
              false);
    }
    /**
     * Verify that the market data client is available.
     */
    private void verifyClientAvailable()
    {
        if(marketDataServiceClient != null && marketDataServiceClient.isRunning()) {
            return;
        }
        if(marketDataServiceClient == null) {
            throw new IllegalStateException("Module has been stopped");
        }
        marketDataServiceClient.start();
        if(!marketDataServiceClient.isRunning()) {
            throw new IllegalStateException("Client has stopped and cannot be restarted");
        }
    }
    /**
     * Holds information about request subscribers and manages market data events.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class RequestSubscriber
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                verifyClientAvailable();
                SLF4JLoggerProxy.debug(MarketDataRemoteModule.this,
                                       "Retrieving market data events for {}",
                                       requestId);
                Deque<Event> events = marketDataServiceClient.getEvents(requestId);
                if(events == null || events.isEmpty()) {
                    SLF4JLoggerProxy.debug(MarketDataRemoteModule.this,
                                           "No market data events for {}",
                                           requestId);
                } else {
                    SLF4JLoggerProxy.debug(MarketDataRemoteModule.this,
                                           "Retrieved {} market data event(s) for {}",
                                           events.size(),
                                           requestId);
                    for(Event event : events) {
                        try {
                            dataEmitterSupport.send(event);
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(MarketDataRemoteModule.this,
                                                  e);
                        }
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(MarketDataRemoteModule.this,
                                      e);
            }
        }
        /**
         * Start the request subscriber.
         */
        private void start()
        {
            requestSubscriberToken = threadpool.scheduleAtFixedRate(this,
                                                                    config.getPollingInterval(),
                                                                    config.getPollingInterval(),
                                                                    TimeUnit.MILLISECONDS);
        }
        /**
         * Stop the request subscriber.
         */
        private void stop()
        {
            if(requestSubscriberToken != null) {
                requestSubscriberToken.cancel(true);
                requestSubscriberToken = null;
            }
        }
        /**
         * Create a new RequestSubscriber instance.
         *
         * @param inDataEmitterSupport a <code>DataEmitterSupport</code> value
         * @param inRequestId a <code>long</code> value
         */
        private RequestSubscriber(DataEmitterSupport inDataEmitterSupport,
                                  long inRequestId)
        {
            dataEmitterSupport = inDataEmitterSupport;
            requestId = inRequestId;
        }
        /**
         * holds the job token for request checking, if any
         */
        private Future<?> requestSubscriberToken;
        /**
         * data emitter support value
         */
        private final DataEmitterSupport dataEmitterSupport;
        /**
         * request id value
         */
        private final long requestId;
    }
    /**
     * holds subscribers by data flow id
     */
    private final Cache<DataFlowID,RequestSubscriber> subscriberCache = CacheBuilder.newBuilder().build();
    /**
     * used to schedule market data request updates
     */
    private ScheduledExecutorService threadpool;
    /**
     * provides access to remote market data services
     */
    private MarketDataServiceClient marketDataServiceClient;
    /**
     * creates {@link MarketDataServiceClient} objects
     */
    @Autowired
    private MarketDataServiceClientFactory marketDataServiceClientFactory;
    /**
     * optional config for the module
     */
    @Autowired
    private MarketDataRemoteModuleConfig config;
}

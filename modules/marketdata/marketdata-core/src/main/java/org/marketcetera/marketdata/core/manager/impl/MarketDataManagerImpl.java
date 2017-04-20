package org.marketcetera.marketdata.core.manager.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.CapabilityCollection;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.MarketDataProvider;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.MarketDataManagerModule;
import org.marketcetera.marketdata.core.manager.MarketDataProviderRegistry;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Routes market data requests to available providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerImpl
        implements MarketDataManager,MarketDataProviderRegistry
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketData(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber)
    {
        if(useModuleFramework) {
            initMarketDataManagerModule();
            return marketDataManagerModule.requestMarketData(inRequest,
                                                             inSubscriber);
        } else {
            Token token = new Token(inRequest,
                                    inSubscriber);
            marketDataRequests.put(token.getId(),
                                   token);
            for(Map.Entry<MarketDataProvider,ProviderStatus> entry : providerStatus.asMap().entrySet()) {
                MarketDataProvider provider = entry.getKey();
                ProviderStatus status = entry.getValue();
                try {
                    if(status.isRunning()) {
                        provider.requestMarketData(token);
                        token.getMarketDataProviders().add(provider);
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            }
            if(token.getMarketDataProviders().isEmpty()) {
                marketDataRequests.invalidate(token.getId());
                throw new NoMarketDataProvidersAvailable();
            }
            return token.getId();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#cancelMarketDataRequest(long)
     */
    @Override
    public void cancelMarketDataRequest(long inRequestId)
    {
        if(useModuleFramework) {
            initMarketDataManagerModule();
            marketDataManagerModule.cancelMarketDataRequest(inRequestId);
        } else {
            Token token = marketDataRequests.getIfPresent(inRequestId);
            marketDataRequests.invalidate(inRequestId);
            if(token == null) {
                SLF4JLoggerProxy.warn(this,
                                      "No market data request for id: {}",
                                      inRequestId);
            } else {
                for(MarketDataProvider provider : token.getMarketDataProviders()) {
                    try {
                        provider.cancelMarketDataRequest(token);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e);
                    }
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketDataSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Event requestMarketDataSnapshot(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider)
    {
        SLF4JLoggerProxy.debug(this,
                               "Requesting market data snapshot: {} {} {}",
                               inInstrument,
                               inContent,
                               inProvider);
        if(useModuleFramework) {
            initMarketDataManagerModule();
            Event snapshot = marketDataManagerModule.requestMarketDataSnapshot(inInstrument,
                                                                               inContent,
                                                                               inProvider);
            SLF4JLoggerProxy.debug(this,
                                   "Returning market data snapshot: {} {} {}: {}",
                                   inInstrument,
                                   inContent,
                                   inProvider,
                                   snapshot);
            return snapshot;
        } else {
            for(Map.Entry<MarketDataProvider,ProviderStatus> entry : providerStatus.asMap().entrySet()) {
                MarketDataProvider provider = entry.getKey();
                ProviderStatus status = entry.getValue();
                try {
                    if(status.isRunning()) {
                        if(inProvider == null || inProvider.equals(provider.getProviderName())) {
                            return provider.getSnapshot(inInstrument,
                                                        inContent);
                        }
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            }
            throw new NoMarketDataProvidersAvailable();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        return CapabilityCollection.getReportedCapabilities();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataProviderRegistry#setStatus(org.marketcetera.marketdata.core.MarketDataProvider, org.marketcetera.marketdata.core.ProviderStatus)
     */
    @Override
    public void setStatus(MarketDataProvider inProvider,
                          ProviderStatus inStatus)
    {
        SLF4JLoggerProxy.info(this,
                              "{} reports {}",
                              inProvider,
                              inStatus);
        providerStatus.put(inProvider,
                           inStatus);
    }
    /**
     * Get the subscriberTimeout value.
     *
     * @return a <code>long</code> value
     */
    public long getSubscriberTimeout()
    {
        return subscriberTimeout;
    }
    /**
     * Sets the subscriberTimeout value.
     *
     * @param a <code>long</code> value
     */
    public void setSubscriberTimeout(long inSubscriberTimeout)
    {
        subscriberTimeout = inSubscriberTimeout;
    }
    /**
     * Get the defaultMarketDataProvider value.
     *
     * @return a <code>String</code> value
     */
    public String getDefaultMarketDataProvider()
    {
        return defaultMarketDataProvider;
    }
    /**
     * Sets the defaultMarketDataProvider value.
     *
     * @param a <code>String</code> value
     */
    public void setDefaultMarketDataProvider(String inDefaultMarketDataProvider)
    {
        defaultMarketDataProvider = inDefaultMarketDataProvider;
    }
    /**
     * Get the useModuleFramework value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getUseModuleFramework()
    {
        return useModuleFramework;
    }
    /**
     * Sets the useModuleFramework value.
     *
     * @param inUseModuleFramework a <code>boolean</code> value
     */
    public void setUseModuleFramework(boolean inUseModuleFramework)
    {
        useModuleFramework = inUseModuleFramework;
    }
    /**
     * Initialize the market data manager module, if necessary. 
     */
    private synchronized void initMarketDataManagerModule()
    {
        if(marketDataManagerModule == null) {
            marketDataManagerModule = MarketDataManagerModule.getInstance();
            Validate.notNull(marketDataManagerModule);
            marketDataManagerModule.setSubscriberTimeout(subscriberTimeout);
            marketDataManagerModule.setDefaultProvider(defaultMarketDataProvider);
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static final class Token
            implements MarketDataRequestToken
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.request.MarketDataRequestToken#getId()
         */
        @Override
        public long getId()
        {
            return id;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.request.MarketDataRequestToken#getSubscriber()
         */
        @Override
        public ISubscriber getSubscriber()
        {
            return subscriber;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.request.MarketDataRequestToken#getRequest()
         */
        @Override
        public MarketDataRequest getRequest()
        {
            return marketDataRequest;
        }
        
        /**
         * Get the marketDataProviders value.
         *
         * @return a <code>Set&lt;MarketDataProvider&gt;</code> value
         */
        private Set<MarketDataProvider> getMarketDataProviders()
        {
            return marketDataProviders;
        }
        /**
         * Create a new Token instance.
         *
         * @param inMarketDataRequest
         * @param inSubscriber
         */
        private Token(MarketDataRequest inMarketDataRequest,
                      ISubscriber inSubscriber)
        {
            marketDataRequest = inMarketDataRequest;
            subscriber = inSubscriber;
        }
        /**
         * 
         */
        private final Set<MarketDataProvider> marketDataProviders = Sets.newHashSet();
        /**
         * 
         */
        private final MarketDataRequest marketDataRequest;
        /**
         * 
         */
        private final ISubscriber subscriber;
        /**
         * 
         */
        private final long id = counter.incrementAndGet();
        /**
         * 
         */
        private static final long serialVersionUID = 622142012940134611L;
        /**
         * 
         */
        private static final AtomicLong counter = new AtomicLong(0);
    }
    /**
     * indicate whether the service should use the module framework or the provider framework
     */
    private boolean useModuleFramework = true;
    /**
     * time to wait for a subscriber to become available before timing out
     */
    private long subscriberTimeout = 500;
    /**
     * provides an entry point into the module system
     */
    private MarketDataManagerModule marketDataManagerModule;
    /**
     * default market data provider
     */
    private String defaultMarketDataProvider = "exsim";
    /**
     * 
     */
    private final Cache<MarketDataProvider,ProviderStatus> providerStatus = CacheBuilder.newBuilder().build();
    /**
     * 
     */
    private final Cache<Long,Token> marketDataRequests = CacheBuilder.newBuilder().build();
}

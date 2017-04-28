package org.marketcetera.marketdata.core.manager.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitListener;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.OrderModifier;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TopOfBookEvent;
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
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Order;
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
        implements MarketDataManager,MarketDataProviderRegistry,ClientInitListener
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
     * @see org.marketcetera.client.ClientInitListener#receiveClient(org.marketcetera.client.Client)
     */
    @Override
    public void receiveClient(Client inClient)
    {
        client = inClient;
        if(enablePegToMidpoint) {
            client.addOrderModifier(new PegToMidpointOrderModifier());
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
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        ClientManager.addClientInitListener(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        ClientManager.removeClientInitListener(this);
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
     * Get the enablePegToMidpoint value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getEnablePegToMidpoint()
    {
        return enablePegToMidpoint;
    }
    /**
     * Sets the enablePegToMidpoint value.
     *
     * @param inEnablePegToMidpoint a <code>boolean</code> value
     */
    public void setEnablePegToMidpoint(boolean inEnablePegToMidpoint)
    {
        enablePegToMidpoint = inEnablePegToMidpoint;
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
     * Holds information for a market data request.
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
         * @param inMarketDataRequest a <code>MarketDataRequest</code> value
         * @param inSubscriber an <code>ISubscriber</code> value
         */
        private Token(MarketDataRequest inMarketDataRequest,
                      ISubscriber inSubscriber)
        {
            marketDataRequest = inMarketDataRequest;
            subscriber = inSubscriber;
        }
        /**
         * market data providers subscribed to
         */
        private final Set<MarketDataProvider> marketDataProviders = Sets.newHashSet();
        /**
         * market data request
         */
        private final MarketDataRequest marketDataRequest;
        /**
         * subscriber object used to return results
         */
        private final ISubscriber subscriber;
        /**
         * id of this token
         */
        private final long id = counter.incrementAndGet();
        /**
         * used to generate unique token ids
         */
        private static final AtomicLong counter = new AtomicLong(0);
        private static final long serialVersionUID = 622142012940134611L;
    }
    /**
     * Modifies outgoing orders to set peg-to-midpoint price, if necessary.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class PegToMidpointOrderModifier
            implements OrderModifier
    {
        /* (non-Javadoc)
         * @see org.marketcetera.client.OrderModifier#modify(org.marketcetera.trade.Order)
         */
        @Override
        public boolean modify(Order inOrder)
        {
            boolean modified = false;
            if(inOrder instanceof NewOrReplaceOrder) {
                NewOrReplaceOrder newOrReplaceOrder = (NewOrReplaceOrder)inOrder;
                if(newOrReplaceOrder.getPegToMidpoint()) {
                    Event marketData = requestMarketDataSnapshot(newOrReplaceOrder.getInstrument(),
                                                                 Content.TOP_OF_BOOK,
                                                                 null);
                    if(marketData == null) {
                        throw new IllegalArgumentException("No market data available for " + newOrReplaceOrder.getInstrument().getFullSymbol());
                    }
                    TopOfBookEvent topOfBook = (TopOfBookEvent)marketData;
                    BidEvent bid = topOfBook.getBid();
                    AskEvent ask = topOfBook.getAsk();
                    if(bid == null || ask == null) {
                        throw new IllegalArgumentException("Insufficient liquidity to peg-to-midpoint for " + newOrReplaceOrder.getInstrument().getFullSymbol());
                    }
                    BigDecimal totalPrice = bid.getPrice().add(ask.getPrice());
                    BigDecimal newPrice = totalPrice.divide(new BigDecimal(2)).setScale(6,RoundingMode.HALF_UP);
                    newOrReplaceOrder.setPrice(newPrice);
                    newOrReplaceOrder.setPegToMidpoint(false);
                    modified = true;
                    SLF4JLoggerProxy.info(MarketDataManagerImpl.this,
                                          "Repricing {} to {}",
                                          newOrReplaceOrder.getOrderID(),
                                          newPrice);
                }
            }
            return modified;
        }
    }
    /**
     * provides access to trading client services.
     */
    private Client client;
    /**
     * indicates if peg-to-midpoint should be enabled
     */
    private boolean enablePegToMidpoint = true;
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
     * caches provider status by provider
     */
    private final Cache<MarketDataProvider,ProviderStatus> providerStatus = CacheBuilder.newBuilder().build();
    /**
     * caches market data requests by token id
     */
    private final Cache<Long,Token> marketDataRequests = CacheBuilder.newBuilder().build();
}

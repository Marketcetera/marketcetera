package org.marketcetera.marketdata.core.manager.impl;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitListener;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.MarketDataRequestListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.CapabilityCollection;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.MarketDataManagerModule;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Routes market data requests to available providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerImpl
        implements MarketDataManager,MarketDataRequestListener,ClientInitListener
{
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
        client.removeMarketDataRequestListener(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketData(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber)
    {
        initMarketDataManagerModule();
        return marketDataManagerModule.requestMarketData(inRequest,
                                                         inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#cancelMarketDataRequest(long)
     */
    @Override
    public void cancelMarketDataRequest(long inRequestId)
    {
        initMarketDataManagerModule();
        marketDataManagerModule.cancelMarketDataRequest(inRequestId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketDataSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Event requestMarketDataSnapshot(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider)
    {
        initMarketDataManagerModule();
        return marketDataManagerModule.requestMarketDataSnapshot(inInstrument,
                                                                 inContent,
                                                                 inProvider);
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
     * @see org.marketcetera.client.MarketDataRequestListener#receiveMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public void receiveMarketDataRequest(final MarketDataRequest inMarketDataRequest)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received server market data request: {}",
                               inMarketDataRequest);
        ISubscriber subscriber = new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return inData instanceof Event;
            }
            @Override
            public void publishTo(Object inData)
            {
                Event event = (Event)inData;
                SLF4JLoggerProxy.trace(MarketDataManagerImpl.this,
                                       "Sending MDR {}: {}",
                                       inMarketDataRequest.getRequestId(),
                                       event);
                client.sendEvent(event);
            }
        };
        // TODO need to be able to handle cancels, too
        requestMarketData(inMarketDataRequest,
                          subscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientInitListener#receiveClient(org.marketcetera.client.Client)
     */
    @Override
    public void receiveClient(Client inClient)
    {
        client = inClient;
        client.addMarketDataRequestListener(this);
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
     * Initialize the market data manager module, if necessary. 
     */
    private synchronized void initMarketDataManagerModule()
    {
        if(marketDataManagerModule == null) {
            marketDataManagerModule = MarketDataManagerModule.getInstance();
            Validate.notNull(marketDataManagerModule);
            marketDataManagerModule.setSubscriberTimeout(subscriberTimeout);
        }
    }
    /**
     * provides access to the trading client
     */
    private Client client;
    /**
     * time to wait for a subscriber to become available before timing out
     */
    private long subscriberTimeout = 500;
    /**
     * provides an entry point into the module system
     */
    private MarketDataManagerModule marketDataManagerModule;
}

package org.marketcetera.marketdata.core.manager.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.CapabilityCollection;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.MarketDataManagerModule;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Routes market data requests to available providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataManagerImpl
        implements MarketDataManager
{
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
     * time to wait for a subscriber to become available before timing out
     */
    private long subscriberTimeout = 500;
    /**
     * provides an entry point into the module system
     */
    private MarketDataManagerModule marketDataManagerModule;
}

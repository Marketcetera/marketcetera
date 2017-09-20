package org.marketcetera.marketdata.service;

/* $License$ */

/**
 * Manages the market data cache service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataCacheManager
{
    /**
     * Add the given market data cache provider to the manager.
     *
     * @param inMarketDataCacheProvider a <code>MarketDataCacheProvider</code> value
     */
    void addMarketDataCacheProvider(MarketDataCacheProvider inMarketDataCacheProvider);
    /**
     * Remove the given market data cache provider from the manager.
     *
     * @param inMarketDataCacheProvider a <code>MarketDataCacheProvider</code> value
     */
    void removeMarketDataCacheProvider(MarketDataCacheProvider inMarketDataCacheProvider);
}

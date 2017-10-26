package org.marketcetera.marketdata.service;

import java.util.Deque;

import org.marketcetera.core.Cacheable;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Manages the market data cache service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataCacheManager
        extends Cacheable
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
    /**
     * Gets the most recent snapshot for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent);
    /**
     * Gets the most recent snapshot page for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                              Content inContent,
                                              PageRequest inPageRequest);
}

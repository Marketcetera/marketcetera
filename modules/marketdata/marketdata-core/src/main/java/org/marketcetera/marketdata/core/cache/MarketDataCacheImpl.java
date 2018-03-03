package org.marketcetera.marketdata.core.cache;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.Event;
import org.marketcetera.event.HasExchange;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides market data cache services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class MarketDataCacheImpl
        implements MarketDataCache
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.cache.MarketDataCache#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Optional<Event> getSnapshot(Instrument inInstrument,
                                       Content inContent,
                                       String inExchange)
    {
        // TODO make this threadsafe
        InstrumentExchange noExchangeKey = new InstrumentExchange(inInstrument);
        InstrumentExchange exchangeKey = null;
        if(inExchange != null) {
            exchangeKey = new InstrumentExchange(inInstrument,
                                                 inExchange);
        }
        MarketDataCacheElement cacheElement = null;
        // the first preference is if there is market data for the specific exchange
        if(exchangeKey != null) {
            cacheElement = cachedMarketdata.getIfPresent(exchangeKey);
        }
        if(cacheElement == null) {
            cacheElement = cachedMarketdata.getIfPresent(noExchangeKey);
        }
        if(cacheElement == null) {
            return Optional.absent();
        }
        return Optional.of(cacheElement.getSnapshot(inContent));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.cache.MarketDataCache#update(org.marketcetera.marketdata.Content, org.marketcetera.event.Event[])
     */
    @Override
    public List<Event> update(Content inContent,
                              Event... inEvents)
    {
        if(inEvents == null || inEvents.length == 0) {
            return Collections.emptyList();
        }
        // this will hold the events that we're going to return
        List<Event> events = Lists.newArrayList();
        for(Event event : inEvents) {
            // we can't assume that all the elements share the same instrument/exchange
            if(event instanceof HasInstrument) {
                Instrument instrument = ((HasInstrument)event).getInstrument();
                // we don't require an exchange like we require an instrument
                String exchange = null;
                if(event instanceof HasExchange) {
                    exchange = ((HasExchange)event).getExchange();
                }
                InstrumentExchange noExchangeKey = new InstrumentExchange(instrument);
                InstrumentExchange exchangeKey = null;
                if(exchange != null) {
                    exchangeKey = new InstrumentExchange(instrument,
                                                         exchange);
                }
                // we have to return the updates this batch of events creates. since we can't guarantee each event has an exchange
                //  and some legit events (like marketstat) don't have an exchange, we'll return the batch of events from the "no exchange" cache
                MarketDataCacheElement cacheElement = cachedMarketdata.getUnchecked(noExchangeKey);
                events.addAll(cacheElement.update(inContent,
                                                  event));
                // we won't return these events, but make sure the "exchange" cache is updated, if appropriate for this event
                if(exchangeKey != null) {
                    cacheElement = cachedMarketdata.getUnchecked(exchangeKey);
                    cacheElement.update(inContent,
                                        event);
                }
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot cache {} because it has no instrument",  // TODO message
                                      event);
            }
        }
        return events;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        cachedMarketdata.invalidateAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.cache.MarketDataCache#invalidate(org.marketcetera.trade.Instrument, java.lang.String)
     */
    @Override
    public void invalidate(Instrument inInstrument,
                           String inExchange)
    {
        InstrumentExchange noExchangeKey = new InstrumentExchange(inInstrument);
        InstrumentExchange exchangeKey = null;
        if(inExchange != null) {
            exchangeKey = new InstrumentExchange(inInstrument,
                                                 inExchange);
        }
        cachedMarketdata.invalidate(noExchangeKey);
        if(exchangeKey != null) {
            cachedMarketdata.invalidate(exchangeKey);
        }
    }
    /**
     * tracks cached market data by the instrument/exchange
     */
    private final LoadingCache<InstrumentExchange,MarketDataCacheElement> cachedMarketdata = CacheBuilder.newBuilder().build(new CacheLoader<InstrumentExchange,MarketDataCacheElement>() {
        @Override
        public MarketDataCacheElement load(InstrumentExchange inKey)
                throws Exception
        {
            return new MarketDataCacheElement(inKey);
        }}
    );
}

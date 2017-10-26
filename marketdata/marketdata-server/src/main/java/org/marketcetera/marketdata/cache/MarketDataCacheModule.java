package org.marketcetera.marketdata.cache;

import org.marketcetera.core.QueueProcessor;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.core.provider.MarketDataCacheElement;
import org.marketcetera.marketdata.service.MarketDataCacheManager;
import org.marketcetera.marketdata.service.MarketDataCacheProvider;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.Instrument;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/* $License$ */

/**
 * Caches and reemits market data in a data flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class MarketDataCacheModule
        extends AbstractDataReemitterModule
        implements MarketDataCacheProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataCache#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content)
     */
    @Override
    public Event getSnapshot(Instrument inInstrument,
                             Content inContent)
    {
        return marketDataCache.getUnchecked(inInstrument).getSnapshot(inContent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        marketDataCache.invalidateAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        if(marketDataCacheManager != null) {
            marketDataCacheManager.addMarketDataCacheProvider(this);
        }
        eventProcessor = new EventProcessor();
        eventProcessor.start();
        super.preStart();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        super.preStop();
        if(marketDataCacheManager != null) {
            marketDataCacheManager.removeMarketDataCacheProvider(this);
        }
        if(eventProcessor != null) {
            try {
                eventProcessor.stop();
            } catch (Exception ignored) {}
            eventProcessor = null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected Object onReceiveData(Object inData,
                                   DataEmitterSupport inDataSupport)
    {
        if(inData instanceof Event) {
            eventProcessor.add((Event)inData);
        }
        return inData;
    }
    /**
     * Create a new MarketDataCacheModule instance.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    MarketDataCacheModule(ModuleURN inInstanceUrn)
    {
        super(inInstanceUrn,
              true);
    }
    /**
     * Processes events for the cache.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class EventProcessor
            extends QueueProcessor<Event>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(Event inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(Event inData)
                throws Exception
        {
            if(inData instanceof HasInstrument) {
                HasInstrument hasInstrument = (HasInstrument)inData;
                MarketDataCacheElement marketDataCacheElement = marketDataCache.getUnchecked(hasInstrument.getInstrument());
                for(Content content : Content.values()) {
                    if(content.isRelevantTo(inData.getClass())) {
                        marketDataCacheElement.update(content,
                                                      inData);
                    }
                }
            }
        }
        /**
         * Create a new EventProcessor instance.
         */
        private EventProcessor()
        {
            super("Market Data Cache Processor");
        }
    }
    /**
     * caches market data
     */
    private final LoadingCache<Instrument,MarketDataCacheElement> marketDataCache = CacheBuilder.newBuilder().build(new CacheLoader<Instrument,MarketDataCacheElement>() {
        @Override
        public MarketDataCacheElement load(Instrument inKey)
                throws Exception
        {
            return new MarketDataCacheElement(inKey);
        }}
    );
    /**
     * processes events
     */
    private EventProcessor eventProcessor;
    /**
     * provides access to market data cache services
     */
    @Autowired(required=false)
    private MarketDataCacheManager marketDataCacheManager;
}

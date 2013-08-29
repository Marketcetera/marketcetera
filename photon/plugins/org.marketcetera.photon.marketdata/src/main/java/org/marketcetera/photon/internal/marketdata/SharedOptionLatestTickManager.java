package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.*;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages latest tick flows for an entire option chain that shares a common
 * underlying equity.
 * <p>
 * Implementation note: the "item" managed by this class is a map that is a
 * computing map. Calling get() atomically creates a value for the key. The
 * {@link MarketData} class manages this map, calling get() when latest tick is
 * request for an instrument, and calling remove() when the it is no longer
 * needed. Other code that interacts with the map should be careful not to call
 * get() unless they are certain the item exists (containsKey() returned true).
 * Otherwise, map entries might not be cleaned up properly.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SharedOptionLatestTickManager
        extends
        DataFlowManager<Map<Option, MDLatestTickImpl>, SharedOptionLatestTickKey>
        implements ISharedOptionLatestTickManager {

    /**
     * Constructor.
     * 
     * @param moduleManager
     *            the module manager
     * @param marketDataExecutor
     *            an executor for long running module operations that
     *            <strong>must</strong> execute tasks sequentially
     * @param marketDataRequestSupport
     *            supports generating market data requests
     * @throws IllegalArgumentException
     *             if moduleManager is null
     */
    @Inject
    public SharedOptionLatestTickManager(final ModuleManager moduleManager,
            @MarketDataExecutor final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        super(moduleManager, EnumSet.of(Capability.LATEST_TICK),
                marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected Map<Option, MDLatestTickImpl> createItem(
            final SharedOptionLatestTickKey key) {
        assert key != null;
        return CacheBuilder.newBuilder().build(new CacheLoader<Option,MDLatestTickImpl>() {
            @Override
            public MDLatestTickImpl load(Option from)
                    throws Exception
            {
                MDLatestTickImpl item = new MDLatestTickImpl();
                item.setInstrument(from);
                return item;
            }}).asMap();
    }

    @Override
    protected void resetItem(final SharedOptionLatestTickKey key,
            final Map<Option, MDLatestTickImpl> item) {
        assert key != null;
        assert item != null;
        synchronized (item) {
            for (MDLatestTickImpl value : item.values()) {
                value.setPrice(null);
                value.setSize(null);
                value.setMultiplier(null);
            }
        }
    }

    @Override
    protected Subscriber createSubscriber(final SharedOptionLatestTickKey key) {
        assert key != null;
        final Instrument instrument = key.getInstrument();
        final MarketDataRequest request = MarketDataRequestBuilder.newRequest()
                .withAssetClass(AssetClass.OPTION).withUnderlyingSymbols(
                        instrument.getSymbol())
                .withContent(Content.LATEST_TICK).create();
        return new Subscriber() {

            @Override
            public MarketDataRequest getRequest() {
                return request;
            }

            @Override
            public void receiveData(final Object inData) {
                final Map<Option, MDLatestTickImpl> item = getItem(key);
                synchronized (item) {
                    if (inData instanceof TradeEvent) {
                        TradeEvent data = (TradeEvent) inData;
                        Instrument instrument = data.getInstrument();
                        /*
                         * Ignore data if it is for an instrument (e.g. another
                         * option) that has not been requested yet.
                         */
                        if (item.containsKey(instrument)) {
                            MDLatestTickImpl mdItem = item.get(instrument);
                            BigDecimal price = data.getPrice();
                            if (price != null) {
                                mdItem.setPrice(price);
                            }
                            BigDecimal size = data.getSize();
                            if (size != null) {
                                mdItem.setSize(size);
                            }
                            if (data instanceof OptionEvent) {
                                OptionEvent optionData = (OptionEvent) data;
                                BigDecimal multiplier = optionData.getMultiplier();
                                if (multiplier != null) {
                                    mdItem.setMultiplier(multiplier);
                                }
                            }
                        }
                    } else {
                        reportUnexpectedData(inData);
                    }
                }
            }
        };
    }
}

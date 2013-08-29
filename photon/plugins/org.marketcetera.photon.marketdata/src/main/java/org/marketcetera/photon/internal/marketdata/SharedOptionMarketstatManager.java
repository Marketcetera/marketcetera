package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.marketdata.*;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages marketstat data for an entire option chain that shares a common
 * underlying equity.
 * <p>
 * Implementation note: the "item" managed by this class is a map that is a
 * computing map. Calling get() atomically creates a value for the key. The
 * {@link MarketData} class manages this map, calling get() when marketstat is
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
public class SharedOptionMarketstatManager
        extends
        DataFlowManager<Map<Option, MDMarketstatImpl>, SharedOptionMarketstatKey>
        implements ISharedOptionMarketstatManager {

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
    public SharedOptionMarketstatManager(final ModuleManager moduleManager,
            @MarketDataExecutor final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        super(moduleManager, EnumSet.of(Capability.MARKET_STAT),
                marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected Map<Option, MDMarketstatImpl> createItem(
            final SharedOptionMarketstatKey key) {
        assert key != null;
        return CacheBuilder.newBuilder().build(new CacheLoader<Option,MDMarketstatImpl>() {

            @Override
            public MDMarketstatImpl load(Option from)
                    throws Exception
            {
                MDMarketstatImpl item = new MDMarketstatImpl();
                item.setInstrument(from);
                return item;
            }
        }).asMap();
    }

    @Override
    protected void resetItem(final SharedOptionMarketstatKey key,
            final Map<Option, MDMarketstatImpl> item) {
        assert key != null;
        assert item != null;
        synchronized (item) {
            for (MDMarketstatImpl value : item.values()) {
                value.setClosePrice(null);
                value.setCloseDate(null);
                value.setPreviousClosePrice(null);
                value.setPreviousCloseDate(null);
            }
        }
    }

    @Override
    protected Subscriber createSubscriber(final SharedOptionMarketstatKey key) {
        assert key != null;
        final Instrument instrument = key.getInstrument();
        final MarketDataRequest request = MarketDataRequestBuilder.newRequest()
                .withAssetClass(AssetClass.OPTION).withUnderlyingSymbols(
                        instrument.getSymbol())
                .withContent(Content.MARKET_STAT).create();
        return new Subscriber() {

            @Override
            public MarketDataRequest getRequest() {
                return request;
            }

            @Override
            public void receiveData(final Object inData) {
                final Map<Option, MDMarketstatImpl> item = getItem(key);
                synchronized (item) {
                    if (inData instanceof MarketstatEvent) {
                        MarketstatEvent data = (MarketstatEvent) inData;
                        Instrument instrument = data.getInstrument();
                        /*
                         * Ignore data if it is for an instrument (e.g. another
                         * option) that has not been requested yet.
                         */
                        if (item.containsKey(instrument)) {
                            MDMarketstatImpl mdItem = item.get(instrument);
                            BigDecimal close = data.getClose();
                            if (close != null) {
                                mdItem.setClosePrice(close);
                            }
                            String closeDate = data.getCloseDate();
                            if (closeDate != null) {
                                mdItem.setCloseDate(closeDate);
                            }
                            BigDecimal previousClose = data.getPreviousClose();
                            if (previousClose != null) {
                                mdItem.setPreviousClosePrice(previousClose);
                            }
                            String previousCloseDate = data
                                    .getPreviousCloseDate();
                            if (previousCloseDate != null) {
                                mdItem.setPreviousCloseDate(previousCloseDate);
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

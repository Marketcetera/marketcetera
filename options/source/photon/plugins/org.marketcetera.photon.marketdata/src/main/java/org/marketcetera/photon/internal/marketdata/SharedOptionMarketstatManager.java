package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.AssetClass;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages marketstat data for an entire option chain that shares a common
 * underlying equity.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
        return new MapMaker()
                .makeComputingMap(new Function<Option, MDMarketstatImpl>() {
                    @Override
                    public MDMarketstatImpl apply(final Option from) {
                        MDMarketstatImpl item = new MDMarketstatImpl();
                        item.setInstrument(from);
                        return item;
                    }
                });
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
        final MarketDataRequest request = MarketDataRequest.newRequest()
                .ofAssetClass(AssetClass.OPTION).withUnderlyingSymbols(
                        instrument.getSymbol())
                .withContent(Content.LATEST_TICK);
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
                        System.out.println(inData);
                        MarketstatEvent data = (MarketstatEvent) inData;
                        Instrument instrument = data.getInstrument();
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

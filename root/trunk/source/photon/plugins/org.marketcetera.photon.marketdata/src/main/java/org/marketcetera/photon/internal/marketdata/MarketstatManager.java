package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages market statistic flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketstatManager extends
        DataFlowManager<MDMarketstatImpl, MarketstatKey> implements
        IMarketstatManager {

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
    public MarketstatManager(final ModuleManager moduleManager,
            @MarketDataExecutor final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        super(moduleManager, EnumSet.of(Capability.MARKET_STAT),
                marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected MDMarketstatImpl createItem(final MarketstatKey key) {
        assert key != null;
        MDMarketstatImpl item = new MDMarketstatImpl();
        item.setInstrument(key.getInstrument());
        return item;
    }

    @Override
    protected void resetItem(final MarketstatKey key,
            final MDMarketstatImpl item) {
        assert key != null;
        assert item != null;
        synchronized (item) {
            item.setCloseDate(null);
            item.setClosePrice(null);
            item.setPreviousCloseDate(null);
            item.setPreviousClosePrice(null);
        }
    }

    @Override
    protected Subscriber createSubscriber(final MarketstatKey key) {
        assert key != null;
        final Instrument instrument = key.getInstrument();
        final MarketDataRequest request = initializeRequest(instrument)
                .withContent(Content.MARKET_STAT);
        return new Subscriber() {

            @Override
            public MarketDataRequest getRequest() {
                return request;
            }

            @Override
            public void receiveData(final Object inData) {
                MDMarketstatImpl item = getItem(key);
                synchronized (item) {
                    if (inData instanceof MarketstatEvent) {
                        MarketstatEvent data = (MarketstatEvent) inData;
                        if (!validateInstrument(instrument, data
                                .getInstrument())) {
                            return;
                        }
                        BigDecimal close = data.getClose();
                        if (close != null) {
                            item.setClosePrice(close);
                        }
                        String closeDate = data.getCloseDate();
                        if (closeDate != null) {
                            item.setCloseDate(closeDate);
                        }
                        BigDecimal previousClose = data.getPreviousClose();
                        if (previousClose != null) {
                            item.setPreviousClosePrice(previousClose);
                        }
                        String previousCloseDate = data.getPreviousCloseDate();
                        if (previousCloseDate != null) {
                            item.setPreviousCloseDate(previousCloseDate);
                        }
                    } else {
                        reportUnexpectedData(inData);
                    }
                }
            }
        };
    }
}

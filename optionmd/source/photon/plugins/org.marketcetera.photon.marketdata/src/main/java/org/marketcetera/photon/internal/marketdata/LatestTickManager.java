package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages latest tick flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class LatestTickManager extends
        DataFlowManager<MDLatestTickImpl, LatestTickKey> implements
        ILatestTickManager {

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
    public LatestTickManager(final ModuleManager moduleManager,
            @MarketDataExecutor final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        super(moduleManager, EnumSet.of(Capability.LATEST_TICK),
                marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected MDLatestTickImpl createItem(final LatestTickKey key) {
        assert key != null;
        MDLatestTickImpl item = new MDLatestTickImpl();
        item.setInstrument(key.getInstrument());
        return item;
    }

    @Override
    protected void resetItem(final LatestTickKey key,
            final MDLatestTickImpl item) {
        assert key != null;
        assert item != null;
        synchronized (item) {
            item.setPrice(null);
            item.setSize(null);
            item.setMultiplier(null);
        }
    }

    @Override
    protected Subscriber createSubscriber(final LatestTickKey key) {
        assert key != null;
        final Instrument instrument = key.getInstrument();
        final MarketDataRequest request = initializeRequest(instrument)
                .withContent(Content.LATEST_TICK);
        return new Subscriber() {

            @Override
            public MarketDataRequest getRequest() {
                return request;
            }

            @Override
            public void receiveData(final Object inData) {
                final MDLatestTickImpl item = getItem(key);
                synchronized (item) {
                    if (inData instanceof TradeEvent) {
                        TradeEvent data = (TradeEvent) inData;
                        if (!validateInstrument(instrument, data)) {
                            return;
                        }
                        BigDecimal price = data.getPrice();
                        if (price != null) {
                            item.setPrice(price);
                        }
                        BigDecimal size = data.getSize();
                        if (size != null) {
                            item.setSize(size);
                        }
                        if (data instanceof OptionEvent) {
                            OptionEvent optionData = (OptionEvent) data;
                            BigDecimal multiplier = optionData.getMultiplier();
                            if (multiplier != null) {
                                item.setMultiplier(multiplier);
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

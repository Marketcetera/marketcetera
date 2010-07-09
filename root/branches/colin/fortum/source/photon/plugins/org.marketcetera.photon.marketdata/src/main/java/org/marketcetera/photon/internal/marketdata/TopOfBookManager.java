package org.marketcetera.photon.internal.marketdata;

import java.util.EnumSet;
import java.util.concurrent.Executor;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Manages Top of Book flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class TopOfBookManager extends
        DataFlowManager<MDTopOfBookImpl, TopOfBookKey> implements
        ITopOfBookManager {

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
    public TopOfBookManager(final ModuleManager moduleManager,
            @MarketDataExecutor final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        super(moduleManager, EnumSet.of(Capability.TOP_OF_BOOK),
                marketDataExecutor, marketDataRequestSupport);
    }

    @Override
    protected MDTopOfBookImpl createItem(final TopOfBookKey key) {
        assert key != null;
        MDTopOfBookImpl item = new MDTopOfBookImpl();
        item.setInstrument(key.getInstrument());
        return item;
    }

    @Override
    protected void resetItem(final TopOfBookKey key, final MDTopOfBookImpl item) {
        assert key != null;
        assert item != null;
        synchronized (item) {
            item.setAskPrice(null);
            item.setAskSize(null);
            item.setBidPrice(null);
            item.setBidSize(null);
        }
    }

    @Override
    protected Subscriber createSubscriber(final TopOfBookKey key) {
        assert key != null;
        final Instrument instrument = key.getInstrument();
        final MarketDataRequest request = initializeRequest(instrument)
                .withContent(Content.TOP_OF_BOOK).create();
        return new Subscriber() {

            @Override
            public MarketDataRequest getRequest() {
                return request;
            }

            @Override
            public void receiveData(final Object inData) {
                final MDTopOfBookImpl item = getItem(key);
                synchronized (item) {
                    if (inData instanceof MarketDataEvent
                            && !validateInstrument(instrument,
                                    (MarketDataEvent) inData)) {
                        return;
                    }
                    if (inData instanceof BidEvent) {
                        BidEvent event = (BidEvent) inData;
                        item.setBidPrice(event.getPrice());
                        item.setBidSize(event.getSize());
                    } else if (inData instanceof AskEvent) {
                        AskEvent event = (AskEvent) inData;
                        item.setAskPrice(event.getPrice());
                        item.setAskSize(event.getSize());
                    } else {
                        reportUnexpectedData(inData);
                    }
                }
            }
        };
    }
}

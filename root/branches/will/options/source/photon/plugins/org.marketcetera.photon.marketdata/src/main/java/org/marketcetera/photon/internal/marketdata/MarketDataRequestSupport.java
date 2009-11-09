package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.AssetClass;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link IMarketDataRequestSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketDataRequestSupport implements IMarketDataRequestSupport {

    /**
     * Constructor.
     * 
     * @param useFineGrainedMarketDataForOptions
     *            controls whether
     */
    public MarketDataRequestSupport(boolean useFineGrainedMarketDataForOptions) {
        mUseFineGrainedMarketDataForOptions = useFineGrainedMarketDataForOptions;
    }

    @Override
    public MarketDataRequest initializeRequest(Instrument instrument) {
        if (!mUseFineGrainedMarketDataForOptions
                && (instrument instanceof Option)) {
            return MarketDataRequest.newRequest().ofAssetClass(
                    AssetClass.OPTION).withUnderlyingSymbols(
                    instrument.getSymbol());
        }
        return getAdapter(instrument).initializeRequest(instrument);
    }

    @Override
    public boolean useFineGrainedMarketDataForOptions() {
        return mUseFineGrainedMarketDataForOptions;
    }

    @SuppressWarnings("unchecked")
    private <I extends Instrument> InstrumentRequestAdapter<? super Instrument> getAdapter(
            I instrument) {
        return InstrumentRequestAdapter.SELECTOR.forInstrument(instrument);
    }

    private final boolean mUseFineGrainedMarketDataForOptions;
}

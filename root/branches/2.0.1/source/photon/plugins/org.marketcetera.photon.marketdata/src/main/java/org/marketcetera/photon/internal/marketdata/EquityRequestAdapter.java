package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.AssetClass;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles equities. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityRequestAdapter extends InstrumentRequestAdapter<Equity> {

    /**
     * Constructor.
     */
    public EquityRequestAdapter() {
        super(Equity.class);
    }

    @Override
    public MarketDataRequest initializeRequest(Equity instrument) {
        return MarketDataRequest.newRequest().ofAssetClass(AssetClass.EQUITY)
                .withSymbols(instrument.getSymbol());
    }
}

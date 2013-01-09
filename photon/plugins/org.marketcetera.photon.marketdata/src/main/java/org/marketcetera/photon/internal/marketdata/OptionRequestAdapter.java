package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles options (OSI style).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class OptionRequestAdapter extends InstrumentRequestAdapter<Option> {

    /**
     * Constructor.
     */
    public OptionRequestAdapter() {
        super(Option.class);
    }

    @Override
    public MarketDataRequestBuilder initializeRequest(Option instrument) {
        return MarketDataRequestBuilder
                .newRequest()
                .withAssetClass(AssetClass.OPTION)
                .withSymbols(
                        OptionUtils.getOsiSymbolFromOption(instrument));
    }
}

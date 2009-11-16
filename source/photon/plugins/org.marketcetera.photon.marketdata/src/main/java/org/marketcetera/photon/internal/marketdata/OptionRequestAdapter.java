package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.AssetClass;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles options (OSI style).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
    public MarketDataRequest initializeRequest(Option instrument) {
        return MarketDataRequest
                .newRequest()
                .ofAssetClass(AssetClass.OPTION)
                .withSymbols(
                        OptionUtils.getOsiSymbolFromOption(instrument));
    }
}

package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Future;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Produces market data requests for {@link Future} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureRequestAdapter
        extends InstrumentRequestAdapter<Future>
{
    /**
     * Create a new FutureRequestAdapter instance.
     */
    public FutureRequestAdapter()
    {
        super(Future.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.internal.marketdata.InstrumentRequestAdapter#initializeRequest(org.marketcetera.trade.Instrument)
     */
    @Override
    public MarketDataRequestBuilder initializeRequest(Future inInstrument)
    {
        return MarketDataRequestBuilder.newRequest().withAssetClass(AssetClass.FUTURE)
                                                    .withSymbols(inInstrument.getSymbol());
    }
}

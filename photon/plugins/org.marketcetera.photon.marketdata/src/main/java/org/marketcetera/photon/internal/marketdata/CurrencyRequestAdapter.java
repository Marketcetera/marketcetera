package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Produces market data requests for {@link Currency} instruments.
 *
 */
@ClassVersion("$Id$")
public class CurrencyRequestAdapter
        extends InstrumentRequestAdapter<Currency>
{
    /**
     * Create a new CurrencyRequestAdapter instance.
     */
    public CurrencyRequestAdapter()
    {
        super(Currency.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.internal.marketdata.InstrumentRequestAdapter#initializeRequest(org.marketcetera.trade.Instrument)
     */
    @Override
    public MarketDataRequestBuilder initializeRequest(Currency inInstrument)
    {
        return MarketDataRequestBuilder.newRequest().withAssetClass(AssetClass.CURRENCY)
                                                    .withSymbols(inInstrument.getSymbol());
    }
}

package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Initializes market data request for an instrument.
 * 
 * <p>A subclass of this class should be created for every instrument type handled by the system.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentRequestAdapter<InstrumentClazz extends Instrument>
        extends InstrumentFunctionHandler<InstrumentClazz>
{
    /**
     * Initializes a {@link MarketDataRequestBuilder} for the provided instrument.
     * 
     * @param inInstrument an <code>I</code> value
     * @return a {@link MarketDataRequestBuilder} with instrument information
     */
    abstract public MarketDataRequestBuilder initializeRequest(InstrumentClazz inInstrument);
    /**
     * Creates an instance that handles the specified instrument subclass.
     * 
     * @param instrument the instrument subclass handled by this instance.
     */
    protected InstrumentRequestAdapter(Class<InstrumentClazz> instrument)
    {
        super(instrument);
    }
    public static final StaticInstrumentFunctionSelector<InstrumentRequestAdapter> SELECTOR = new StaticInstrumentFunctionSelector<InstrumentRequestAdapter>(InstrumentRequestAdapter.class);
}

package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Initializes market data request for an instrument.
 * <p>
 * A subclass of this class should be created for every instrument type handled
 * by the system.
 * 
 * @param <I>
 *            The type of instrument handled by this function
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class InstrumentRequestAdapter<I extends Instrument> extends
        InstrumentFunctionHandler<I> {

    /**
     * Creates an instance that handles the specified instrument subclass.
     * 
     * @param instrument
     *            the instrument subclass handled by this instance.
     */
    protected InstrumentRequestAdapter(Class<I> instrument) {
        super(instrument);
    }

    /**
     * Initializes a {@link MarketDataRequest} for the provided instrument.
     * 
     * @param instrument
     *            the instrument
     * @return a {@link MarketDataRequest} with instrument information
     */
    abstract public MarketDataRequest initializeRequest(I instrument);

    @SuppressWarnings("unchecked")
    public static final StaticInstrumentFunctionSelector<InstrumentRequestAdapter> SELECTOR = new StaticInstrumentFunctionSelector<InstrumentRequestAdapter>(
            InstrumentRequestAdapter.class);
}

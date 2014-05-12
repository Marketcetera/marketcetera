package org.marketcetera.client.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.trade.Instrument;
import org.marketcetera.client.OrderValidationException;

/* $License$ */
/**
 * A class that abstracts out instrument validation functions.
 *
 * @param <I> The type of instrument handled by this function
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentValidationHandler<I extends Instrument>
        extends InstrumentFunctionHandler<I> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     *
     * @param inInstrument the instrument subclass handled by this instance.
     */
    protected InstrumentValidationHandler(Class<I> inInstrument) {
        super(inInstrument);
    }

    /**
     * Validates the supplied instrument.
     *
     * @param instrument the instrument
     *
     * @throws OrderValidationException if the validation fails.
     */
    public abstract void validate(Instrument instrument) throws OrderValidationException;
    /**
     * The factory that provides the handler instance for the specified
     * instrument.
     */
    @SuppressWarnings("rawtypes")
    public static final StaticInstrumentFunctionSelector<InstrumentValidationHandler> SELECTOR =
            new StaticInstrumentFunctionSelector<InstrumentValidationHandler>(InstrumentValidationHandler.class);
}

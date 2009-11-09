package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;

import java.math.BigDecimal;

/* $License$ */
/**
 * A class that helps extract the instrument specific fields for persistence
 * within {@link ExecutionReportSummary}.
 *
 * @param <I> The type of instrument handled by this function
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class InstrumentSummaryFields<I extends Instrument>
        extends InstrumentFunctionHandler<I> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     *
     * @param inInstrument the instrument subclass handled by this instance.
     */
    protected InstrumentSummaryFields(Class<I> inInstrument) {
        super(inInstrument);
    }

    /**
     * Returns the expiry value of the instrument.
     *
     * @param inInstrument the instrument.
     *
     * @return the expiry or null if no expiry is available for the instrument.
     */
    public abstract String getExpiry(Instrument inInstrument);

    /**
     * Returns the strike price of the instrument.
     *
     * @param inInstrument the instrument.
     *
     * @return the strike price or null if no strike price is available for the
     * instrument.
     */
    public abstract BigDecimal getStrikePrice(Instrument inInstrument);

    /**
     * Returns the option type of the instrument.
     *
     * @param inInstrument the instrument.
     *
     * @return the option type or null if no option type is available for the
     * instrument.
     */
    public abstract OptionType getOptionType(Instrument inInstrument);

    /**
     * The selector that provides the appropriate instance of this class for
     * an instrument.
     */
    public static final StaticInstrumentFunctionSelector<InstrumentSummaryFields> SELECTOR =
            new StaticInstrumentFunctionSelector<InstrumentSummaryFields>(InstrumentSummaryFields.class);
}

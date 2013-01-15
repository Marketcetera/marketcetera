package org.marketcetera.photon.core;

import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.internal.core.FuturePrettyPrinter;
import org.marketcetera.photon.internal.core.OptionPrettyPrinter;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Pretty prints {@link Instrument} objects for the UI.
 * <p>
 * A subclass of this class should be created for every instrument type handled
 * by the system.
 * 
 * @param <I>
 *            The type of instrument handled by this function
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentPrettyPrinter<I extends Instrument> extends
        InstrumentFunctionHandler<I> {

    /**
     * Creates an instance that handles the specified instrument subclass.
     * 
     * @param instrument
     *            the instrument subclass handled by this instance.
     */
    protected InstrumentPrettyPrinter(Class<I> instrument) {
        super(instrument);
    }

    /**
     * Pretty prints the instrument as a string.
     * 
     * @param instrument
     *            the instrument
     * @return the string value
     */
    protected abstract String doPrint(I instrument);

    /**
     * Pretty prints the instrument as a string. If no handler is defined for
     * the instrument, this just returns {@code instrument.toString()}.
     * 
     * @param instrument
     *            the instrument
     * @return the string value
     * @throws IllegalArgumentException
     *             if instrument is null
     */
    @SuppressWarnings("unchecked")
    public static String print(Instrument instrument) {
        Validate.notNull(instrument, "instrument"); //$NON-NLS-1$
        try {
            return SELECTOR.forInstrument(instrument).doPrint(instrument);
        } catch (Exception e) {
            return instrument.toString();
        }
    }

    /**
     * Pretty prints an option expiry. If the expiry cannot be parsed, it is
     * returned.
     * 
     * @param option
     *            the option
     * @return the string value
     * @throws IllegalArgumentException
     *             if option is null
     */
    public static String printOptionExpiry(Option option) {
        return OptionPrettyPrinter.printExpiry(option);
    }
    /**
     * Prints a future expiry.
     *
     * @param inFuture a <code>Future</code> value
     * @return a <code>String</code> value
     * @throws IllegalArgumentException if the parameter is <code>null</code>
     */
    public static String printFutureExpiry(Future inFuture)
    {
        return FuturePrettyPrinter.printExpiry(inFuture);
    }

    @SuppressWarnings("unchecked")
    private static final StaticInstrumentFunctionSelector<InstrumentPrettyPrinter> SELECTOR = new StaticInstrumentFunctionSelector<InstrumentPrettyPrinter>(
            InstrumentPrettyPrinter.class);
}

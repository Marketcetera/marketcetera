package org.marketcetera.core.position.impl;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.trade.Instrument;

import java.util.Comparator;

/* $License$ */
/**
 * A comparator used to compare instruments of various types.
 * <p>
 * A subclass of this class should be created for every instrument
 * type handled by the system.
 * Instrument of different types are ordered by the {@link #getRank() rank}.
 * Each subclass should return a unique rank value.
 *
 *
 * @param <I> The type of instrument handled by this function
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentPositionKeyComparator<I extends Instrument>
        extends InstrumentFunctionHandler<I>
        implements Comparator<I> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     *
     * @param inInstrument the instrument subclass handled by this instance.
     */
    protected InstrumentPositionKeyComparator(Class<I> inInstrument) {
        super(inInstrument);
    }

    /**
     * Returns the rank of this instrument so that the instrument's position
     * can be compared with instruments of other types.
     *
     * @return the rank.
     */
    public abstract int getRank();
    
    /**
     * The factory that provides the handler instance for the specified
     * instrument.
     */
    public static final StaticInstrumentFunctionSelector<InstrumentPositionKeyComparator> SELECTOR =
            new StaticInstrumentFunctionSelector<InstrumentPositionKeyComparator>(InstrumentPositionKeyComparator.class);
}

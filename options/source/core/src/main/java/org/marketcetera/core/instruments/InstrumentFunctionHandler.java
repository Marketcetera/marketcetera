package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;

/* $License$ */
/**
 * Instrument specific function handler class.
 * <p>
 * An abstract subclass of this class is created for each instrument specific
 * functionality. The subclass adds an abstract method that represents the
 * instrument specific function performed by the class.
 * <p>
 * That abstract subclass is then further subclasssed for each instrument type
 * that the system supports.
 *
 * @param <I> The type of instrument handled by this function.
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class InstrumentFunctionHandler<I extends Instrument> {
    /**
     * Creates an instance that handles the specified instrument subclass.
     *
     * @param inInstrument the instrument subclass handled by this instance.
     */
    protected InstrumentFunctionHandler(Class<I> inInstrument) {
        mInstrument = inInstrument;
    }

    /**
     * Returns the instrument type handled by this subclass.
     *
     * @return the instrument type handled by this subclass.
     */
    final Class<I> getInstrumentType() {
        return mInstrument;
    }

    private final Class<I> mInstrument;
}

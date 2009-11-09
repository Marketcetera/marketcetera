package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.core.instruments.InstrumentFunctionHandler;
import org.marketcetera.core.instruments.StaticInstrumentFunctionSelector;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates serialization of an instrument to an Eclipse {@link IMemento}.
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
public abstract class InstrumentToMemento<I extends Instrument> extends
        InstrumentFunctionHandler<I> {

    /**
     * Creates an instance that handles the specified instrument subclass.
     * 
     * @param instrument
     *            the instrument subclass handled by this instance.
     */
    protected InstrumentToMemento(Class<I> instrument) {
        super(instrument);
    }

    /**
     * Saves an instrument to the memento.
     * 
     * @param instrument
     *            the instrument
     * @param memento
     *            the memento
     */
    protected abstract void doSave(I instrument, IMemento memento);

    /**
     * Saves the instrument to the memento.
     * 
     * @param instrument
     *            the instrument
     * @param memento
     *            the memento
     */
    @SuppressWarnings("unchecked")
    public static void save(Instrument instrument, IMemento memento) {
        Validate.notNull(instrument, "instrument", //$NON-NLS-1$
                memento, "memento"); //$NON-NLS-1$
        SELECTOR.forInstrument(instrument).doSave(instrument, memento);
    }

    @SuppressWarnings("unchecked")
    private static final StaticInstrumentFunctionSelector<InstrumentToMemento> SELECTOR = new StaticInstrumentFunctionSelector<InstrumentToMemento>(
            InstrumentToMemento.class);
}

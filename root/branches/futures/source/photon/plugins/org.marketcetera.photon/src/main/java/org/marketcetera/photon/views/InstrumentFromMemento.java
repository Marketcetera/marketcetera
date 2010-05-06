package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.core.instruments.DynamicInstrumentFunctionSelector;
import org.marketcetera.core.instruments.DynamicInstrumentHandler;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates deserialization of an instrument from an Eclipse {@link IMemento}
 * .
 * <p>
 * A subclass of this class should be created for every instrument type handled
 * by the system.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentFromMemento extends
        DynamicInstrumentHandler<IMemento> {

    /**
     * Restores an instrument saved via
     * {@link InstrumentToMemento#doSave(Instrument, IMemento)}.
     * 
     * @param memento
     *            the memento
     * @return the instrument
     */
    protected abstract Instrument doRestore(IMemento memento);

    /**
     * Restores an instrument saved via
     * {@link InstrumentToMemento#save(Instrument, IMemento)}.
     * 
     * @param memento
     *            the memento
     * @return the instrument, or null if none exists
     */
    public static Instrument restore(IMemento memento) {
        Validate.notNull(memento, "memento"); //$NON-NLS-1$
        return SELECTOR.forValue(memento).doRestore(memento);
    }

    private static final DynamicInstrumentFunctionSelector<IMemento, InstrumentFromMemento> SELECTOR = new DynamicInstrumentFunctionSelector<IMemento, InstrumentFromMemento>(
            InstrumentFromMemento.class);
}

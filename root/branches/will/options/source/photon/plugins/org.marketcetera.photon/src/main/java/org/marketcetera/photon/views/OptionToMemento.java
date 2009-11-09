package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates serialization of an {@link Option} to an Eclipse {@link IMemento}
 * .
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionToMemento extends InstrumentToMemento<Option> {

    static final String OPTION_TAG = "option"; //$NON-NLS-1$
    static final String SYMBOL_ATTRIBUTE = "symbol"; //$NON-NLS-1$
    static final String EXPIRY_ATTRIBUTE = "expiry"; //$NON-NLS-1$
    static final String STRIKE_ATTRIBUTE = "strike"; //$NON-NLS-1$
    static final String TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public OptionToMemento() {
        super(Option.class);
    }

    @Override
    protected void doSave(Option instrument, IMemento memento) {
        IMemento option = memento.createChild(OPTION_TAG);
        option.putString(SYMBOL_ATTRIBUTE, instrument.getSymbol());
        option.putString(EXPIRY_ATTRIBUTE, instrument.getExpiry());
        option.putString(STRIKE_ATTRIBUTE, instrument.getStrikePrice()
                .toPlainString());
        option.putString(TYPE_ATTRIBUTE, instrument.getType().name());
    }
}

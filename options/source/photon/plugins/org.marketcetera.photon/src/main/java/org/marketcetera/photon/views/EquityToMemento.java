package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates serialization/deserialization of an equity to and Eclipse
 * {@link IMemento}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class EquityToMemento extends
        InstrumentToMemento<Equity> {

    static final String EQUITY_TAG = "equity"; //$NON-NLS-1$
    static final String SYMBOL_ATTRIBUTE = "symbol"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public EquityToMemento() {
        super(Equity.class);
    }

    @Override
    protected void doSave(Equity instrument, IMemento memento) {
        IMemento equity = memento.createChild(EQUITY_TAG);
        equity.putString(SYMBOL_ATTRIBUTE, instrument.getSymbol());
    }
}

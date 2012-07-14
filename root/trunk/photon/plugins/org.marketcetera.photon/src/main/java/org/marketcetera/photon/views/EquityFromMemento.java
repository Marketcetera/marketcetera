package org.marketcetera.photon.views;

import static org.marketcetera.photon.views.EquityToMemento.EQUITY_TAG;
import static org.marketcetera.photon.views.EquityToMemento.SYMBOL_ATTRIBUTE;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates deserialization of an equity from an Eclipse {@link IMemento}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityFromMemento extends InstrumentFromMemento {

    @Override
    protected boolean isHandled(IMemento inValue) {
        return inValue.getChildren(EQUITY_TAG).length == 1;
    }

    @Override
    protected Equity doRestore(IMemento memento) {
        IMemento equity = memento.getChild(EQUITY_TAG);
        return new Equity(equity.getString(SYMBOL_ATTRIBUTE));
    }
}

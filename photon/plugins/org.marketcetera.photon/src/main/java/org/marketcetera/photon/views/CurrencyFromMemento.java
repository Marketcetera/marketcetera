package org.marketcetera.photon.views;


import org.eclipse.ui.IMemento;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.Currency;
import static org.marketcetera.photon.views.CurrencyToMemento.*;

/* $License$ */

/**
 * Facilitates deserialization of an {@link Currency} from an Eclipse
 * {@link IMemento} .
 * 
 */
@ClassVersion("$Id$")
public class CurrencyFromMemento extends InstrumentFromMemento {
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(IMemento inValue) {
        return inValue.getChildren(CURRENCY_TAG).length == 1;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.InstrumentFromMemento#doRestore(org.eclipse.ui.IMemento)
     */
    @Override
    protected Currency doRestore(IMemento memento) {
        IMemento currency = memento.getChild(CURRENCY_TAG);
        return new Currency(currency.getString(LEFT_CCY_ATTRIBUTE), currency
                .getString(RIGHT_CCY_ATTRIBUTE), currency
                .getString(NEAR_TENOR_ATTRIBUTE), currency
                .getString(FAR_TENOR_ATTRIBUTE));
    }
}

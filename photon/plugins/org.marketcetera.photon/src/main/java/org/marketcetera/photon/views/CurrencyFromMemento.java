package org.marketcetera.photon.views;


import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Currency;
import static org.marketcetera.photon.views.CurrencyToMemento.*;

/* $License$ */

/**
 * Facilitates deserialization of an {@link Currency} from an Eclipse
 * {@link IMemento} .
 * 
 */
public class CurrencyFromMemento extends InstrumentFromMemento {

    @Override
    protected boolean isHandled(IMemento inValue) {
        return inValue.getChildren(CURRENCY_TAG).length == 1;
    }

    @Override
    protected Currency doRestore(IMemento memento) {
        IMemento currency = memento.getChild(CURRENCY_TAG);
        return new Currency(currency.getString(LEFT_CCY_ATTRIBUTE), currency
                .getString(RIGHT_CCY_ATTRIBUTE), currency
                .getString(NEAR_TENOR_ATTRIBUTE), currency
                .getString(FAR_TENOR_ATTRIBUTE));
    }
}

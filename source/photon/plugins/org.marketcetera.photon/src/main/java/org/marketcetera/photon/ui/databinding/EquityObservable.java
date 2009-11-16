package org.marketcetera.photon.ui.databinding;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages and {@link Equity} binding made up of a symbol string.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class EquityObservable extends
        CompoundObservableManager<ITypedObservableValue<Instrument>> {

    private final ITypedObservableValue<String> mSymbol;

    public EquityObservable(ITypedObservableValue<Instrument> instrument) {
        super(instrument);
        mSymbol = TypedObservableValueDecorator.create(String.class);
        init(Collections.singleton(mSymbol));
    }

    @Override
    protected void updateChildren() {
        Instrument instrument = getParent().getTypedValue();
        if (instrument instanceof Equity) {
            Equity equity = (Equity) instrument;
            setIfChanged(mSymbol, equity.getSymbol());
        } else {
            setIfChanged(mSymbol, null);
        }
    }

    @Override
    protected void updateParent() {
        String symbol = mSymbol.getTypedValue();
        Equity newValue = null;
        if (StringUtils.isNotBlank(symbol)) {
            newValue = new Equity(symbol);
        }
        ITypedObservableValue<Instrument> instrument = getParent();
        setIfChanged(instrument, newValue);
    }

    /**
     * Returns an observable tied to the equity's symbol.
     * 
     * @return an observable for the equity's symbol
     */
    public ITypedObservableValue<String> observeSymbol() {
        return mSymbol;
    }
}

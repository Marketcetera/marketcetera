package org.marketcetera.photon.ui.databinding;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Observes an instrument as a future and provides child observables for the
 * future components.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureObservable
        extends CompoundObservableManager<ITypedObservableValue<Instrument>>
{
    /**
     * Create a new FutureObservable instance.
     *
     * @param inParent an <code>ITypedObservableValue&lt;Instrument&gt</code> value
     */
    public FutureObservable(ITypedObservableValue<Instrument> inParent)
    {
        super(inParent);
        mSymbol = TypedObservableValueDecorator.create(String.class);
        mCustomerInfo = TypedObservableValueDecorator.create(String.class);
        init(ImmutableList.of(mSymbol,
                              mCustomerInfo));
    }
    /**
     * Observes the future symbol.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> observeSymbol()
    {
        return mSymbol;
    }
    /**
     * Observes the future customer info.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> observeCustomerInfo()
    {
        return mCustomerInfo;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateParent()
     */
    @Override
    protected void updateParent()
    {
        String symbol = mSymbol.getTypedValue();
        Future newValue = null;
        if(StringUtils.isNotBlank(symbol)) {
            try {
                newValue = new Future(symbol);
            } catch (Exception ignored) {}
        }
        ITypedObservableValue<Instrument> instrument = getParent();
        setIfChanged(instrument,
                     newValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateChildren()
     */
    @Override
    protected void updateChildren()
    {
        Instrument instrument = getParent().getTypedValue();
        if (instrument instanceof Future) {
            Future future = (Future)instrument;
            setIfChanged(mSymbol,
                         future.getSymbol());
        } else {
            setIfChanged(mSymbol,
                         null);
        }
    }
    /**
     * observes the future symbol
     */
    private final ITypedObservableValue<String> mSymbol;
    /**
     * observes the future customer info
     */
    private final ITypedObservableValue<String> mCustomerInfo;
}

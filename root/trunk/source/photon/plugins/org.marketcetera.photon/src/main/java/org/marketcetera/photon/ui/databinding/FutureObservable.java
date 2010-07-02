package org.marketcetera.photon.ui.databinding;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
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
        mExpirationMonth = TypedObservableValueDecorator.create(FutureExpirationMonth.class);
        mExpirationYear = TypedObservableValueDecorator.create(String.class);
        init(ImmutableList.of(mSymbol,
                              mExpirationMonth,
                              mExpirationYear));
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
     * Observes the future expiration month.
     * 
     * @return an <code>ITypedObservableValue&lt;FutureExpirationMonth&gt;</code> value
     */
    public ITypedObservableValue<FutureExpirationMonth> observeExpirationMonth()
    {
        return mExpirationMonth;
    }
    /**
     * Observes the future expiration year.
     * 
     * @return an <code>ITypedObservableValue&lt;Integer&gt;</code> value
     */
    public ITypedObservableValue<String> observeExpirationYear()
    {
        return mExpirationYear;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateParent()
     */
    @Override
    protected void updateParent()
    {
        String symbol = mSymbol.getTypedValue();
        FutureExpirationMonth expirationMonth = mExpirationMonth.getTypedValue();
        String expirationYear = mExpirationYear.getTypedValue();
        Future newValue = null;
        if(StringUtils.isNotBlank(symbol) &&
            mExpirationMonth != null && 
            mExpirationYear != null) {
            try {
                if(Integer.parseInt(((String)mExpirationYear.getValue())) > 0) {
                    newValue = new Future(symbol,
                                          expirationMonth,
                                          Integer.parseInt(expirationYear));
                }
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
            setIfChanged(mExpirationMonth,
                         future.getExpirationMonth());
            setIfChanged(mExpirationYear,
                         Integer.toString(future.getExpirationYear()));
        } else {
            setIfChanged(mSymbol,
                         null);
            setIfChanged(mExpirationMonth,
                         null);
            setIfChanged(mExpirationYear,
                         null);
        }
    }
    /**
     * observes the future symbol
     */
    private final ITypedObservableValue<String> mSymbol;
    /**
     * observes the future expiration month
     */
    private final ITypedObservableValue<FutureExpirationMonth> mExpirationMonth;
    /**
     * observes the future expiration year
     */
    private final ITypedObservableValue<String> mExpirationYear;
}

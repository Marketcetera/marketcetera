package org.marketcetera.photon.ui.databinding;

import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.ExpirableInstrument;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ExpirableInstrumentObservableManager<I extends Instrument>
        extends CompoundObservableManager<ITypedObservableValue<I>>
{
    /**
     * Create a new ExpirableInstrumentObservable instance.
     *
     * @param inParent
     */
    public ExpirableInstrumentObservableManager(ITypedObservableValue<I> inParent)
    {
        super(inParent);
        mSymbol = TypedObservableValueDecorator.create(String.class);
        mExpiry = TypedObservableValueDecorator.create(String.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateChildren()
     */
    @Override
    protected void updateChildren()
    {
        Instrument instrument = getParent().getTypedValue();
        if(instrument instanceof ExpirableInstrument) {
            ExpirableInstrument eInstrument = (ExpirableInstrument)instrument;
            setIfChanged(getSymbol(),
                         eInstrument.getSymbol());
            setIfChanged(getExpiry(),
                         eInstrument.getExpiry());
        } else {
            setIfChanged(getSymbol(),
                         null);
            setIfChanged(getExpiry(),
                         null);
        }
    }
    /**
     * Observes the expiry.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value containing the expiry observable
     */
    public ITypedObservableValue<String> observeExpiry()
    {
        return mExpiry;
    }
    /**
     * Observes the symbol.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value containing the symbol observable
     */
    public ITypedObservableValue<String> observeSymbol()
    {
        return mSymbol;
    }
    /**
     * Get the symbol value.
     *
     * @return an <code>ITypedObservableValue<String></code> value
     */
    protected final ITypedObservableValue<String> getSymbol()
    {
        return mSymbol;
    }
    /**
     * Get the expiry value.
     *
     * @return an <code>ITypedObservableValue<String></code> value
     */
    protected final ITypedObservableValue<String> getExpiry()
    {
        return mExpiry;
    }
    /**
     * 
     */
    private final ITypedObservableValue<String> mExpiry;
    /**
     * 
     */
    private final ITypedObservableValue<String> mSymbol;
}

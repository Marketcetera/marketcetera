package org.marketcetera.photon.ui.databinding;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Observes an instrument as a currency and provides child observables for the
 * currency components.
 *
 */
public class CurrencyObservable
        extends CompoundObservableManager<ITypedObservableValue<Instrument>>
{
    /**
     * Create a new CurrencyObservable instance.
     *
     * @param inParent an <code>ITypedObservableValue&lt;Instrument&gt</code> value
     */
    public CurrencyObservable(ITypedObservableValue<Instrument> inParent)
    {
        super(inParent);
        mSymbol = TypedObservableValueDecorator.create(String.class);
        mNearTenor = TypedObservableValueDecorator.create(String.class);
        mFarTenor = TypedObservableValueDecorator.create(String.class);
        init(ImmutableList.of(mSymbol,mNearTenor,mFarTenor));
    }
    /**
     * Observes the currency symbol.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> observeSymbol()
    {
        return mSymbol;
    }
    /**
     * Observes the currency near tenor.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> observeNearTenor()
    {
        return mNearTenor;
    }
    /**
     * Observes the currency far tenor.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> observeFarTenor()
    {
        return mFarTenor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateParent()
     */
    @Override
    protected void updateParent()
    {
        String symbol = mSymbol.getTypedValue();        
        String[] currencyPair = null;
        if(symbol!=null)        	
        {
        	currencyPair = symbol.split("/");
        }        
        String nearTenor = mNearTenor.getTypedValue();
        String farTenor = mFarTenor.getTypedValue();
        Currency newValue = null;
        if(currencyPair!=null && currencyPair.length==2 && StringUtils.isNotBlank(currencyPair[0]) 
        		&& StringUtils.isNotBlank(currencyPair[1]) && nearTenor != null)
        {
            try {
                    newValue = new Currency(currencyPair[0],currencyPair[1],nearTenor,farTenor);
            } catch (Exception ignored) {
            }
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
        if (instrument instanceof Currency) {
            Currency currency = (Currency)instrument;
            setIfChanged(mSymbol,currency.getSymbol());
            setIfChanged(mNearTenor,currency.getNearTenor());
            setIfChanged(mFarTenor,currency.getFarTenor());
        } else {
            setIfChanged(mSymbol,
                         null);
            setIfChanged(mNearTenor,
                         null);
            setIfChanged(mFarTenor,
                         null);
        }
    }
    /**
     * observes the currency symbol
     */
    private final ITypedObservableValue<String> mSymbol;
    /**
     * observes the currency near tenor
     */
    private final ITypedObservableValue<String> mNearTenor;
    /**
     * observes the currency far tenor
     */
    private final ITypedObservableValue<String> mFarTenor;
}

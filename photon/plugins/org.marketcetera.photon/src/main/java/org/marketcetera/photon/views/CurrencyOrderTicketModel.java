package org.marketcetera.photon.views;

import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.ui.databinding.CurrencyObservable;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The model for a currency order ticket.
 *
 */
@ClassVersion("$Id$")
public class CurrencyOrderTicketModel
        extends OrderTicketModel
{
    /**
     * Create a new CurrencyOrderTicketModel instance.
     */
    public CurrencyOrderTicketModel()
    {
        ITypedObservableValue<Instrument> instrument = getOrderObservable().observeInstrument();
        CurrencyObservable currencyObservable = new CurrencyObservable(instrument);
        mSymbol = currencyObservable.observeSymbol();
        mNearTenor = currencyObservable.observeNearTenor();
        mFarTenor = currencyObservable.observeFarTenor();
        mBaseCCY = currencyObservable.observeBaseCCY();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketModel#getSymbol()
     */
    @Override
    public ITypedObservableValue<String> getSymbol()
    {
        return mSymbol;
    }
    

    public ITypedObservableValue<String> getNearTenor() {
		return mNearTenor;
	}
	public ITypedObservableValue<String> getFarTenor() {
		return mFarTenor;
	}
	public ITypedObservableValue<Boolean> getBaseCCY() {
		return mBaseCCY;
	}

	/**
     * the symbol of the current order
     */
    private final ITypedObservableValue<String> mSymbol;
    /**
     * the near tenor of the current order
     */  
    private final ITypedObservableValue<String> mNearTenor;
    /**
     * the far tenor of the current order
     */  
    private final ITypedObservableValue<String> mFarTenor;
    
    /**
     * the base currency of the current order
     */  
    private final ITypedObservableValue<Boolean> mBaseCCY;
}

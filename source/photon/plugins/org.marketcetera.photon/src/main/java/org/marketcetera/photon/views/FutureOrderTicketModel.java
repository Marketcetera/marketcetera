package org.marketcetera.photon.views;

import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.ui.databinding.FutureObservable;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureOrderTicketModel
        extends ExpirableInstrumentOrderTicketModel
{
    /**
     * Create a new FutureOrderTicketModel instance.
     */
    @SuppressWarnings("unchecked")
    public FutureOrderTicketModel()
    {
        ITypedObservableValue<? extends Instrument> instrument = getOrderObservable().observeInstrument();
        FutureObservable futureObservable = new FutureObservable((ITypedObservableValue<Future>) instrument);
        mSymbol = futureObservable.observeSymbol();
        mOptionExpiry = futureObservable.observeExpiry();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketModel#getSymbol()
     */
    @Override
    public ITypedObservableValue<String> getSymbol()
    {
        return mSymbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ExpirableOrderTicketModel#getExpiry()
     */
    @Override
    public ITypedObservableValue<String> getExpiry()
    {
        return mOptionExpiry;
    }
    private final ITypedObservableValue<String> mSymbol;
    private final ITypedObservableValue<String> mOptionExpiry;
}

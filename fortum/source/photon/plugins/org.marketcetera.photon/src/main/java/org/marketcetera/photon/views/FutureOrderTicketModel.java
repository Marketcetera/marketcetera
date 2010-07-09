package org.marketcetera.photon.views;

import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.ui.databinding.FutureObservable;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The model for a future order ticket.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id")
public class FutureOrderTicketModel
        extends OrderTicketModel
{
    /**
     * Create a new FutureOrderTicketModel instance.
     */
    public FutureOrderTicketModel()
    {
        ITypedObservableValue<Instrument> instrument = getOrderObservable().observeInstrument();
        FutureObservable futureObservable = new FutureObservable(instrument);
        mSymbol = futureObservable.observeSymbol();
        mCustomerInfo = futureObservable.observeCustomerInfo();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketModel#getSymbol()
     */
    @Override
    public ITypedObservableValue<String> getSymbol()
    {
        return mSymbol;
    }
    /**
     * Gets an <code>ITypedObservableValue</code> that tracks the customer info of the current order.
     *
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public ITypedObservableValue<String> getCustomerInfo()
    {
        return mCustomerInfo;
    }
    /**
     * the symbol of the current order
     */
    private final ITypedObservableValue<String> mSymbol;
    /**
     * the customer info of the current order
     */
    private final ITypedObservableValue<String> mCustomerInfo;
}

package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Future;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates serialization of a {@link Future} to an Eclipse {@link IMemento}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureToMemento
        extends InstrumentToMemento<Future>
{
    /**
     * Create a new FutureToMemento instance.
     */
    public FutureToMemento()
    {
        super(Future.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.InstrumentToMemento#doSave(org.marketcetera.trade.Instrument, org.eclipse.ui.IMemento)
     */
    @Override
    protected void doSave(Future inFuture,
                          IMemento inMemento)
    {
        IMemento future = inMemento.createChild(FUTURE_TAG);
        future.putString(SYMBOL_ATTRIBUTE,
                         inFuture.getSymbol());
        future.putString(CUSTOMER_INFO_ATTRIBUTE,
                         inFuture.getCustomerInfo());
    }
    /**
     * the key for future mementos 
     */
    static final String FUTURE_TAG = "future"; //$NON-NLS-1$
    /**
     * the symbol attribute tag
     */
    static final String SYMBOL_ATTRIBUTE = "symbol"; //$NON-NLS-1$
    /**
     * the customer info attribute tag
     */
    static final String CUSTOMER_INFO_ATTRIBUTE = "customerInfo"; //$NON-NLS-1$
}

package org.marketcetera.photon.views;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Future;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
        future.putString(EXPIRY_ATTRIBUTE,
                         inFuture.getExpiry());
    }
    /**
     * 
     */
    static final String FUTURE_TAG = "future"; //$NON-NLS-1$
    /**
     * 
     */
    static final String SYMBOL_ATTRIBUTE = "symbol"; //$NON-NLS-1$
    /**
     * 
     */
    static final String EXPIRY_ATTRIBUTE = "expiry"; //$NON-NLS-1$

}

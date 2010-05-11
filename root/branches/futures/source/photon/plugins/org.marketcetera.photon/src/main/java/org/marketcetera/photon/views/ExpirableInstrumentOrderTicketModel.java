package org.marketcetera.photon.views;

import org.marketcetera.photon.commons.databinding.ITypedObservableValue;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ExpirableInstrumentOrderTicketModel
        extends OrderTicketModel
{
    /**
     * Returns an observable that tracks the expiry of the current order.
     * 
     * @return an <code>ITypedObservableValue&lt;String&gt;</code> value
     */
    public abstract ITypedObservableValue<String> getExpiry();
}

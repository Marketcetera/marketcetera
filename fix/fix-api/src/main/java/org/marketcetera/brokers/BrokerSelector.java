package org.marketcetera.brokers;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Selects a broker for an order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerSelector
{
    /**
     * Returns the ID of the broker the receiver selects for the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>BrokerID</code> value or or <code>null</code> if the selector cannot make a selection.
     */
    BrokerID chooseBroker(Order inOrder);
}

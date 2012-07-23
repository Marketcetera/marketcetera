package org.marketcetera.client;

import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Modifies orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
public interface OrderModifier
{
    /**
     * Examines the given order and optionally modifies it.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>boolean</code> value indicating if the order was modified
     */
    public boolean modify(Order inOrder);
}

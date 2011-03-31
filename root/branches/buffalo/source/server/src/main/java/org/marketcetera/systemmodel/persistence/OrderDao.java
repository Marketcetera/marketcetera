package org.marketcetera.systemmodel.persistence;

import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderDao
{
    /**
     * 
     *
     *
     * @param inOrder
     */
    public void write(Order inOrder);
    /**
     * 
     *
     *
     * @param inOrderID
     * @return
     */
    public Order getBy(OrderID inOrderID);
}

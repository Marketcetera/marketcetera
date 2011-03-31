package org.marketcetera.server.service;

import org.marketcetera.trade.*;
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
public interface OrderManager
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

package org.marketcetera.server.service;

import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
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
public interface OrderProcessor
{
    /**
     * 
     *
     *
     * @param inOrderSingle
     */
    public void send(OrderSingle inOrderSingle);
    /**
     * 
     *
     *
     * @param inOrderReplace
     */
    public void send(OrderReplace inOrderReplace);
    /**
     * 
     *
     *
     * @param inOrderCancel
     */
    public void send(OrderCancel inOrderCancel);
    /**
     * 
     *
     *
     * @param inFIXOrder
     */
    public void send(FIXOrder inFIXOrder);
}

package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A Single Order to trade a security. Instances of this type can be
 * created via {@link org.marketcetera.trade.Factory#createOrderSingle()}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface OrderSingle extends TradeMessage, OrderBase, NewOrReplaceOrder, Cloneable {
    /**
     * Creates clone of this order.
     *
     * @return the clone of this order
     */
    public OrderSingle clone();
}

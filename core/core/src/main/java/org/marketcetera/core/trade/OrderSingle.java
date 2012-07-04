package org.marketcetera.core.trade;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * A Single Order to trade a security. Instances of this type can be
 * created via {@link Factory#createOrderSingle()}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderSingle.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderSingle.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface OrderSingle extends TradeMessage, OrderBase, NewOrReplaceOrder, Cloneable {
    /**
     * Creates clone of this order.
     *
     * @return the clone of this order
     */
    public OrderSingle clone();
}

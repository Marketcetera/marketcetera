package org.marketcetera.core.trade;

/* $License$ */
/**
 * A Single Order to trade a security. Instances of this type can be
 * created via {@link Factory#createOrderSingle()}
 *
 * @version $Id: OrderSingle.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public interface OrderSingle extends TradeMessage, OrderBase, NewOrReplaceOrder, Cloneable {
    /**
     * Creates clone of this order.
     *
     * @return the clone of this order
     */
    public OrderSingle clone();
}

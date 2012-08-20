package org.marketcetera.core.trade;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * Details that are common between new and replace orders.
 * This message type is not meant to be used directly.   
 *
 * @author anshul@marketcetera.com
 * @version $Id: NewOrReplaceOrder.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: NewOrReplaceOrder.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface NewOrReplaceOrder extends OrderBase, Serializable {
    /**
     * Gets the OrderType for the Order.
     *
     * @return the order type.
     */
    OrderType getOrderType();

    /**
     * Sets the OrderType for the Order.
     *
     * @param inOrderType the order type.
     */
    void setOrderType(OrderType inOrderType);

    /**
     * Gets the time in force value for the Order. If a value
     * is not specified, it defaults to
     * {@link TimeInForce#Day}.
     *
     * @return the time in force value.
     */
    TimeInForce getTimeInForce();

    /**
     * Sets the time in force value for the Order.
     *
     * @param inTimeInForce the time in force value.
     */
    void setTimeInForce(TimeInForce inTimeInForce);

    /**
     * Gets the limit price for this order. A limit price should be
     * specified when the OrderType is {@link OrderType#Limit}.
     * This value is ignored if the OrderType is not {@link OrderType#Limit}.
     *
     *
     * @return the limit price for the order.
     */
    BigDecimal getPrice();

    /**
     * Sets the limit price for this order. A limit price should be
     * specified when the OrderType is {@link OrderType#Limit}.
     *
     * @param inPrice the limit price for the order.
     */
    void setPrice(BigDecimal inPrice);

    /**
     * Gets the order capacity value for this order.
     *
     * @return the order capacity value.
     */
    OrderCapacity getOrderCapacity();

    /**
     * Sets the order capacity value for this order.
     *
     * @param inOrderCapacity the order capacity value
     */
    void setOrderCapacity(OrderCapacity inOrderCapacity);

    /**
     * Gets the position effect for this order.
     *
     * @return the position effect value.
     */
    PositionEffect getPositionEffect();

    /**
     * Sets the position effect value for this order.
     *
     * @param inPositionEffect the position effect value.
     */
    void setPositionEffect(PositionEffect inPositionEffect);
}

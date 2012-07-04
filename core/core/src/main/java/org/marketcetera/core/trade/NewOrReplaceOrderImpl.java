package org.marketcetera.core.trade;

import java.math.BigDecimal;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Implementation of {@link NewOrReplaceOrder}
 *
 * @author anshul@marketcetera.com
 * @version $Id: NewOrReplaceOrderImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: NewOrReplaceOrderImpl.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
class NewOrReplaceOrderImpl extends OrderBaseImpl
        implements NewOrReplaceOrder {
    @Override
    public OrderType getOrderType() {
        return mOrderType;
    }

    @Override
    public void setOrderType(OrderType inOrderType) {
        mOrderType = inOrderType;
    }

    @Override
    public TimeInForce getTimeInForce() {
        return mTimeInForce;
    }

    @Override
    public void setTimeInForce(TimeInForce inTimeInForce) {
        mTimeInForce = inTimeInForce;
    }

    @Override
    public BigDecimal getPrice() {
        return mPrice;
    }

    @Override
    public void setPrice(BigDecimal inPrice) {
        mPrice = inPrice;
    }

    @Override
    public OrderCapacity getOrderCapacity() {
        return mOrderCapacity;
    }

    @Override
    public void setOrderCapacity(OrderCapacity inOrderCapacity) {
        mOrderCapacity = inOrderCapacity;
    }

    @Override
    public PositionEffect getPositionEffect() {
        return mPositionEffect;
    }

    @Override
    public void setPositionEffect(PositionEffect inPositionEffect) {
        mPositionEffect = inPositionEffect;
    }

    private OrderCapacity mOrderCapacity;
    private PositionEffect mPositionEffect;
    private OrderType mOrderType;
    private TimeInForce mTimeInForce;
    private BigDecimal mPrice;
    private static final long serialVersionUID = 1L;
}

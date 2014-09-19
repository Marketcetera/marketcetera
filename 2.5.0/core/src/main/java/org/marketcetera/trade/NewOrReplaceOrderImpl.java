package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Implementation of {@link NewOrReplaceOrder}
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class NewOrReplaceOrderImpl
        extends OrderBaseImpl
        implements NewOrReplaceOrder
{
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
    
    @Override
    public BigDecimal getDisplayQuantity() {
        return mDisplayQuantity;
    }

    @Override
    public void setDisplayQuantity(BigDecimal inDisplayQuantity) {
    	mDisplayQuantity = inDisplayQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.NewOrReplaceOrder#getBrokerAlgos()
     */
    @Override
    public BrokerAlgo getBrokerAlgo()
    {
        return brokerAlgo;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.NewOrReplaceOrder#setBrokerAlgos(java.util.Set)
     */
    @Override
    public void setBrokerAlgo(BrokerAlgo inBrokerAlgo)
    {
        brokerAlgo = inBrokerAlgo;
    }
    private OrderCapacity mOrderCapacity;
    private PositionEffect mPositionEffect;
    private OrderType mOrderType;
    private TimeInForce mTimeInForce;
    private BigDecimal mPrice;
    private BigDecimal mDisplayQuantity;
    private BrokerAlgo brokerAlgo;
    private static final long serialVersionUID = 3745608449209190015L;
}

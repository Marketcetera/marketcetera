package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * ReplaceOrder Implementation.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderReplaceImpl extends NewOrReplaceOrderImpl implements OrderReplace {

    @Override
    public OrderID getOriginalOrderID() {
        return mOriginalOrderID;
    }

    @Override
    public void setOriginalOrderID(OrderID inOrderID) {
        mOriginalOrderID = inOrderID;
    }

    @Override
    public String getBrokerOrderID() {
        return mDestOrderID;
    }

    @Override
    public void setBrokerOrderID(String inDestOrderID) {
        mDestOrderID = inDestOrderID;
    }

    @Override
    public String toString() {
        return Messages.ORDER_REPLACE_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getCustomFields()),
                String.valueOf(getBrokerID()),
                String.valueOf(getOrderCapacity()),
                String.valueOf(getOrderID()),
                String.valueOf(getOrderType()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getPositionEffect()),
                String.valueOf(getPrice()),
                String.valueOf(getQuantity()),
                String.valueOf(getSecurityType()),
                String.valueOf(getSide()),
                String.valueOf(getSymbol()),
                String.valueOf(getTimeInForce()),
                String.valueOf(getBrokerOrderID())
        );
    }

    private OrderID mOriginalOrderID;
    private static final long serialVersionUID = 1L;
    private String mDestOrderID;
}

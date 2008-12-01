package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Order Cancel message implementation.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderCancelImpl extends OrderBaseImpl implements OrderCancel {
    @Override
    public OrderID getOriginalOrderID() {
        return mOriginalOrderID;
    }

    @Override
    public void setOriginalOrderID(OrderID inOrderID) {
        mOriginalOrderID = inOrderID;
    }

    @Override
    public String getDestinationOrderID() {
        return mDestOrderID;
    }

    @Override
    public void setDestinationOrderID(String inDestOrderID) {
        mDestOrderID = inDestOrderID;
    }

    @Override
    public String toString() {
        return Messages.ORDER_CANCEL_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getCustomFields()),
                String.valueOf(getDestinationID()),
                String.valueOf(getOrderID()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getQuantity()),
                String.valueOf(getSecurityType()),
                String.valueOf(getSide()),
                String.valueOf(getSymbol()),
                String.valueOf(getDestinationOrderID())
        );
    }

    private OrderID mOriginalOrderID;
    private String mDestOrderID;
    private static final long serialVersionUID = 1L;
}

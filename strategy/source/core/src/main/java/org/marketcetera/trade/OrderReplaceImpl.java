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
class OrderReplaceImpl extends OrderSingleImpl implements OrderReplace {

    @Override
    public OrderID getOriginalOrderID() {
        return mOriginalOrderID;
    }

    @Override
    public void setOriginalOrderID(OrderID inOrderID) {
        mOriginalOrderID = inOrderID;
    }
    private OrderID mOriginalOrderID;
    private static final long serialVersionUID = 1L;
}

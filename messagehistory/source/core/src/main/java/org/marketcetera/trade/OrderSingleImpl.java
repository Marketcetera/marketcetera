package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Backing object for an order to trade a security.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderSingleImpl extends NewOrReplaceOrderImpl implements OrderSingle {

    @Override
    public OrderSingle clone() {
        try {
            // Since this instance has no modifiable fields, 
            // simply return the clone
            return (OrderSingleImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final long serialVersionUID = 1L;
}

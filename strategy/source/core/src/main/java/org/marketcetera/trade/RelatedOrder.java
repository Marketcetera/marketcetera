package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Orders that are related to an existing order. These orders
 * are usually used to cancel or replace an existing order. This type is
 * not meant to be used directly. Use one of its subtypes. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface RelatedOrder extends OrderBase {
    /**
     * The original order ID of the order that is being cancelled or replaced.
     *
     * @return the orderID of the order being cancelled or replaced.
     */
    OrderID getOriginalOrderID();

    /**
     * Set the original order ID of the order that is being cancelled.
     *
     * @param inOrderID the orderID of the order being cancelled.
     */
    void setOriginalOrderID(OrderID inOrderID);
}

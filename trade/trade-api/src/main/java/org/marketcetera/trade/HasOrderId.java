package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementer has an {@link OrderID}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasOrder.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public interface HasOrderId
{
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getOrderId();
    /**
     * Set the order id value.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    void setOrderId(OrderID inOrderId);
}

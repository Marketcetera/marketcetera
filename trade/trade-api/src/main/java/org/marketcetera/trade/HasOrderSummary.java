package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementer has an {@link OrderSummary}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasOrder.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public interface HasOrderSummary
{
    /**
     * Get the order summary value.
     *
     * @return an <code>OrderSummary</code> value
     */
    OrderSummary getOrderSummary();
}

package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementer has an {@link Order}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasOrder
{
    /**
     * Get the order value.
     *
     * @return an <code>Order</code> value
     */
    Order getOrder();
}

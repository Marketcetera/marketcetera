package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementor has an {@link OrderCancel} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasOrderCancel
{
    /**
     * Get the cancel order value.
     *
     * @return an <code>OrderCancel</code> value
     */
    OrderCancel getOrderCancel();
}

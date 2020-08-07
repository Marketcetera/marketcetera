package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementor has an {@link OrderReplace} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasOrderReplace
{
    /**
     * Get the replace order value.
     *
     * @return an <code>OrderReplace</code> value
     */
    OrderReplace getOrderReplace();
}

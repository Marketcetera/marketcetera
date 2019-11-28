package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementer has a root order id value
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasOrder.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public interface HasRootOrderId
{
    /**
     * Get the root order id value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getRootOrderId();
}

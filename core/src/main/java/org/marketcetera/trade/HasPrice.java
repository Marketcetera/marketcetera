package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Indicates the implementor has a <code>Price</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasPrice
{
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getPrice();
}

package org.marketcetera.trade;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummaryFactory
{
    /**
     * Create an order summary value.
     *
     * @return an <code>OrderSummary</code> value
     */
    OrderSummary create();
}

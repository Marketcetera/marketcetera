package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link MutableOrderSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableOrderSummaryFactory
        extends OrderSummaryFactory
{
    /**
     * Create a mutable order summary value.
     * 
     * @return a <code>MutableOrderSummary</code> value
     */
    MutableOrderSummary create();
}

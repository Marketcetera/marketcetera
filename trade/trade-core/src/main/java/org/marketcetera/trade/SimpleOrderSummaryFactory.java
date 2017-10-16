package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link MutableOrderSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOrderSummaryFactory
        implements MutableOrderSummaryFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummaryFactory#create()
     */
    @Override
    public MutableOrderSummary create()
    {
        return new SimpleOrderSummary();
    }
}

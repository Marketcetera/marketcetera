package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link SimpleExecutionReportSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleExecutionReportSummaryFactory
        implements MutableExecutionReportSummaryFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummaryFactory#create()
     */
    @Override
    public MutableExecutionReportSummary create()
    {
        return new SimpleExecutionReportSummary();
    }
}

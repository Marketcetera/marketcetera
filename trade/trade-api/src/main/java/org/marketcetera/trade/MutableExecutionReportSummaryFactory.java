package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link MutableExecutionReportSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReportFactory.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public interface MutableExecutionReportSummaryFactory
        extends ExecutionReportSummaryFactory
{
    /**
     * Create an execution report summary object.
     *
     * @return a <code>MutableExecutionReportSummary</code> value
     */
    @Override
    MutableExecutionReportSummary create();
}

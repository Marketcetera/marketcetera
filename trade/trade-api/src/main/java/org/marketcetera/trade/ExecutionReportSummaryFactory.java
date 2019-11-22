package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link ExecutionReportSummary} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReportFactory.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public interface ExecutionReportSummaryFactory
{
    /**
     * Create an execution report summary object.
     *
     * @return an <code>ExecutionReportSummary</code> value
     */
    ExecutionReportSummary create();
}

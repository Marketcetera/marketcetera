package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link Report} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ReportFactory
{
    /**
     * Create a report object.
     *
     * @return a <code>Report</code> value
     */
    Report create();
}

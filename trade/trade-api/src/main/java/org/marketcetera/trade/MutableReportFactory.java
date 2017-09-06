package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link MutableReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableReportFactory
        extends ReportFactory
{
    /**
     * Create a report object.
     *
     * @return a <code>MutableReport</code> value
     */
    MutableReport create();
}

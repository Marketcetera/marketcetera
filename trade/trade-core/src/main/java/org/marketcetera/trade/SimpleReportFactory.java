package org.marketcetera.trade;

/* $License$ */

/**
 * Creates {@link SimpleReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleReportFactory
        implements MutableReportFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReportFactory#create()
     */
    @Override
    public SimpleReport create()
    {
        return new SimpleReport();
    }
}

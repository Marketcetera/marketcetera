package org.marketcetera.trade;

import org.marketcetera.admin.User;

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
    /**
     * Create a report object.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     * @param inUser 
     * @return a <code>MutableReport</code> value
     */
    MutableReport create(TradeMessage inTradeMessage,
                         User inUser);
}

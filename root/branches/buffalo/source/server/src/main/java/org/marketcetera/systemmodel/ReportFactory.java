package org.marketcetera.systemmodel;

import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportFactory
{
    /**
     * 
     *
     *
     * @param inTradeMessage
     * @return
     */
    public Report createFrom(TradeMessage inTradeMessage);
}

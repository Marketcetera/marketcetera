package org.marketcetera.server.service;

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
public interface TradeMessageModifier
{
    /**
     * 
     *
     *
     * @param inTradeMessage
     */
    public void modify(TradeMessage inTradeMessage);
}

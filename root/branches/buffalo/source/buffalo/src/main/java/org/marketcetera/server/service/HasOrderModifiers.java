package org.marketcetera.server.service;

import java.util.List;

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
public interface HasOrderModifiers
{
    /**
     * 
     *
     *
     * @return
     */
    public List<OrderModifier> getOrderModifiers();
    /**
     * 
     *
     *
     * @return
     */
    public List<TradeMessageModifier> getTradeMessageModifiers();
}

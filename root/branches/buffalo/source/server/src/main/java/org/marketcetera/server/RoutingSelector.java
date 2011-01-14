package org.marketcetera.server;

import java.util.Set;

import org.marketcetera.server.config.OrderRouter;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RoutingSelector
{
    /**
     * 
     *
     *
     * @param inMessage
     * @return
     */
    public OrderRouter route(Message inMessage,
                             Set<OrderRouter> inOrderRouters);
}

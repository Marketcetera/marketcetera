package org.marketcetera.server;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.server.config.OrderRouter;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class RoundRobinRoutingSelector
        implements RoutingSelector
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.RoutingSelector#route(quickfix.Message)
     */
    @Override
    public OrderRouter route(Message inMessage,
                             Set<OrderRouter> inRouters)
    {
        if(inRouters.size() == 0) {
            return inRouters.iterator().next();
        }
        synchronized(this) {
            if(index == null ||
               !index.hasNext()) {
                index = inRouters.iterator();
            }
            return index.next();
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "RoundRobinRoutingSelector";
    }
    /**
     * 
     */
    @GuardedBy("this")
    private Iterator<OrderRouter> index;
}

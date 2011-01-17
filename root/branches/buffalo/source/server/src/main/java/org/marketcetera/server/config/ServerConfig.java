package org.marketcetera.server.config;

import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.server.RoutingSelector;
import org.marketcetera.util.misc.ClassVersion;

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
public class ServerConfig
{
    /**
     * 
     *
     *
     * @return
     */
    public static ServerConfig getInstance()
    {
        synchronized(ServerConfig.class) {
            return instance;
        }
    }
    /**
     * Create a new ServerConfig instance.
     *
     * @param inOrderRouters
     * @param inSelector
     * @throws IllegalArgumentException
     */
    public ServerConfig(OrderRouter.OrderRouters inOrderRouters,
                        RoutingSelector inSelector)
    {
        orderRouters = inOrderRouters.getRouters();
        routingSelector = inSelector;
//        Validate.notNull(orderRouters,
//                         "Must specify at least one order routing destination");
//        Validate.notEmpty(orderRouters,
//                          "Must specify at least one order routing destination");
//        Validate.notNull(routingSelector,
//                         "Must specify a routing selector");
        synchronized(ServerConfig.class) {
            instance = this;
        }
    }
    /**
     * Get the orderRouters value.
     *
     * @return a <code>Set&lt;OrderRouter&gt;</code> value
     */
    public Set<OrderRouter> getOrderRouters()
    {
        return orderRouters;
    }
    /**
     * 
     */
    private final Set<OrderRouter> orderRouters;
    /**
     * 
     */
    private final RoutingSelector routingSelector;
    /**
     * 
     */
    @GuardedBy("SpringConfig.class")
    private static ServerConfig instance;
}

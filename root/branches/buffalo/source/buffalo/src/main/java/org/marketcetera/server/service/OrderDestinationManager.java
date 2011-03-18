package org.marketcetera.server.service;

import java.util.Set;

import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the order destinations known to the system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderDestinationManager
{
    /**
     * 
     *
     *
     * @return
     */
    public Set<OrderDestination> getDestinations();
    /**
     * 
     *
     *
     * @param inOrder
     */
    public void send(Order inOrder);
}

package org.marketcetera.server.service;

import java.util.Set;

import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

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
        extends Lifecycle
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
    /**
     * 
     *
     *
     * @param inId
     * @return
     */
    public OrderDestination getOrderDestinationFor(OrderDestinationID inId);
    /**
     * 
     *
     *
     * @param inMessage
     * @param inOrderDestination
     */
    public void receive(TradeMessage inMessage,
                        OrderDestination inOrderDestination);
}

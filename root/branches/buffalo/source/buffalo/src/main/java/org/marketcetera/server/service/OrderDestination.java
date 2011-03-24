package org.marketcetera.server.service;

import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Represents a destination to which an {@link Order} can be sent.
 * 
 * <p>The destination may be an intermediary, a broker, or an exchange. Essentially,
 * an <code>OrderDestination</code> is anything to which an order can be sent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderDestination
        extends Lifecycle
{
    /**
     * Gets the status of the <code>OrderDestination</code>.
     *
     * @return a <code>DestinationStatus</code> value
     */
    public DestinationStatus getStatus();
    /**
     * Gets the name of the <code>OrderDestination</code>.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * Gets the ID of the <code>OrderDestination</code>.
     *
     * @return an <code>OrderDestinationID</code> value
     */
    public OrderDestinationID getId();
    /**
     * Sends an <code>Order</code> to the <code>OrderDestination</code>.
     *
     * @param inOrder an <code>Order</code> valu
     */
    public void send(Order inOrder);
}

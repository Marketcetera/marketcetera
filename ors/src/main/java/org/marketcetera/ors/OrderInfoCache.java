package org.marketcetera.ors;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A cache of order information. Cache entries are instances of {@link
 * OrderInfo}.
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface OrderInfoCache
{

    /**
     * Adds (and returns) a new entry to the receiver for an order
     * with the given self and parent IDs, and the given actor ID.
     *
     * @param orderID The order ID.
     * @param origOrderID The parent order ID. It may be null for
     * orders that are chain roots.
     * @param actorID The actor ID.
     *
     * @return The new entry.
     */

    public OrderInfo put
        (OrderID orderID,
         OrderID origOrderID,
         UserID actorID);

    /**
     * Returns the receiver's entry for the order with the given ID.
     *
     * @param orderID The order ID.
     *
     * @return The entry. It may be null if the received has no such
     * entry.
     */

    public OrderInfo get
        (OrderID orderID);
}

package org.marketcetera.ors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A cache of order information. Cache entries are instances of {@link
 * SimpleOrderInfo}.
 *
 * <p><strong>WARNING</strong>: certain cache entries may be
 * maintained forever. This may happen if, for example, a broker
 * response is expected (because an order has been sent to the
 * broker), but the response is never received. Or if the ORS cannot
 * persist the actor/viewer of an order. This behavior can leak to ORS
 * memory leaks over extended periods of time and under scenarios of
 * frequent faulty conditions.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SimpleOrderInfoCache
    implements OrderInfoCache
{

    // INSTANCE DATA.

    private final Map<OrderID,SimpleOrderInfo> mMap=
        new ConcurrentHashMap<OrderID,SimpleOrderInfo>();


    // INSTANCE METHODS.

    /**
     * Returns the receiver's in-memory map of order ID to order
     * information.
     *
     * @return The map.
     */

    private Map<OrderID,SimpleOrderInfo> getMap()
    {
        return mMap;
    }

    /**
     * Removes the receiver's entry for the order with the given ID.
     *
     * @param orderID The order ID.
     */

    public void remove
        (OrderID orderID)
    {
        getMap().remove(orderID);
        Messages.OIM_REMOVED_ENTRY.debug(this,orderID,getMap().size());
    }


    // OrderInfoCache.

    @Override
    public SimpleOrderInfo put
        (OrderID orderID,
         OrderID origOrderID,
         UserID actorID)
    {
        SimpleOrderInfo info=new SimpleOrderInfo
            (this,orderID,origOrderID,actorID);
        getMap().put(orderID,info);
        Messages.OIM_ADDED_ENTRY.debug(this,orderID,getMap().size());
        return info;
    }

    @Override
    public SimpleOrderInfo get
        (OrderID orderID)
    {
        return getMap().get(orderID);
    }
}

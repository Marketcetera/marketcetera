package org.marketcetera.trade.event;

import org.marketcetera.admin.User;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Provides a simple {@link OutgoingOrderEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOutgoingOrderEvent
        implements OutgoingOrderEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrder#getOrder()
     */
    @Override
    public Order getOrder()
    {
        return order;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.HasUser#getUser()
     */
    @Override
    public User getUser()
    {
        return user;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OutgoingOrderEvent [user=").append(user).append(", order=").append(order).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleOutgoingOrderEvent instance.
     *
     * @param inUser a <code>User</code> value
     * @param inOrder an <code>Order</code> value
     */
    public SimpleOutgoingOrderEvent(User inUser,
                                    Order inOrder)
    {
        user = inUser;
        order = inOrder;
    }
    /**
     * order value
     */
    private final Order order;
    /**
     * user that owns the order
     */
    private final User user;
}

package org.marketcetera.trade.modules;

import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Contains a user and the owner of that user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OwnedOrder
        implements HasOrder,HasUser
{
    /**
     * Create a new OwnedOrder instance.
     */
    public OwnedOrder() {}
    /**
     * Create a new OwnedOrder instance.
     *
     * @param inUser a <code>User</code> value
     * @param inOrder an <code>Order</code> value
     */
    public OwnedOrder(User inUser,
                      Order inOrder)
    {
        user = inUser;
        order = inOrder;
    }
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
        builder.append("OwnedOrder [user=").append(user).append(", order=").append(order).append("]");
        return builder.toString();
    }
    /**
     * Sets the user value.
     *
     * @param inUser a <code>User</code> value
     */
    public void setUser(User inUser)
    {
        user = inUser;
    }
    /**
     * Sets the order value.
     *
     * @param inOrder a <code>Order</code> value
     */
    public void setOrder(Order inOrder)
    {
        order = inOrder;
    }
    /**
     * user value
     */
    private User user;
    /**
     * order value
     */
    private Order order;
}

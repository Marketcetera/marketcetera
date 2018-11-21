package org.marketcetera.client;

import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Provides a wrapper class to use to submit an order to the outgoing data flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SubmitOrderWrapper
        implements HasOrder,HasUser,HasMutableStatus
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
     * @see org.marketcetera.trade.HasStatus#getFailed()
     */
    @Override
    public boolean getFailed()
    {
        return failed;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasStatus#setFailed(boolean)
     */
    @Override
    public void setFailed(boolean inFailed)
    {
        failed = inFailed;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasStatus#getMessage()
     */
    @Override
    public String getErrorMessage()
    {
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasStatus#setMessage(java.lang.String)
     */
    @Override
    public void setErrorMessage(String inMessage)
    {
        message = inMessage;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("RpcOrderWrapper [order=").append(order).append(", user=").append(user).append(", failed=")
        .append(failed).append(", message=").append(message).append(", start=").append(start).append("]");
        return builder.toString();
    }
    /**
     * Create a new SubmitOrderWrapper instance.
     *
     * @param inUser a <code>User</code> value
     * @param inOrder an <code>Order</code> value
     */
    public SubmitOrderWrapper(User inUser,
                              Order inOrder)
    {
        user = inUser;
        order = inOrder;
        failed = false;
    }
    /**
     * message value
     */
    private volatile String message;
    /**
     * failed value
     */
    private volatile boolean failed;
    /**
     * user value
     */
    private final User user;
    /**
     * order value
     */
    private final Order order;
    /**
     * start time stamp
     */
    private final long start = System.nanoTime();
}

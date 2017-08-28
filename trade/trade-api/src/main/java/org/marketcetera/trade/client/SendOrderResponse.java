package org.marketcetera.trade.client;

import org.marketcetera.trade.OrderID;

/* $License$ */

/**
 * Contains the information relevant to a sent order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SendOrderResponse
{
    /**
     * Get the orderId value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrderId()
    {
        return orderId;
    }
    /**
     * Sets the orderId value.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    public void setOrderId(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /**
     * Get the message value.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        return message;
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>String</code> value
     */
    public void setMessage(String inMessage)
    {
        message = inMessage;
    }
    /**
     * Get the failed value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getFailed()
    {
        return failed;
    }
    /**
     * Sets the failed value.
     *
     * @param inFailed a <code>boolean</code> value
     */
    public void setFailed(boolean inFailed)
    {
        failed = inFailed;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SendOrderResponse [orderId=").append(orderId).append(", failed=").append(failed)
                .append(", message=").append(message).append("]");
        return builder.toString();
    }
    /**
     * order id value
     */
    private OrderID orderId;
    /**
     * message value
     */
    private String message;
    /**
     * indicates if this order was sent properly or not
     */
    private boolean failed;
}

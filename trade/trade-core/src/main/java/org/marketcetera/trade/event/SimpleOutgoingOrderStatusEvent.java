package org.marketcetera.trade.event;

import org.apache.commons.lang.Validate;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;

/* $License$ */

/**
 * Provides a simple {@link OutgoingOrderStatusEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOutgoingOrderStatusEvent
        implements OutgoingOrderStatusEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.HasMutableStatus#setErrorMessage(java.lang.String)
     */
    @Override
    public void setErrorMessage(String inMessage)
    {
        message = inMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.HasMutableStatus#setFailed(boolean)
     */
    @Override
    public void setFailed(boolean inFailed)
    {
        failed = inFailed;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.HasStatus#getFailed()
     */
    @Override
    public boolean getFailed()
    {
        return failed;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.HasStatus#getErrorMessage()
     */
    @Override
    public String getErrorMessage()
    {
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return fixMessage;
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
     * @see org.marketcetera.trade.HasOrderId#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#setOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderId(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleOutgoingOrderStatusEvent [message=").append(message).append(", failed=").append(failed)
                .append(", fixMessage=").append(FIXMessageUtil.toHumanDelimitedString(fixMessage)).append(", order=").append(order).append(", orderId=")
                .append(orderId).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleOutgoingOrderStatus instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inFailed a <code>boolean</code> value
     * @param inOrder an <code>Order</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @param inFixMessage a <code>quickfix.Message</code> value
     */
    public SimpleOutgoingOrderStatusEvent(String inMessage,
                                          boolean inFailed,
                                          Order inOrder,
                                          OrderID inOrderId,
                                          quickfix.Message inFixMessage)
    {
        message = inMessage;
        failed = inFailed;
        fixMessage = inFixMessage;
        order = inOrder;
        orderId = inOrderId;
        Validate.notNull(orderId);
    }
    /**
     * message value
     */
    private String message;
    /**
     * failed value
     */
    private boolean failed;
    /**
     * FIX message value, may be <code>null</code>
     */
    private final quickfix.Message fixMessage;
    /**
     * outgoing order value, may be <code>null</code>
     */
    private final Order order;
    /**
     * outgoing order id value, may not be <code>null</code>
     */
    private OrderID orderId;
}

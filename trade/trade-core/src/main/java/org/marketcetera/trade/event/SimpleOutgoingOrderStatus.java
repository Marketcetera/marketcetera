package org.marketcetera.trade.event;

/* $License$ */

/**
 * Provides a simple {@link OutgoingOrderStatus} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOutgoingOrderStatus
        implements OutgoingOrderStatus
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
        return order;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OutgoingOrderStatus [order=").append(order).append(", message=").append(message)
                .append(", failed=").append(failed).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleOutgoingOrderStatus instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inFailed a <code>boolean</code> value
     * @param inOrder a <code>quickfix.Message</code> value
     */
    public SimpleOutgoingOrderStatus(String inMessage,
                                     boolean inFailed,
                                     quickfix.Message inOrder)
    {
        message = inMessage;
        failed = inFailed;
        order = inOrder;
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
     * order value
     */
    private final quickfix.Message order;
}

package org.marketcetera.trade.event;

import org.marketcetera.trade.OrderSummary;

/* $License$ */

/**
 * Provides a POJO {@link OrderSummaryEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOrderSummaryEvent
        implements OrderSummaryEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderSummary#getOrderSummary()
     */
    @Override
    public OrderSummary getOrderSummary()
    {
        return orderSummary;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleOrderSummaryEvent [orderSummary=").append(orderSummary).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleOrderSummaryEvent instance.
     *
     * @param inOrderSummary an <code>OrderSummary</code> value
     */
    public SimpleOrderSummaryEvent(OrderSummary inOrderSummary)
    {
        orderSummary = inOrderSummary;
    }
    /**
     * order summary value
     */
    private final OrderSummary orderSummary;
}

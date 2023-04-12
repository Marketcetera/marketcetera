package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Provides an {@link OrderCancelSuggestion} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderCancelSuggestionImpl
        extends AbstractSuggestion
        implements OrderCancelSuggestion
{
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     */
    public OrderCancelSuggestionImpl() {}
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     *
     * @param inOrderCancel an <code>OrderCancel</code> value
     */
    public OrderCancelSuggestionImpl(OrderCancel inOrderCancel)
    {
        setOrderCancel(inOrderCancel);
    }
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     *
     * @param inIdentifier a <code>String</code> value
     * @param inScore a <code>BigDecimal</code> value
     * @param inOrderCancel an <code>OrderCancel</code> value
     */
    public OrderCancelSuggestionImpl(String inIdentifier,
                                      BigDecimal inScore,
                                      OrderCancel inOrderCancel)
    {
        super(inIdentifier,
              inScore);
        setOrderCancel(inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderCancel#getOrderCancel()
     */
    @Override
    public OrderCancel getOrderCancel()
    {
        return orderCancel;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderCancelSuggestion#setOrderCancel(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void setOrderCancel(OrderCancel inOrderCancel)
    {
        orderCancel = inOrderCancel;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderCancelSuggestionImpl [identifier=").append(getIdentifier()).append(", score=")
                .append(getScore()).append(", orderCancel=").append(orderCancel).append("]");
        return builder.toString();
    }
    /**
     * order Cancel value
     */
    private OrderCancel orderCancel;
    private static final long serialVersionUID = 2069691082438028206L;
}

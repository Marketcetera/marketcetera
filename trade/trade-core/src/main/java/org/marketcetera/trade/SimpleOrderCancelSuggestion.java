package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Provides an {@link OrderCancelSuggestion} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOrderCancelSuggestion
        extends AbstractSuggestion
        implements OrderCancelSuggestion
{
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     */
    public SimpleOrderCancelSuggestion() {}
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     *
     * @param inOrderCancel an <code>OrderCancel</code> value
     */
    public SimpleOrderCancelSuggestion(OrderCancel inOrderCancel)
    {
        setOrderCancel(inOrderCancel);
    }
    /**
     * Create a new OrderCancelSuggestionImpl instance.
     *
     * @param inIdentifier a <code>String</code> value
     * @param inScore a <code>BigDecimal</code> value
     * @param inUser a <code>User</code> value
     * @param inOrderCancel an <code>OrderCancel</code> value
     */
    public SimpleOrderCancelSuggestion(String inIdentifier,
                                       BigDecimal inScore,
                                       User inUser,
                                       OrderCancel inOrderCancel)
    {
        super(inIdentifier,
              inScore,
              inUser);
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
        builder.append("OrderCancelSuggestionImpl [user=").append(getUser()).append(", identifier=").append(getIdentifier()).append(", score=")
                .append(getScore()).append(", orderCancel=").append(orderCancel).append("]");
        return builder.toString();
    }
    /**
     * order Cancel value
     */
    private OrderCancel orderCancel;
    private static final long serialVersionUID = 2069691082438028206L;
}

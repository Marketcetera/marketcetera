package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.admin.User;

/* $License$ */
/**
 * Implementation for Single Order Suggestions.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class SimpleOrderSingleSuggestion
        extends AbstractSuggestion
        implements OrderSingleSuggestion
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSingleSuggestion#getOrder()
     */
    @Override
    public OrderSingle getOrder()
    {
        return orderSingle;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSingleSuggestion#setOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void setOrder(OrderSingle inOrder)
    {
        orderSingle = inOrder;
    }
    /**
     * Create a new SimpleOrderSingleSuggestion instance.
     */
    public SimpleOrderSingleSuggestion()
    {
        super();
    }
    /**
     * Create a new SimpleOrderSingleSuggestion instance.
     *
     * @param inOrder an <code>OrderSingle</code> value
     */
    public SimpleOrderSingleSuggestion(OrderSingle inOrder)
    {
        setOrder(inOrder);
    }
    /**
     * Create a new SimpleOrderSingleSuggestion instance.
     *
     * @param inIdentifier a <code>String</code> value
     * @param inScore a <code>BigDecimal</code> value
     * @param inUser a <code>User</code> value
     * @param inOrderSingle an <code>OrderSingle</code> value
     */
    public SimpleOrderSingleSuggestion(String inIdentifier,
                                       BigDecimal inScore,
                                       User inUser,
                                       OrderSingle inOrderSingle)
    {
        super(inIdentifier,
              inScore,
              inUser);
        setOrder(inOrderSingle);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderSingleSuggestionImpl [user=").append(getUser()).append(", identifier=").append(getIdentifier()).append(", score=")
                .append(getScore()).append(", orderReplace=").append(orderSingle).append("]");
        return builder.toString();
    }
    /**
     * order single for this suggestion
     */
    private OrderSingle orderSingle;
    private static final long serialVersionUID = -2340633219171494896L;
}

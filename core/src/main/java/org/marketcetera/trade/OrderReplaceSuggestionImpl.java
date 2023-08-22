package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Provides an {@link OrderReplaceSuggestion} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderReplaceSuggestionImpl
        extends AbstractSuggestion
        implements OrderReplaceSuggestion
{
    /**
     * Create a new OrderReplaceSuggestionImpl instance.
     */
    public OrderReplaceSuggestionImpl() {}
    /**
     * Create a new OrderReplaceSuggestionImpl instance.
     *
     * @param inOrderReplace an <code>OrderReplace</code> value
     */
    public OrderReplaceSuggestionImpl(OrderReplace inOrderReplace)
    {
        setOrderReplace(inOrderReplace);
    }
    /**
     * Create a new OrderReplaceSuggestionImpl instance.
     *
     * @param inIdentifier a <code>String</code> value
     * @param inScore a <code>BigDecimal</code> value
     * @param inOrderReplace an <code>OrderReplace</code> value
     */
    public OrderReplaceSuggestionImpl(String inIdentifier,
                                      BigDecimal inScore,
                                      OrderReplace inOrderReplace)
    {
        super(inIdentifier,
              inScore);
        setOrderReplace(inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderReplace#getOrderReplace()
     */
    @Override
    public OrderReplace getOrderReplace()
    {
        return orderReplace;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderReplaceSuggestion#setOrderReplace(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void setOrderReplace(OrderReplace inOrderReplace)
    {
        orderReplace = inOrderReplace;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderReplaceSuggestionImpl [identifier=").append(getIdentifier()).append(", score=")
                .append(getScore()).append(", orderReplace=").append(orderReplace).append("]");
        return builder.toString();
    }
    /**
     * order replace value
     */
    private OrderReplace orderReplace;
    private static final long serialVersionUID = 2069691082438028206L;
}

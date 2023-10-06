package org.marketcetera.trade;

/* $License$ */

/**
 * Factory for creating various {@link Suggestion} objects.
 *
 * @author colin@marketcetera.com
 * @version $Id$
 * @since 4.2.0
 */
public class SimpleSuggestionFactory
        implements SuggestionFactory
{
    @Override
    public OrderSingleSuggestion createOrderSingleSuggestion()
    {
        return new SimpleOrderSingleSuggestion();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Factory#createOrderCancelSuggestion()
     */
    @Override
    public OrderCancelSuggestion createOrderCancelSuggestion()
    {
        return new SimpleOrderCancelSuggestion();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Factory#createOrderReplaceSuggestion()
     */
    @Override
    public OrderReplaceSuggestion createOrderReplaceSuggestion()
    {
        return new SimpleOrderReplaceSuggestion();
    }
}

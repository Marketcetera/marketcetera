package org.marketcetera.core.trade;

/* $License$ */
/**
 * A Suggestion for a single order. Instances of this type
 * can be created via {@link Factory#createOrderSingleSuggestion()}
 *
 * @version $Id$
 * @since 1.0.0
 */
public interface OrderSingleSuggestion extends Suggestion {
    /**
     * Returns the order suggested by this suggestion.
     *
     * @return the order suggested by this suggestion.
     */
    public OrderSingle getOrder();

    /**
     * Sets the order suggested by this suggestion.
     *
     * @param inOrder the suggested order.
     */
    public void setOrder(OrderSingle inOrder);
}

package org.marketcetera.trade;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * A Suggestion for a single order. Instances of this type
 * can be created via {@link Factory#createOrderSingleSuggestion()}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderSingleSuggestion.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderSingleSuggestion.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
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

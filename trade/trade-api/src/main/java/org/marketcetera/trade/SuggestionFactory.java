package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Creates {@link Suggestion} objects.
 *
 * @author colin@marketcetera.com
 * @version $Id$
 * @since 4.2.0
 */
@ClassVersion("$Id$")
public interface SuggestionFactory
{
    /**
     * Creates a suggestion for a new order to trade a security.
     *
     * @return a suggestion for a new order.
     */
    OrderSingleSuggestion createOrderSingleSuggestion();
    /**
     * Creates a suggestion for a cancel order.
     *
     * @return an <code>OrderCancelSuggestion</code> value
     */
    OrderCancelSuggestion createOrderCancelSuggestion();
    /**
     * Creates a suggestion for a replace order.
     *
     * @return an <code>OrderReplaceSuggestion</code> value
     */
    OrderReplaceSuggestion createOrderReplaceSuggestion();
}

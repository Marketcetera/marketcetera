package org.marketcetera.core.trade;

/* $License$ */
/**
 * An order to replace a previously placed order. Instances of this
 * type can be created via {@link Factory#createOrderReplace(ExecutionReport)}.
 *
 * @version $Id$
 * @since 1.0.0
 */
public interface OrderReplace extends TradeMessage, RelatedOrder, NewOrReplaceOrder {
}

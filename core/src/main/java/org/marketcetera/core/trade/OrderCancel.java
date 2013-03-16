package org.marketcetera.core.trade;

/* $License$ */
/**
 * An order to cancel a previously placed order. Instances
 * of this order can be created via
 * {@link Factory#createOrderCancel(ExecutionReport)}.
 *
 * @version $Id$
 * @since 1.0.0
 */
public interface OrderCancel extends TradeMessage, RelatedOrder {
}

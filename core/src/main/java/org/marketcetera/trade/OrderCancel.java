package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An order to cancel a previously placed order. Instances
 * of this order can be created via
 * {@link Factory#createOrderCancel(ExecutionReport)}.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderCancel.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderCancel.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface OrderCancel extends TradeMessage, RelatedOrder {
}

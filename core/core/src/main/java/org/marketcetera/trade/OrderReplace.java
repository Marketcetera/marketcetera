package org.marketcetera.trade;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * An order to replace a previously placed order. Instances of this
 * type can be created via {@link Factory#createOrderReplace(ExecutionReport)}.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderReplace.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderReplace.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface OrderReplace extends TradeMessage, RelatedOrder, NewOrReplaceOrder {
}

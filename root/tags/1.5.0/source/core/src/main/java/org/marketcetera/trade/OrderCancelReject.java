package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Represents the rejection of an {@link OrderCancel} or a
 * {@link OrderReplace}. Instances of this message can be created via
 * {@link Factory#createOrderCancelReject(quickfix.Message, BrokerID, Originator)}.
 * <p>
 * The enum attributes of this type have a null value, in case a value
 * is not specified for that attribute / field in the underlying FIX Message.
 * However, if the attribute / field has a value that does not have a
 * corresponding value in the Enum type, the sentinel value <code>Unknown</code>
 * is returned to indicate that the value is set but is not currently
 * expressible through the current API.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface OrderCancelReject extends TradeMessage, ReportBase {
}

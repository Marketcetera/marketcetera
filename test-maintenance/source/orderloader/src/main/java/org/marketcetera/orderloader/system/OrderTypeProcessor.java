package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * Extracts an {@link OrderType} value from an order row and sets it
 * on the supplied order.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class OrderTypeProcessor
        extends EnumProcessor<OrderType> {
    /**
     * Creates an instance.
     *
     * @param inIndex the index at which the order capacity value can be
     * found in the order.
     */
    OrderTypeProcessor(int inIndex) {
        super(OrderType.class, OrderType.Unknown,
                Messages.INVALID_ORDER_TYPE, inIndex);
    }

    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        inOrder.setOrderType(getEnumValue(inRow));
    }
}

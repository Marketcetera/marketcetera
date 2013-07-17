package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * Extracts an {@link Side} value from an order row and sets it
 * on the supplied order.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class SideProcessor extends EnumProcessor<Side> {
    /**
     * Creates an instance.
     *
     * @param inIndex the column index at which the side value can be
     * found in an order row.
     */
    SideProcessor(int inIndex) {
        super(Side.class, Side.Unknown,
                Messages.INVALID_SIDE, inIndex);
    }

    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        inOrder.setSide(getEnumValue(inRow));
    }
}

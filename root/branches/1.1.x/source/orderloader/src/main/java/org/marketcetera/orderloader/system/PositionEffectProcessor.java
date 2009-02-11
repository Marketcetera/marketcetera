package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * Extracts an {@link PositionEffect} value from an order row and sets it
 * on the supplied order.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
final class PositionEffectProcessor extends EnumProcessor<PositionEffect> {
    /**
     * Creates an instance.
     *
     * @param inIndex the index at which the position effect value can found
     * in the order row.
     */
    PositionEffectProcessor(int inIndex) {
        super(PositionEffect.class, PositionEffect.Unknown,
                Messages.INVALID_POSITION_EFFECT, inIndex);
    }
    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        inOrder.setPositionEffect(getEnumValue(inRow));
    }
}

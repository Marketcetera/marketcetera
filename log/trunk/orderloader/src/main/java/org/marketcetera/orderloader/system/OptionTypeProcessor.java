package org.marketcetera.orderloader.system;

import org.marketcetera.orderloader.Messages;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Extracts an {@link org.marketcetera.trade.OptionType} value from an order row and sets it
 * on the supplied order.
 * This processor is not used directly to parse out OptionType value. It's
 * instead used by {@link OptionFromRow} to create an instrument value.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
final class OptionTypeProcessor extends EnumProcessor<OptionType> {
    /**
     * Creates an instance.
     *
     * @param inIndex the index at which the option type value can
     * be found in an order row.
     */
    OptionTypeProcessor(int inIndex) {
        super(OptionType.class, OptionType.Unknown,
                Messages.INVALID_OPTION_TYPE, inIndex);
    }

    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        //do nothing as this method is never invoked.
    }
}
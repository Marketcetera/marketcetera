package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * Extracts an {@link SecurityType} value from an order row and sets it
 * on the supplied order.
 * This processor is not used directly to parse out SecurityType value. It's
 * instead used by {@link SymbolProcessor} to create symbol value.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class SecurityTypeProcessor extends EnumProcessor<SecurityType> {
    /**
     * Creates an instance.
     *
     * @param inIndex the index at which the security type value can
     * be found in an order row.
     */
    SecurityTypeProcessor(int inIndex) {
        super(SecurityType.class, SecurityType.Unknown,
                Messages.INVALID_SECURITY_TYPE, inIndex);
    }

    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        //do nothing as this method is never invoked.
    }
}

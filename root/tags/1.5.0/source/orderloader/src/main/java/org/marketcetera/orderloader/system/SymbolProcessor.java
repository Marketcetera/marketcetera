package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * A processor that parses a symbol value from an order row and sets it
 * on the supplied order.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class SymbolProcessor implements FieldProcessor {
    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        String symbol = inRow[mSymbolIdx];
        if(symbol == null || symbol.isEmpty()) {
            return;
        }
        if (mSecurityProcessor != null) {
            inOrder.setSymbol(new MSymbol(symbol,
                    mSecurityProcessor.getEnumValue(inRow)));
        } else {
            inOrder.setSymbol(new MSymbol(symbol));
        }
    }

    /**
     * Sets the column index at which the symbol value is located.
     *
     * @param inSymbolIdx the column index for the symbol.
     */
    public void setSymbolIdx(int inSymbolIdx) {
        mSymbolIdx = inSymbolIdx;
    }

    /**
     * Sets the security type index at which the security type value is
     * located.
     *
     * @param inSecurityTypeIdx the column index for security type value.
     */
    public void setSecurityTypeIdx(int inSecurityTypeIdx) {
        mSecurityProcessor = new SecurityTypeProcessor(inSecurityTypeIdx);
    }

    private SecurityTypeProcessor mSecurityProcessor = null;
    private int mSymbolIdx;
}

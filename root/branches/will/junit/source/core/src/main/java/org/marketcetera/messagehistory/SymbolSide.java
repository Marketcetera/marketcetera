package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MemoizedHashCombinator;
import org.marketcetera.trade.MSymbol;


/**
 * SymbolSide implements a two component tuple for
 * (security) symbol, and (order) side.  A conceptual
 * example being "BUY IBM".  This is useful when trying 
 * to group messages in a list based on a match of this
 * tuple, for example when calculating average prices.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SymbolSide extends MemoizedHashCombinator<MSymbol, String> {
    /**
     * Create a new SymbolSide with the specified {@link MSymbol} and
     * string value for side.
     * 
     * @param symbol the symbol
     * @param side the side, one of the values from {@link quickfix.field.Side}
     * @see quickfix.field.Side
     */
    public SymbolSide(MSymbol symbol, String side){
        super(symbol, side);
    }
}

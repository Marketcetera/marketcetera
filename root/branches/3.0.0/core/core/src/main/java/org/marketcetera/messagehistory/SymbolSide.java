package org.marketcetera.messagehistory;

import org.marketcetera.core.MemoizedHashCombinator;
import org.marketcetera.trade.Instrument;
import org.marketcetera.core.attributes.ClassVersion;


/**
 * SymbolSide implements a two component tuple for
 * instrument, and (order) side.  A conceptual
 * example being "BUY IBM".  This is useful when trying 
 * to group messages in a list based on a match of this
 * tuple, for example when calculating average prices.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id: SymbolSide.java 82326 2012-04-10 16:27:07Z colin $")
public class SymbolSide extends MemoizedHashCombinator<Instrument, String> {
    /**
     * Create a new SymbolSide with the specified {@link Instrument} and
     * string value for side.
     * 
     * @param instrument the instrument
     * @param side the side, one of the values from {@link quickfix.field.Side}
     * @see quickfix.field.Side
     */
    public SymbolSide(Instrument instrument, String side){
        super(instrument, side);
    }
}

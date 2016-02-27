package org.marketcetera.symbol;

import java.util.regex.Pattern;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Resolves symbol patterns to {@link Future} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureSymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolver#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        if(FIX_SYMBOL_PATTERN.matcher(inSymbol).matches()) {
            return Future.fromString(inSymbol);
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolver#generateSymbol(org.marketcetera.trade.Instrument)
     */
    @Override
    public String generateSymbol(Instrument inInstrument)
    {
        if(inInstrument instanceof Future) {
            return inInstrument.getFullSymbol();
        }
        return null;
    }
    private static final Pattern FIX_SYMBOL_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,}-[0-9]{6,6}$"); //$NON-NLS-1$
}

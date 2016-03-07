package org.marketcetera.symbol;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Resolves given symbol patterns to {@link Equity} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EquitySymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolver#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        return new Equity(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolver#generateSymbol(org.marketcetera.trade.Instrument)
     */
    @Override
    public String generateSymbol(Instrument inInstrument)
    {
        return inInstrument.getSymbol();
    }
}

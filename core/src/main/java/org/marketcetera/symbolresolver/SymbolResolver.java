package org.marketcetera.symbolresolver;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Resolves symbols to instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SymbolResolver.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: SymbolResolver.java 82384 2012-07-20 19:09:59Z colin $")
public interface SymbolResolver
{
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value or <code>null</code> if the symbol could not be resolved
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public Instrument resolve(String inSymbol);
    /**
     * Resolves the given symbol to an <code>Instrument</code> using the given context.
     *
     * @param inSymbol a <code>String</code> value
     * @param inContext an <code>Object</code> value or <code>null</code>
     * @return an <code>Instrument</code> value or <code>null</code> if the symbol could not be resolved
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public Instrument resolve(String inSymbol,
                              Object inContext);
}

package org.marketcetera.api.symbolresolver;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Resolves symbols to instruments.
 *
 * @version $Id: SymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
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

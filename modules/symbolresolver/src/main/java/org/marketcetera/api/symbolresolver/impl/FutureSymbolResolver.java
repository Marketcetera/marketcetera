package org.marketcetera.api.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.api.systemmodel.instruments.Instrument;
import org.marketcetera.core.trade.FutureImpl;

/* $License$ */

/**
 * Attempts to resolve symbols to {@link org.marketcetera.core.trade.FutureImpl} instruments.
 *
 * @version $Id: FutureSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class FutureSymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolver#resolve(java.lang.String)
     */
    @Override
    public Instrument resolve(String inSymbol)
    {
        return resolve(inSymbol,
                       null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolver#resolve(java.lang.String, java.lang.Object)
     */
    @Override
    public Instrument resolve(String inSymbol,
                              Object inContext)
    {
        try {
            return FutureImpl.fromString(inSymbol);
        } catch (IllegalArgumentException e) {
            // nope, not a future
        }
        return null;
    }
}

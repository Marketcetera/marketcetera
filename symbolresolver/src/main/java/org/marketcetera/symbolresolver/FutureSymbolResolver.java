package org.marketcetera.symbolresolver;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.symbolresolver.SymbolResolver;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.impl.FutureImpl;

/* $License$ */

/**
 * Attempts to resolve symbols to {@link org.marketcetera.core.trade.impl.FutureImpl} instruments.
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

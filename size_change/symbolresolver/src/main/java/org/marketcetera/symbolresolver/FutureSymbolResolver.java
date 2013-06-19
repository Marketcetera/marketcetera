package org.marketcetera.symbolresolver;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.symbolresolver.SymbolResolverElement;
import org.marketcetera.core.trade.Future;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Attempts to resolve symbols to {@link org.marketcetera.core.trade.Future} instruments.
 *
 * @version $Id: FutureSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class FutureSymbolResolver
        implements SymbolResolverElement
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
            return Future.fromString(inSymbol);
        } catch (IllegalArgumentException e) {
            // nope, not a future
        }
        return null;
    }
}

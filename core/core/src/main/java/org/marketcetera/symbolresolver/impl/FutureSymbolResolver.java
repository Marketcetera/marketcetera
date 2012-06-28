package org.marketcetera.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.symbolresolver.SymbolResolver;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Attempts to resolve symbols to {@link Future} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: FutureSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $")
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
            return Future.fromString(inSymbol);
        } catch (IllegalArgumentException e) {
            // nope, not a future
        }
        return null;
    }
}

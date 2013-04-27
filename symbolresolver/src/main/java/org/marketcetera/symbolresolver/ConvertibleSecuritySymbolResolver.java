package org.marketcetera.symbolresolver;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.symbolresolver.SymbolResolverElement;
import org.marketcetera.core.trade.ConvertibleSecurity;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Attempts to convert symbols to {@link org.marketcetera.core.trade.ConvertibleSecurity} instruments.
 *
 * @version $Id: ConvertibleSecuritySymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class ConvertibleSecuritySymbolResolver
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
            return new ConvertibleSecurity(inSymbol);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

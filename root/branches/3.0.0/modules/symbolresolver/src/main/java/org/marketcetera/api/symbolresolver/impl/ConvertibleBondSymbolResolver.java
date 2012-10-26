package org.marketcetera.api.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.api.systemmodel.instruments.Instrument;
import org.marketcetera.core.trade.ConvertibleBondImpl;

/* $License$ */

/**
 * Attempts to convert symbols to {@link org.marketcetera.core.trade.ConvertibleBondImpl} instruments.
 *
 * @version $Id: ConvertibleBondSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class ConvertibleBondSymbolResolver
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
        return new ConvertibleBondImpl(inSymbol);
    }
}

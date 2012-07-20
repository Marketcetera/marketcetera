package org.marketcetera.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.symbolresolver.SymbolResolver;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Attempts to convert symbols to {@link ConvertibleBond} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConvertibleBondSymbolResolver.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: ConvertibleBondSymbolResolver.java 82384 2012-07-20 19:09:59Z colin $")
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
        return new ConvertibleBond(inSymbol);
    }
}

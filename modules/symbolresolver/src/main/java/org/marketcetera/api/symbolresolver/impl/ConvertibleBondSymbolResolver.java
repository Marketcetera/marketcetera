package org.marketcetera.api.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.trade.ConvertibleBond;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Attempts to convert symbols to {@link org.marketcetera.core.trade.ConvertibleBond} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConvertibleBondSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: ConvertibleBondSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $")
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

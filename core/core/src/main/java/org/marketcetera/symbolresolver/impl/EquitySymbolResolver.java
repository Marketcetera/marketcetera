package org.marketcetera.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.symbolresolver.SymbolResolver;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Attempts to resolve symbols as {@link Equity} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EquitySymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: EquitySymbolResolver.java 82347 2012-05-03 19:30:54Z colin $")
public class EquitySymbolResolver
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
        return new Equity(inSymbol);
    }
}

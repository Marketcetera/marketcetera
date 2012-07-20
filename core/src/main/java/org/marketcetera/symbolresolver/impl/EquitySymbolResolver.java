package org.marketcetera.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.symbolresolver.SymbolResolver;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Attempts to resolve symbols as {@link Equity} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EquitySymbolResolver.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: EquitySymbolResolver.java 82384 2012-07-20 19:09:59Z colin $")
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

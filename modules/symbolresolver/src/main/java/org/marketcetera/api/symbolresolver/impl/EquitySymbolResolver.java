package org.marketcetera.api.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Attempts to resolve symbols as {@link org.marketcetera.core.trade.Equity} instruments.
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

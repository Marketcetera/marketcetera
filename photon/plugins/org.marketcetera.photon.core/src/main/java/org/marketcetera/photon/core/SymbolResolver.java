package org.marketcetera.photon.core;

import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides symbol resolution services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SymbolResolver
        implements ISymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.photon.instrument.SymbolResolver#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        return symbolResolver.resolveSymbol(inSymbol);
    }
    /**
     * delegate symbol resolver from core product
     */
    private PatternSymbolResolver symbolResolver = new PatternSymbolResolver();
}

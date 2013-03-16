package org.marketcetera.core.symbolresolver;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SymbolResolutionService
        implements SymbolResolver
{
    /**
     * Create a new SymbolResolutionService instance.
     */
    public SymbolResolutionService()
    {
    }
    /**
     * Get the symbolResolver value.
     *
     * @return a <code>MockSymbolResolver</code> value
     */
    public MockSymbolResolver getSymbolResolver()
    {
        return symbolResolver;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.symbolresolver.SymbolResolver#resolve(java.lang.String)
     */
    @Override
    public Instrument resolve(String inSymbol)
    {
        return symbolResolver.resolve(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.symbolresolver.SymbolResolver#resolve(java.lang.String, java.lang.Object)
     */
    @Override
    public Instrument resolve(String inSymbol,
                              Object inContext)
    {
        return symbolResolver.resolve(inSymbol,
                                      inContext);
    }
    /**
     * symbol resolver value
     */
    private final MockSymbolResolver symbolResolver = new MockSymbolResolver();
}

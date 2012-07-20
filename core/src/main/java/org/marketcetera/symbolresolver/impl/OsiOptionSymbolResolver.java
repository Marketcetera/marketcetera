package org.marketcetera.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.options.OptionUtils;
import org.marketcetera.symbolresolver.SymbolResolver;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Attempts to resolve symbols to {@link Option} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: OsiOptionSymbolResolver.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: OsiOptionSymbolResolver.java 82384 2012-07-20 19:09:59Z colin $")
public class OsiOptionSymbolResolver
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
            return OptionUtils.getOsiOptionFromString(inSymbol);
        } catch (IllegalArgumentException e) {
            // no option, no soup
        }
        return null;
    }
}

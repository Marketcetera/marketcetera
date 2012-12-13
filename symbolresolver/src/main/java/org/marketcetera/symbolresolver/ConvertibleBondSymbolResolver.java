package org.marketcetera.symbolresolver;

import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.symbolresolver.SymbolResolverElement;
import org.marketcetera.core.trade.ConvertibleBond;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Attempts to convert symbols to {@link org.marketcetera.core.trade.ConvertibleBond} instruments.
 *
 * @version $Id: ConvertibleBondSymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class ConvertibleBondSymbolResolver
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
        if(isin.matcher(inSymbol).matches()) {
            return new ConvertibleBond(inSymbol);
        }
        return null;
    }
    private static final Pattern isin = Pattern.compile("[A-Z]{2}([A-Z0-9]){9}[0-9]");
}

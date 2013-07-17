package org.marketcetera.trade.utils;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Provides symbol-to-<code>Instrument</code> resolution services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SymbolResolver
{
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     * @throws UnresolvableSymbol if the symbol cannot be resolved
     */
    public Instrument resolveSymbol(String inSymbol)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        try {
            return Future.fromString(inSymbol);
        } catch (IllegalArgumentException ignored) {}
        try {
            return OptionUtils.getOsiOptionFromString(inSymbol);
        } catch (IllegalArgumentException ignored) {}
        if(inSymbol.contains("/")) { //$NON-NLS-1$
            return new Currency(inSymbol);
        }
        return new Equity(inSymbol);
    }
}

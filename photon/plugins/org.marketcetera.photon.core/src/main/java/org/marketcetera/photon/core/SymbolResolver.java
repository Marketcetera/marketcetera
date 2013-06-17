package org.marketcetera.photon.core;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
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
        // TODO insert something similar for Currency
        return new Equity(inSymbol);
    }
}

package org.marketcetera.symbol;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Resolves a symbol to an <code>Instrument</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface SymbolResolver
{
    /**
     * Resolves the given symbol to an instrument.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    public Instrument resolveSymbol(String inSymbol);
}

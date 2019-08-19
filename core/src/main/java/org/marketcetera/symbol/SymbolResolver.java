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
     * Resolve the given symbol to an instrument.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    Instrument resolveSymbol(String inSymbol);
    /**
     * Generate a symbol from the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a code>String</code> value
     */
    String generateSymbol(Instrument inInstrument);
}

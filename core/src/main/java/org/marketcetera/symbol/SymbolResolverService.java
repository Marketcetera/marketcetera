package org.marketcetera.symbol;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides symbol resolution services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface SymbolResolverService
{
    /**
     * Resolve the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> or <code>null</code> if the symbol could not be resolved
     */
    public Instrument resolveSymbol(String inSymbol);
    /**
     * Generate a symbol from the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>String</code> value
     */
    public String generateSymbol(Instrument inInstrument);
}

package org.marketcetera.symbol;

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
@ClassVersion("$Id: SpringConfig.java 16663 2013-08-23 14:40:19Z colin $")
public interface SymbolResolverService
{
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> or <code>null</code> if the symbol could not be resolved
     */
    public Instrument resolveSymbol(String inSymbol);
}

package org.marketcetera.ors.symbol;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Resolves a symbol to an <code>Instrument</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: ServiceImpl.java 16661 2013-08-22 17:51:57Z colin $")
public interface SymbolResolver
{
    public Instrument resolveSymbol(String inSymbol);
}

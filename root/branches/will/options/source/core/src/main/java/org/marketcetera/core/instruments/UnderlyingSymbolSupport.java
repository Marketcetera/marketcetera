package org.marketcetera.core.instruments;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for providers of underlying symbol for an instrument.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface UnderlyingSymbolSupport {

    /**
     * Returns the underlying symbol for the supplied instrument.
     * 
     * @param instrument
     *            the instrument
     * 
     * @return the underlying symbol for the supplied instrument
     */
    String getUnderlying(Instrument instrument);
}

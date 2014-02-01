package org.marketcetera.core.instruments;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for providers of underlying symbol for an instrument. The
 * underlying symbol is a loosely defined piece of metadata that is used for
 * things like grouping instruments together (e.g. equities with their
 * derivative options). This interface is used to abstract the business logic
 * for determining that value.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface UnderlyingSymbolSupport {

    /**
     * Returns the underlying symbol for the supplied instrument.
     * 
     * @param instrument the instrument
     * 
     * @return the underlying symbol for the supplied instrument, never null
     */
    String getUnderlying(Instrument instrument);
}

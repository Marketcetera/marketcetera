package org.marketcetera.trade;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A financial instrument.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@Immutable
public interface Instrument {

    /**
     * Returns the symbol for the instrument, if any.
     * 
     * @return the symbol, null if the instrument has none
     */
    String getSymbol();
}

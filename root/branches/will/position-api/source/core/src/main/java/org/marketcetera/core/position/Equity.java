package org.marketcetera.core.position;

import javax.annotation.Nonnull;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an equity.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Equity extends Instrument {

    /**
     * Returns equity symbol.
     * 
     * @return the equity symbol, never null or empty string
     */
    @Nonnull
    String getSymbol();
}

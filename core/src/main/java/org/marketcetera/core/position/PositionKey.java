package org.marketcetera.core.position;

import javax.annotation.Nonnull;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The tuple that identifies a unique position.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionKey<T extends Instrument> {

    /**
     * Returns the instrument used by this position.
     * 
     * @return the instrument, never null
     */
    @Nonnull
    T getInstrument();

    /**
     * Returns the account of the key.
     * 
     * @return the account of the key, null if unknown
     */
    String getAccount();

    /**
     * Returns the trader id of the key.
     * 
     * @return the trader id of the key, null if unknown
     */
    String getTraderId();
}

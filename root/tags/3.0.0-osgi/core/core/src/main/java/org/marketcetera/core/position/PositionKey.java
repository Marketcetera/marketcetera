package org.marketcetera.core.position;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * The tuple that identifies a unique position.
 * 
 * @version $Id: PositionKey.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@Immutable
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

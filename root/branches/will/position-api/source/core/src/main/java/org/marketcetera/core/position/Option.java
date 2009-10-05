package org.marketcetera.core.position;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an option.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Option extends Instrument {

    enum Type {
        CALL, PUT;
    }

    /**
     * Returns the option root symbol.
     * 
     * @return the option root symbol, never null or empty string
     */
    @Nonnull
    String getSymbol();

    /**
     * Returns the option type.
     * 
     * @return the option symbol, never null
     */
    @Nonnull
    Type getType();

    /**
     * Returns the option expiry.
     * 
     * @return the option expiry, never null or empty string
     */
    @Nonnull
    String getExpiry();

    /**
     * Returns the option strike price.
     * 
     * @return the option strike price, never null
     */
    @Nonnull
    BigDecimal getStrikePrice();
}

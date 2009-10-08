package org.marketcetera.core.position;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A trade that is used to compute position information.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface Trade<T extends Instrument> {
    
    /**
     * Return the position that this trade is associated with.
     * 
     * @return the position key, never null
     */
    @Nonnull
    PositionKey<T> getPositionKey();

    /**
     * Return the price of the trade.
     * 
     * @return the price, must be greater than zero
     */
    @Nonnull
    BigDecimal getPrice();

    /**
     * Return the quantity of the trade. A positive quantity implies a buy and a
     * negative quantity implies a sell.
     * 
     * @return the quantity, cannot be null or zero
     */
    @Nonnull
    BigDecimal getQuantity();

    /**
     * Return the unique sequence number of the trade used for ordering trades.
     * No two trades can have the same sequence number.
     * 
     * @return the sequence number, must be greater than zero
     */
    long getSequenceNumber();
}

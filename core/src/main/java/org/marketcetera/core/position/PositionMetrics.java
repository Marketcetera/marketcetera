package org.marketcetera.core.position;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface to computed position-related metrics. Since the position and realize P&amp;L are not
 * dependent on market data, they should never be null. Other values may be null if the necessary
 * market data is unavailable.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionMetrics {

    /**
     * Returns the number of shares at the start of the trading session. A positive value indicates
     * a long position and a negative value indicates a short position.
     * 
     * @return the incoming position, should never be null
     */
    BigDecimal getIncomingPosition();

    /**
     * Returns the number of shares currently held in the position. A positive value indicates a
     * long position and a negative value indicates a short position.
     * 
     * @return the position, should never be null
     */
    BigDecimal getPosition();

    /**
     * Returns the current P&amp;L value of the incoming position. A positive value indicates a profit
     * and a negative value indicates a loss.
     * 
     * @return the positional P&amp;L, null if unknown
     */
    BigDecimal getPositionPL();

    /**
     * Returns the current P&amp;L value of the day's trading activity. A positive value indicates a
     * profit and a negative value indicates a loss.
     * 
     * @return the trading P&amp;L, null if unknown
     */
    BigDecimal getTradingPL();

    /**
     * Returns the P&amp;L value of the positions closed during the current day. A positive value
     * indicates a profit and a negative value indicates a loss.
     * 
     * @return the realized P&amp;L, null if unknown
     */
    BigDecimal getRealizedPL();

    /**
     * Returns the current P&amp;L value of the open positions. A positive value indicates a profit and
     * a negative value indicates a loss.
     * 
     * @return the unrealized P&amp;L, null if unknown
     */
    BigDecimal getUnrealizedPL();

    /**
     * Returns the current total P&amp;L value which is the sum of the realized and unrealized P&amp;L (it
     * is also the sum of the positional and trading P&amp;L). A positive value indicates a profit and a
     * negative value indicates a loss.
     * 
     * @return the total P&amp;L, null if unknown
     */
    BigDecimal getTotalPL();

}

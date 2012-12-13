package org.marketcetera.core.event;

import java.math.BigDecimal;

/* $License$ */

/**
 * Represents the set of statistics of a specific {@link org.marketcetera.core.trade.Option}.
 *
 * @version $Id$
 * @since 2.0.0
 */
public interface OptionMarketstatEvent
        extends MarketstatEvent, OptionEvent
{
    /**
     * Gets the change in volume since the previous close.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getVolumeChange();
    /**
     * Gets the change in interest since the previous close.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getInterestChange();
}

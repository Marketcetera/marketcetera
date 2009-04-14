package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An algorithm that computes position metrics from either market data ticks or
 * trades.
 * 
 * Implementations must preserve an internal state such that the metrics
 * returned build upon each other and always reflect the current position
 * information given the sequence of method invocations.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionMetricsCalculator {

    /**
     * Compute the updated metrics given a new share value, i.e. a new last
     * trade price in the market.
     * 
     * @param tradePrice
     *            the new trading price
     * @return the updated metrics
     */
    PositionMetrics tick(BigDecimal tradePrice);

    /**
     * Compute the updated metrics given a new trade that has contributed to the
     * position. Trades provided through this method must be given in sequential
     * order for proper accounting.
     * 
     * @param trade
     *            the new trade
     * @return the updated metrics
     */
    PositionMetrics trade(Trade trade);

}
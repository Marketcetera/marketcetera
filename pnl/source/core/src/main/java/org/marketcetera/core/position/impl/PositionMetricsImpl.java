package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple immutable implementation of {@link PositionMetrics}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionMetricsImpl implements PositionMetrics {

    private final BigDecimal position;
    private final BigDecimal positionPL;
    private final BigDecimal tradingPL;
    private final BigDecimal realizedPL;
    private final BigDecimal unrealizedPL;
    private final BigDecimal totalPL;

    /**
     * Constructor.
     * 
     * @param position
     * @param positionPL
     * @param tradingPL
     * @param realizedPL
     * @param unrealizedPL
     * @param totalPL
     * @throws IllegalArgumentException
     *             if any parameters are null
     */
    public PositionMetricsImpl(BigDecimal position, BigDecimal positionPL, BigDecimal tradingPL,
            BigDecimal realizedPL, BigDecimal unrealizedPL, BigDecimal totalPL) {
        this.position = position;
        this.positionPL = positionPL;
        this.tradingPL = tradingPL;
        this.realizedPL = realizedPL;
        this.unrealizedPL = unrealizedPL;
        this.totalPL = totalPL;
    }

    @Override
    public BigDecimal getPosition() {
        return position;
    }

    @Override
    public BigDecimal getPositionPL() {
        return positionPL;
    }

    @Override
    public BigDecimal getTradingPL() {
        return tradingPL;
    }

    @Override
    public BigDecimal getRealizedPL() {
        return realizedPL;
    }

    @Override
    public BigDecimal getUnrealizedPL() {
        return unrealizedPL;
    }

    @Override
    public BigDecimal getTotalPL() {
        return totalPL;
    }
}

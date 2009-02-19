package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionMetrics;

public interface PositionMetricsCalculator {

    PositionMetrics tick(BigDecimal tradePrice);

    PositionMetrics trade(Trade trade);

}
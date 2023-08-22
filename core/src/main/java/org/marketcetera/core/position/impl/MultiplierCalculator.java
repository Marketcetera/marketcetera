package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * <p>A calculator that wraps another calculator and multiplies all P&amp;L values by a
 * multiplier.</p>
 * 
 * <p>If the multiplier is unavailable, this class effectively nulls out all PNL
 * values.</p>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class MultiplierCalculator
        implements PositionMetricsCalculator
{

    private final PositionMetricsCalculator mDelegate;
    private final BigDecimal mMultiplier;

    /**
     * Constructor.
     * 
     * @param delegate
     *            the calculator implementation
     * @param multiplier
     *            the multiplier to apply to P&amp;L values, null if unavailable
     */
    public MultiplierCalculator(PositionMetricsCalculator delegate,
            BigDecimal multiplier) {
        mDelegate = delegate;
        mMultiplier = multiplier;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.PositionMetricsCalculator#bid(java.math.BigDecimal)
     */
    @Override
    public PositionMetrics bid(BigDecimal inBidPrice)
    {
        return multiply(mDelegate.bid(inBidPrice));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.PositionMetricsCalculator#ask(java.math.BigDecimal)
     */
    @Override
    public PositionMetrics ask(BigDecimal inAskPrice)
    {
        return multiply(mDelegate.ask(inAskPrice));
    }
    @Override
    public PositionMetrics tick(BigDecimal tradePrice) {
        return multiply(mDelegate.tick(tradePrice));
    }

    @Override
    public PositionMetrics trade(Trade<?> trade) {
        return multiply(mDelegate.trade(trade));
    }

    private PositionMetrics multiply(PositionMetrics metrics) {
        return new PositionMetricsImpl(metrics.getIncomingPosition(), metrics
                .getPosition(), multiply(metrics.getPositionPL()),
                multiply(metrics.getTradingPL()), multiply(metrics
                        .getRealizedPL()), multiply(metrics.getUnrealizedPL()),
                multiply(metrics.getTotalPL()));
    }

    private BigDecimal multiply(BigDecimal pnl) {
        if (mMultiplier == null || pnl == null) {
            return null;
        } else {
            return pnl.multiply(mMultiplier);
        }
    }
}

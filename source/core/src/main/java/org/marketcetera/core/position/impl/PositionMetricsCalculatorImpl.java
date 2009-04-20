package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An implementation of {@link PositionMetricsCalculator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class PositionMetricsCalculatorImpl implements PositionMetricsCalculator {

    private final BigDecimal mIncomingPosition;
    private final CostElement mPositionCost = new CostElement();
    private final CostElement tradingCost = new CostElement();
    private final CostElement mUnrealizedCost = new CostElement();
    private final LinkedList<PositionElement> mPositionElements = new LinkedList<PositionElement>();
    private final boolean mClosingPriceAvailable;
    private BigDecimal mLastTradePrice;
    private BigDecimal mPosition;
    private BigDecimal mRealizedPL = BigDecimal.ZERO;

    /**
     * Constructor.
     * 
     * @param incomingPosition
     *            the incoming position that will be used to calculate position PL
     * @param closingPrice
     *            the closing price that will be used to calculate position PL
     * @throws IllegalArgumentException if incomingPosition is null
     */
    public PositionMetricsCalculatorImpl(final BigDecimal incomingPosition, BigDecimal closingPrice) {
        Validate.notNull(incomingPosition);
        mIncomingPosition = incomingPosition;
        mPosition = incomingPosition;
        mClosingPriceAvailable = closingPrice != null;
        if (mClosingPriceAvailable) {
            mPositionCost.add(mPosition, closingPrice);
            mUnrealizedCost.add(mPosition, closingPrice);
            mPositionElements.add(new PositionElement(mPosition, closingPrice));
        }
    }

    @Override
    public synchronized PositionMetrics tick(final BigDecimal tradePrice) {
        mLastTradePrice = tradePrice;
        return createPositionMetrics();
    }

    @Override
    public synchronized PositionMetrics trade(final Trade trade) {
        processTrade(trade.getQuantity(), trade.getPrice());
        return createPositionMetrics();
    }

    /**
     * Processes a trade, closing existing positions and creating new ones as necessary.
     * 
     * @param quantity
     *            the quantity of the trade, positive for a buy and negative for a sell
     * @param price
     *            the price of the trade
     */
    private void processTrade(final BigDecimal quantity, final BigDecimal price) {
        mPosition = mPosition.add(quantity);
        // only bother with PNL if the closing price is available
        if (mClosingPriceAvailable) {
            tradingCost.add(quantity, price);
            // determine the sides, +1 for long and -1 for short
            int holdingSide = mUnrealizedCost.signum();
            int tradingSide = quantity.signum();
            BigDecimal remaining = quantity;
            // if sides are different
            if (tradingSide * holdingSide == -1) {
                // close positions
                while (!mPositionElements.isEmpty()) {
                    // get the oldest open position
                    PositionElement toClose = mPositionElements.peek();
                    // add the remaining trade quantity
                    BigDecimal leftover = toClose.quantity.add(remaining);
                    int leftoverSide = leftover.signum();
                    // if there is leftover on the open position
                    if (leftoverSide == holdingSide) {
                        // the trade only partially closed this position
                        processClose(remaining, toClose.price, price);
                        toClose.quantity = leftover;
                        // trade has been completely processed
                        return;
                    } else {
                        // the trade completely closed this position
                        processClose(toClose.quantity.negate(), toClose.price, price);
                        mPositionElements.remove();
                        remaining = leftover;
                        // if leftover is zero
                        if (leftoverSide == 0) {
                            // trade has been completely processed
                            return;
                        }
                    }
                }
            }
            // if non-zero remaining quantity
            if (remaining.signum() != 0) {
                // create new position
                mPositionElements.add(new PositionElement(remaining, price));
                mUnrealizedCost.add(remaining, price);
            }
        }

    }

    /**
     * Processes a position close, updating realized P&L and the unrealized cost
     * 
     * @param quantity
     *            the quantity being closed, negative when closing a long position and positive when
     *            closing a short position
     * @param openPrice
     *            the price at which the position was opened
     * @param closePrice
     *            the price at which the position is closing
     */
    private void processClose(final BigDecimal quantity, final BigDecimal openPrice,
            final BigDecimal closePrice) {
        // subtract closePrice from openPrice since quantity has opposite sign
        // more readable may be:
        // quantity.negate().multiply(closePrice.subtract(openPrice))
        mRealizedPL = mRealizedPL.add(quantity.multiply(openPrice.subtract(closePrice)));
        mUnrealizedCost.add(quantity, openPrice);
    }

    private PositionMetrics createPositionMetrics() {
        BigDecimal unrealizedPL = null;
        BigDecimal realizedPL = null;
        BigDecimal tradingPL = null;
        BigDecimal positionPL = null;
        BigDecimal totalPL = null;
        if (mClosingPriceAvailable) {
            realizedPL = mRealizedPL;
            if (mLastTradePrice != null) {
                positionPL = mPositionCost.getPL(mLastTradePrice);
                unrealizedPL = mUnrealizedCost.getPL(mLastTradePrice);
                tradingPL = tradingCost.getPL(mLastTradePrice);
                totalPL = realizedPL.add(unrealizedPL);
            }
        }
        PositionMetricsImpl positionMetrics = new PositionMetricsImpl(mIncomingPosition,
                mPosition, positionPL, tradingPL, realizedPL, unrealizedPL, totalPL);
        // Theoretically, both ways of calculating total PL should give the same results
        assert !mClosingPriceAvailable || mLastTradePrice == null
                || totalPL.compareTo(positionPL.add(tradingPL)) == 0 : positionMetrics;
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            if (mLastTradePrice != null && totalPL.compareTo(positionPL.add(tradingPL)) != 0) {
                SLF4JLoggerProxy.debug(this, MessageFormat.format(
                        "There is a discrepancy in the total PL.\n{0}", positionMetrics)); //$NON-NLS-1$
            }
        }
        return positionMetrics;
    }

    private static class CostElement {
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal cost = BigDecimal.ZERO;

        public void add(BigDecimal quantity, BigDecimal price) {
            this.quantity = this.quantity.add(quantity);
            cost = cost.add(quantity.multiply(price));
        }

        public BigDecimal getPL(BigDecimal lastTradePrice) {
            return quantity.multiply(lastTradePrice).subtract(cost);
        }

        public int signum() {
            return quantity.signum();
        }

    }

    private class PositionElement {
        public BigDecimal quantity;
        public BigDecimal price;

        public PositionElement(BigDecimal quantity, BigDecimal price) {
            this.quantity = quantity;
            this.price = price;
        }
    }

}
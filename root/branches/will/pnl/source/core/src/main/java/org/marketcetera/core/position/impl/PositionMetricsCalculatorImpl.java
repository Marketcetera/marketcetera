package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An implementation of {@link PositionMetricsCalculator}.
 * 
 * TODO: add incoming position support
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class PositionMetricsCalculatorImpl implements PositionMetricsCalculator {

    private BigDecimal position = BigDecimal.ZERO;
    private BigDecimal realizedPL = BigDecimal.ZERO;
    private final CostElement tradingCost = new CostElement();
    private final CostElement unrealizedCost = new CostElement();
    private final LinkedList<PositionElement> positionElements = new LinkedList<PositionElement>();
    private BigDecimal lastTradePrice;

   @Override
    public synchronized PositionMetrics tick(BigDecimal tradePrice) {
        lastTradePrice = tradePrice;
        return createPositionMetrics();
    }

    @Override
    public synchronized PositionMetrics trade(Trade trade) {
        BigDecimal quantity = trade.getQuantity();
        BigDecimal price = trade.getPrice();
        switch (trade.getSide()) {
        case BUY:
            processTrade(quantity, price);
            break;
        case SELL:
            processTrade(quantity.negate(), price);
            break;
        default:
            assert false;
        }
        return createPositionMetrics();
    }

    /**
     * Processes a trade, closing existing positions and creating new ones as
     * necessary.
     * 
     * @param quantity
     *            the quantity of the trade, positive for a buy and negative for
     *            a sell
     * @param price
     *            the price of the trade
     */
    private void processTrade(final BigDecimal quantity, final BigDecimal price) {
        assert quantity.compareTo(BigDecimal.ZERO) != 0;
        assert price.compareTo(BigDecimal.ZERO) == 1;
        position = position.add(quantity);
        tradingCost.add(quantity, price);
        // determine the sides, +1 for long and -1 for short
        int holdingSide = unrealizedCost.signum();
        int tradingSide = quantity.signum();
        BigDecimal remaining = quantity;
        // if sides are different
        if (tradingSide * holdingSide == -1) {
            // close positions
            while (!positionElements.isEmpty()) {
                // get the oldest open position
                PositionElement toClose = positionElements.peek();
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
                    positionElements.remove();
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
            positionElements.add(new PositionElement(remaining, price));
            unrealizedCost.add(remaining, price);
        }

    }

    /**
     * Processes a position close, updating realized P&L and the unrealized cost
     * 
     * @param quantity
     *            the quantity being closed, negative when closing a long
     *            position and positive when closing a short position
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
        realizedPL = realizedPL.add(quantity.multiply(openPrice.subtract(closePrice)));
        unrealizedCost.add(quantity, openPrice);
    }

    private PositionMetrics createPositionMetrics() {
        BigDecimal unrealizedPL = BigDecimal.ZERO;
        BigDecimal tradingPL = BigDecimal.ZERO;
        // since no incoming positions, positionPL is zero for now
        BigDecimal positionPL = BigDecimal.ZERO;
        BigDecimal totalPL = BigDecimal.ZERO;
        if (lastTradePrice != null) {
            unrealizedPL = unrealizedCost.getPL(lastTradePrice);
            tradingPL = tradingCost.getPL(lastTradePrice);
            totalPL = positionPL.add(tradingPL);
        }
        return new PositionMetricsImpl(position, positionPL, tradingPL, realizedPL, unrealizedPL,
                totalPL);
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
        BigDecimal quantity;
        BigDecimal price;

        PositionElement(Trade trade) {
            quantity = trade.getQuantity();
            price = trade.getPrice();
        }

        public PositionElement(BigDecimal quantity, BigDecimal price) {
            this.quantity = quantity;
            this.price = price;
        }

    }

}
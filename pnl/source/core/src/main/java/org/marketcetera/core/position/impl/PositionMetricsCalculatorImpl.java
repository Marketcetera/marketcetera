package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An implementation of {@link PositionMetricsCalculator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionMetricsCalculatorImpl implements PositionMetricsCalculator {

    private BigDecimal position = BigDecimal.ZERO;
    private BigDecimal positionPL = BigDecimal.ZERO;
    private BigDecimal tradingPL = BigDecimal.ZERO;
    private BigDecimal realizedPL = BigDecimal.ZERO;
    private BigDecimal unrealizedPL = BigDecimal.ZERO;
    private BigDecimal totalPL = BigDecimal.ZERO;
    private CostElement tradingCost = new CostElement();
    private CostElement unrealizedCost = new CostElement();
    private LinkedList<PositionElement> positionElements = new LinkedList<PositionElement>();
    private BigDecimal lastTradePrice;

    private PositionMetrics createPositionMetrics() {
        return new PositionMetricsImpl(position, positionPL, tradingPL, realizedPL, unrealizedPL,
                totalPL);
    }

    @Override
    public synchronized PositionMetrics tick(BigDecimal tradePrice) {
        lastTradePrice = tradePrice;
        updateUnrealized();
        return createPositionMetrics();
    }

    private void updateUnrealized() {
        if (lastTradePrice != null) {
            unrealizedPL = unrealizedCost.getPL(lastTradePrice);
            tradingPL = tradingCost.getPL(lastTradePrice);
            totalPL = positionPL.add(tradingPL);
            // System.out.println("Total discrepancy: " +
            // totalPL.subtract(unrealizedPL.add(realizedPL)));
            // System.out.println(unrealizedPL
            // + " "
            // + realizedPL
            // + " "
            // + tradingPL
            // + " "
            // + positionPL);
            // assert totalPL.compareTo(unrealizedPL.add(realizedPL)) == 0 :
            // unrealizedPL
            // + " "
            // + realizedPL
            // + " "
            // + tradingPL
            // + " "
            // + positionPL;
        }
    }

    @Override
    public synchronized PositionMetrics trade(Trade trade) {
        BigDecimal quantity = trade.getQuantity();
        BigDecimal price = trade.getPrice();
        switch (trade.getSide()) {
        case BUY:
            position = position.add(quantity);
            process(quantity, price);
            tradingCost.add(quantity, price);
            break;
        case SELL:
            position = position.subtract(quantity);
            process(quantity.negate(), price);
            tradingCost.subtract(quantity, price);
            break;
        default:
            assert false;
        }
        updateUnrealized();
        return createPositionMetrics();
    }

    private void process(BigDecimal quantity, final BigDecimal price) {
        assert quantity.compareTo(BigDecimal.ZERO) != 0;
        assert price.compareTo(BigDecimal.ZERO) == 1;
        while (!positionElements.isEmpty()) {
            PositionElement toClose = positionElements.peek();
            int side = toClose.quantity.signum();
            if (quantity.signum() * side != -1) break;
            BigDecimal remaining = toClose.quantity.add(quantity);
            int remainingSide = remaining.signum();
            if (remainingSide == side) {
                realizedPL = realizedPL.add(close(toClose, quantity, price));
                toClose.quantity = remaining;
                quantity = BigDecimal.ZERO;
                break;
            } else {
                realizedPL = realizedPL.add(close(toClose, toClose.quantity.negate(), price));
                positionElements.remove();
                if (remainingSide == 0) {
                    quantity = BigDecimal.ZERO;
                    break;
                } else {
                    quantity = remaining;
                }
            }
        }
        // create new position
        if (quantity.signum() != 0) {
            addElement(quantity, price);
        }

    }

    private void addElement(BigDecimal quantity, BigDecimal price) {
        positionElements.add(new PositionElement(quantity, price));
        unrealizedCost.add(quantity, price);
    }

    private BigDecimal close(PositionElement element, BigDecimal quantity, BigDecimal price) {
        BigDecimal value = quantity.multiply(price);
        return unrealizedCost.add(quantity, element.price).subtract(value);
    }

    private static class CostElement {
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal cost = BigDecimal.ZERO;

        public BigDecimal add(BigDecimal quantity, BigDecimal price) {
            this.quantity = this.quantity.add(quantity);
            BigDecimal change = quantity.multiply(price);
            cost = cost.add(change);
            return change;
        }

        public BigDecimal subtract(BigDecimal quantity, BigDecimal cost) {
            return add(quantity.negate(), cost);
        }

        public BigDecimal getPL(BigDecimal lastTradePrice) {
            return quantity.multiply(lastTradePrice).subtract(cost);
        }

    }

    private static class PositionElement {
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
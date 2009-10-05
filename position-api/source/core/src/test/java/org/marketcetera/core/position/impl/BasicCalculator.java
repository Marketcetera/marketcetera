package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;

/* $License$ */

/**
 * Basic implementation of {@link PositionMetricsCalculator} that recomputes each value from scratch
 * every time.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class BasicCalculator implements PositionMetricsCalculator {

    private List<Trade> mTrades = new ArrayList<Trade>();
    private BigDecimal mTick;
    private final BigDecimal mIncomingPosition;
    private final BigDecimal mClosingPrice;

    public BasicCalculator(BigDecimal incomingPosition, BigDecimal closingPrice) {
        mIncomingPosition = incomingPosition;
        mClosingPrice = closingPrice;
    }

    public PositionMetrics tick(String current) {
        return tick(new BigDecimal(current));
    }

    @Override
    public PositionMetrics tick(BigDecimal current) {
        this.mTick = current;
        return createPositionMetrics();
    }

    @Override
    public PositionMetrics trade(Trade trade) {
        mTrades.add(trade);
        return createPositionMetrics();
    }

    private PositionMetrics createPositionMetrics() {
        BigDecimal positionPL = getPositionPL();
        BigDecimal tradingPL = getTradingPL();
        BigDecimal totalPL = null;
        if (mTick != null) {
            totalPL = positionPL.add(tradingPL);
        }
        return new PositionMetricsImpl(mIncomingPosition, getPosition(), positionPL,
                tradingPL, getRealizedPL(), getUnrealizedPL(), totalPL);
    }

    private BigDecimal getPosition() {
        BigDecimal position = mIncomingPosition;
        for (Trade trade : mTrades) {
            position = position.add(trade.getQuantity());
        }
        return position;
    }

    private BigDecimal getPositionPL() {
        if (mTick == null) {
            return null;
        }
        return mIncomingPosition.multiply(mTick.subtract(mClosingPrice));
    }

    private BigDecimal getTradingPL() {
        if (mTick == null) {
            return null;
        }
        BigDecimal trading = BigDecimal.ZERO;
        for (Trade trade : mTrades) {
            BigDecimal single = mTick.subtract(trade.getPrice()).multiply(trade.getQuantity());
            trading = trading.add(single);
        }
        return trading;
    }

    private BigDecimal getRealizedPL() {
        BigDecimal total = BigDecimal.ZERO;
        Queue<PositionElement> longs = new LinkedList<PositionElement>();
        Queue<PositionElement> shorts = new LinkedList<PositionElement>();
        adjustIncomingPosition(longs, shorts);
        for (Trade trade : mTrades) {
            total = total.add(processTrade(longs, shorts, trade));
        }
        return total;
    }

    /**
     * Helper method that updates two arrays of long and short position elements based on a trade.
     */
    private BigDecimal processTrade(Queue<PositionElement> longs, Queue<PositionElement> shorts,
            Trade trade) {
        BigDecimal total = BigDecimal.ZERO;
        Queue<PositionElement> source, dest;
        BigDecimal remaining;
        BigDecimal quantity = trade.getQuantity();
        BigDecimal price = trade.getPrice();
        if (quantity.signum() == 1) {
            // buy
            remaining = quantity;
            source = shorts;
            dest = longs;
        } else {
            // sell
            remaining = quantity.negate();
            source = longs;
            dest = shorts;
        }
        while (remaining.signum() == 1 && !source.isEmpty()) {
            PositionElement element = source.peek();
            int compare = element.quantity.compareTo(remaining);
            BigDecimal priceDifference = price.subtract(element.price);
            if (source == shorts) {
                // negate the price difference for closing short positions
                // since realized gains happen when price has decreased
                priceDifference = priceDifference.negate();
            }
            if (compare == 0) {
                // position element is closed
                total = total.add(priceDifference.multiply(remaining));
                source.remove();
                remaining = BigDecimal.ZERO;
            } else if (compare > 0) {
                total = total.add(priceDifference.multiply(remaining));
                element.quantity = element.quantity.subtract(remaining);
                remaining = BigDecimal.ZERO;
            } else if (compare < 0) {
                total = total.add(priceDifference.multiply(element.quantity));
                source.remove();
                remaining = remaining.subtract(element.quantity);
            }
        }
        if (remaining.signum() == 1) {
            dest.add(new PositionElement(remaining, price));
        }
        return total;
    }

    private BigDecimal getUnrealizedPL() {
        if (mTick == null) {
            return null;
        }
        Queue<PositionElement> longs = new LinkedList<PositionElement>();
        Queue<PositionElement> shorts = new LinkedList<PositionElement>();

        adjustIncomingPosition(longs, shorts);
        for (Trade trade : mTrades) {
            processTrade(longs, shorts, trade);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PositionElement element : longs) {
            total = total.add(mTick.subtract(element.price).multiply(element.quantity));
        }
        for (PositionElement element : shorts) {
            total = total.subtract(mTick.subtract(element.price).multiply(element.quantity));
        }
        return total;
    }

    private void adjustIncomingPosition(Queue<PositionElement> longs, Queue<PositionElement> shorts) {
        if (mIncomingPosition.signum() == 1) {
            longs.add(new PositionElement(mIncomingPosition, mClosingPrice));
        } else if (mIncomingPosition.signum() == -1) {
            shorts.add(new PositionElement(mIncomingPosition.negate(), mClosingPrice));
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

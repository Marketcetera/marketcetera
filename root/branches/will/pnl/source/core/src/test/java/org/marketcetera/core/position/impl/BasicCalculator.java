package org.marketcetera.core.position.impl;

import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;

/* $License$ */

/**
 * Basic implementation of {@link PositionMetricsCalculator} that recomputes
 * each value from scratch every time.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class BasicCalculator implements PositionMetricsCalculator {

    private List<Trade> trades = new ArrayList<Trade>();
    private BigDecimal tick;

    public PositionMetrics tick(String current) {
        return tick(new BigDecimal(current));
    }

    @Override
    public PositionMetrics tick(BigDecimal current) {
        this.tick = current;
        return createPositionMetrics();
    }

    @Override
    public PositionMetrics trade(Trade trade) {
        trades.add(trade);
        return createPositionMetrics();
    }

    private PositionMetrics createPositionMetrics() {
        return new PositionMetricsImpl(getPosition(), getPositionPL(), getTradingPL(),
                getRealizedPL(), getUnrealizedPL(), getTotalPL());
    }

    private BigDecimal getPosition() {
        BigDecimal position = BigDecimal.ZERO;
        for (Trade trade : trades) {
            position = position.add(trade.getQuantity());
        }
        return position;
    }

    private BigDecimal getPositionPL() {
        // TODO: implement this using incoming position and closing price
        return BigDecimal.ZERO;
    }

    private BigDecimal getTradingPL() {
        if (tick == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal trading = BigDecimal.ZERO;
        for (Trade trade : trades) {
            BigDecimal single = tick.subtract(trade.getPrice()).multiply(trade.getQuantity());
            trading = trading.add(single);
        }
        return trading;
    }

    private BigDecimal getRealizedPL() {
        BigDecimal total = BigDecimal.ZERO;
        Queue<PositionElement> longs = new LinkedList<PositionElement>();
        Queue<PositionElement> shorts = new LinkedList<PositionElement>();
        for (Trade trade : trades) {
            total = total.add(processTrade(longs, shorts, trade));
        }
        return total;
    }

    /**
     * Helper method that updates two arrays of long and short position elements
     * based on a trade.
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
        if (tick == null) {
            return BigDecimal.ZERO;
        }
        Queue<PositionElement> longs = new LinkedList<PositionElement>();
        Queue<PositionElement> shorts = new LinkedList<PositionElement>();
        for (Trade trade : trades) {
            processTrade(longs, shorts, trade);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PositionElement element : longs) {
            total = total.add(tick.subtract(element.price).multiply(element.quantity));
        }
        for (PositionElement element : shorts) {
            total = total.subtract(tick.subtract(element.price).multiply(element.quantity));
        }
        return total;
    }

    private BigDecimal getTotalPL() {
        BigDecimal total = getPositionPL().add(getTradingPL());
        // the totals should be the same regardless of how you add it up
        assertThat(total, comparesEqualTo(getUnrealizedPL().add(getRealizedPL())));
        return total;
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

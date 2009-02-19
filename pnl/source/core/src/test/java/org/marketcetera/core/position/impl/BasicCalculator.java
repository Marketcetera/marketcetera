package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.marketcetera.core.position.PositionMetrics;

/* $License$ */

/**
 * Basic implmentation of {@link PositionMetricsCalculator} that recomputes each
 * value from scratch every time.
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
			switch (trade.getSide()) {
			case BUY:
				position = position.add(trade.getQuantity());
				break;
			case SELL:
				position = position.subtract(trade.getQuantity());
				break;
			}
		}
		return position;
	}

	private BigDecimal getPositionPL() {
		return BigDecimal.ZERO;
	}

	private BigDecimal getTradingPL() {
		if (tick == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal trading = BigDecimal.ZERO;
		for (Trade trade : trades) {
			BigDecimal single = tick.subtract(trade.getPrice()).multiply(trade.getQuantity());
			switch (trade.getSide()) {
			case BUY:
				trading = trading.add(single);
				break;
			case SELL:
				trading = trading.subtract(single);
				break;
			}
		}
		return trading;
	}

	private BigDecimal getRealizedPL() {
		BigDecimal total = BigDecimal.ZERO;
		Queue<BigDecimal[]> longs = new LinkedList<BigDecimal[]>();
		Queue<BigDecimal[]> shorts = new LinkedList<BigDecimal[]>();
		for (Trade trade : trades) {
			switch (trade.getSide()) {
			case BUY:
				total = total.add(processTrade(shorts, longs, trade, true));
				break;
			case SELL:
				total = total.add(processTrade(longs, shorts, trade, false));
				break;
			}
		}
		return total;
	}

	private BigDecimal processTrade(Queue<BigDecimal[]> source, Queue<BigDecimal[]> dest,
			Trade trade, boolean buy) {
		BigDecimal remaining = trade.getQuantity();
		BigDecimal price = trade.getPrice();
		BigDecimal total = BigDecimal.ZERO;
		while (remaining.compareTo(BigDecimal.ZERO) > 0 && !source.isEmpty()) {
			BigDecimal[] single = source.peek();
			int compare = single[0].compareTo(remaining);
			BigDecimal diff = price.subtract(single[1]);
			if (buy) diff = diff.negate();
			if (compare == 0) {
				total = total.add(diff.multiply(remaining));
				source.remove();
				return total;
			} else if (compare > 0) {
				total = total.add(diff.multiply(remaining));
				single[0] = single[0].subtract(remaining);
				return total;
			} else if (compare < 0) {
				total = total.add(diff.multiply(single[0]));
				remaining = remaining.subtract(single[0]);
				source.remove();
			}
		}
		if (remaining.compareTo(BigDecimal.ZERO) > 0) {
			dest.add(new BigDecimal[] { remaining, price });
		}
		return total;
	}

	private BigDecimal getUnrealizedPL() {
		if (tick == null) {
			return BigDecimal.ZERO;
		}
		Queue<BigDecimal[]> longs = new LinkedList<BigDecimal[]>();
		Queue<BigDecimal[]> shorts = new LinkedList<BigDecimal[]>();
		for (Trade trade : trades) {
			switch (trade.getSide()) {
			case BUY:
				processTrade(shorts, longs, trade, false);
				break;
			case SELL:
				processTrade(longs, shorts, trade, false);
				break;
			}
		}
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal[] single : longs) {
			total = total.add(tick.subtract(single[1]).multiply(single[0]));
		}
		for (BigDecimal[] single : shorts) {
			total = total.subtract(tick.subtract(single[1]).multiply(single[0]));
		}
		return total;
	}

	private BigDecimal getTotalPL() {
		BigDecimal total = getPositionPL().add(getTradingPL());
		System.out.println("Basic total discrepancy: " + total.subtract(getUnrealizedPL().add(getRealizedPL())));
		assert total.compareTo(getUnrealizedPL().add(getRealizedPL())) == 0;
		return total;
	}
}

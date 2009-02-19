package org.marketcetera.core.position.impl;

import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Random;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.impl.Trade.Side;

/* $License$ */

/**
 * Template for verifying that 2 {@link PositionMetricsCalculator}
 * implementations produce the same results with random data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PositionMetricsCalculatorTestTemplate implements Runnable {

	int numTests;

	public PositionMetricsCalculatorTestTemplate(int numTests) {
		this.numTests = numTests;
	}

	public void run() {
		PositionMetricsCalculator calculator = createCalculator();
		PositionMetricsCalculator basicCalculator = createBenchmarkCalculator();

		Random random = new Random(1);
		for (int i = 0; i < numTests; i++) {
			if (random.nextBoolean()) {
				BigDecimal tradePrice = randomBigDecimal(random);
				System.out.println(MessageFormat.format("Iteration {0}: Tick of ${1}", i,
						tradePrice));
				assertPositionMetrics(basicCalculator.tick(tradePrice),
						calculator.tick(tradePrice), i);
			} else {
				Trade trade = createTrade(random.nextBoolean(), randomBigDecimal(random),
						randomBigDecimal(random), i);
				System.out.println(MessageFormat.format(
						"Iteration {0}: Trade of {1} {2} ${3}", i, trade.getSide(), trade
								.getQuantity(), trade.getPrice()));
				assertPositionMetrics(basicCalculator.trade(trade), calculator.trade(trade), i);
			}
		}

	}

	protected PositionMetricsCalculator createBenchmarkCalculator() {
		return new BasicCalculator();
	}

	private BigDecimal randomBigDecimal(Random random) {
		return new BigDecimal(random.nextInt(10000)).divide(new BigDecimal("100"));
	}

	private Trade createTrade(boolean buy, BigDecimal quantity, BigDecimal price, int counter) {
		return new TradeImpl("ABC", "asdf", "Yoram", buy ? Side.BUY : Side.SELL, price, quantity,
				counter);
	}

	private void assertPositionMetrics(PositionMetrics expected, PositionMetrics actual, int i) {
		assertBigDecimal(expected.getPosition(), actual.getPosition(), i);
		assertBigDecimal(expected.getPositionPL(), actual.getPositionPL(), i);
		assertBigDecimal(expected.getTradingPL(), actual.getTradingPL(), i);
		assertBigDecimal(expected.getRealizedPL(), actual.getRealizedPL(), i);
		assertBigDecimal(expected.getUnrealizedPL(), actual.getUnrealizedPL(), i);
		assertBigDecimal(expected.getTotalPL(), actual.getTotalPL(), i);
	}

	private void assertBigDecimal(BigDecimal expected, BigDecimal actual, int i) {
		assertThat("Iteration " + i, actual, comparesEqualTo(expected));
	}

	protected abstract PositionMetricsCalculator createCalculator();

}

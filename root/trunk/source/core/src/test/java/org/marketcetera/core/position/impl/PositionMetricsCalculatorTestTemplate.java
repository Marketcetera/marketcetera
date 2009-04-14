package org.marketcetera.core.position.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Random;

import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Template for verifying that 2 {@link PositionMetricsCalculator} implementations produce the same
 * results with random data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public abstract class PositionMetricsCalculatorTestTemplate implements Runnable {

    int numTests;

    public PositionMetricsCalculatorTestTemplate(int numTests) {
        this.numTests = numTests;
    }

    public void run() {
        Random random = new Random(1);

        BigDecimal incomingPosition = randomBigDecimal(random);
        BigDecimal closingPrice = randomBigDecimal(random);
        SLF4JLoggerProxy.debug(this, MessageFormat.format(
                "Incoming position is {0} and closing price is {1}.", incomingPosition,
                closingPrice));
        PositionMetricsCalculator calculator = createCalculator(incomingPosition, closingPrice);
        PositionMetricsCalculator basicCalculator = createBenchmarkCalculator(incomingPosition,
                closingPrice);

        for (int i = 0; i < numTests; i++) {
            if (random.nextBoolean()) {
                BigDecimal tradePrice = randomBigDecimal(random);
                SLF4JLoggerProxy.debug(this, MessageFormat.format("Iteration {0}: Tick of ${1}", i,
                        tradePrice));
                assertPositionMetrics(basicCalculator.tick(tradePrice),
                        calculator.tick(tradePrice), i);
            } else {
                Trade trade = createTrade(random.nextBoolean(), randomBigDecimal(random),
                        randomBigDecimal(random), i);
                SLF4JLoggerProxy.debug(this, MessageFormat.format(
                        "Iteration {0}: Trade of {1} ${2}", i, trade.getQuantity(), trade
                                .getPrice()));
                assertPositionMetrics(basicCalculator.trade(trade), calculator.trade(trade), i);
            }
        }

    }

    protected PositionMetricsCalculator createBenchmarkCalculator(BigDecimal incomingPosition,
            BigDecimal closingPrice) {
        return new BasicCalculator(incomingPosition, closingPrice);
    }

    private BigDecimal randomBigDecimal(Random random) {
        return new BigDecimal(random.nextInt(10000)).divide(new BigDecimal("100"));
    }

    private Trade createTrade(boolean buy, BigDecimal quantity, BigDecimal price, int counter) {
        return new MockTrade("ABC", "asdf", "Yoram", price, buy ? quantity : quantity.negate(),
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
        if (expected == null) {
            assertNull("Iteration " + i, actual);
        } else {
            assertThat("Iteration " + i, actual, comparesEqualTo(expected));
        }
    }

    protected abstract PositionMetricsCalculator createCalculator(BigDecimal incomingPosition,
            BigDecimal closingPrice);

}

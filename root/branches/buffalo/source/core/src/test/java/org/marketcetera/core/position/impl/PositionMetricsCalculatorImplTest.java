package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;

/* $License$ */

/**
 * Test {@link PositionMetricsCalculatorImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionMetricsCalculatorImplTest {

    private static final int NUM_ITERATIONS = 1000;

    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
    }

    @Test
    public void stressTest() {
        new PositionMetricsCalculatorTestTemplate(NUM_ITERATIONS) {

            @Override
            protected PositionMetricsCalculator createCalculator(BigDecimal incomingPosition, BigDecimal closingPrice) {
                return new PositionMetricsCalculatorImpl(incomingPosition, closingPrice);
            }
        }.run();
    }
}

package org.marketcetera.core.position.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;

/* $License$ */

/**
 * Test {@link PositionMetricsCalculatorImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
            protected PositionMetricsCalculator createCalculator() {
                return new PositionMetricsCalculatorImpl(null);
            }
        }.run();
    }
}

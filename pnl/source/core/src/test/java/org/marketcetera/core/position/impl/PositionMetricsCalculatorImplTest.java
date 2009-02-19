package org.marketcetera.core.position.impl;

import org.junit.Test;

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

	@Test
	public void stressTest() {
		new PositionMetricsCalculatorTestTemplate(NUM_ITERATIONS) {

			@Override
			protected PositionMetricsCalculator createCalculator() {
				return new PositionMetricsCalculatorImpl();
			}
		}.run();
	}
}

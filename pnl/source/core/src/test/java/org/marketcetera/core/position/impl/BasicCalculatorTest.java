package org.marketcetera.core.position.impl;

import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;

/* $License$ */

/**
 * Tests {@link BasicCalculator}.  Other {@link PositionMetricsCalculator} implementations are 
 * tested by comparing to BasicCalculator.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class BasicCalculatorTest {

	private int counter = 0;

	@Test
	public void test() {
		BasicCalculator pnl = new BasicCalculator();
		assertPNL(pnl.trade(createTrade("100", "21")), "100", "0", "0", "0", "0");
		assertPNL(pnl.tick("21"), "100", "0", "0", "0", "0");
		assertPNL(pnl.trade(createTrade("-50", "21.08")), "50", "0", "4", "4", "0");
		assertPNL(pnl.tick("21.08"), "50", "0", "8", "4", "4");
		assertPNL(pnl.trade(createTrade("100", "21.12")), "150", "0", "4", "4", "0");
		assertPNL(pnl.tick("21.12"), "150", "0", "10", "4", "6");
		assertPNL(pnl.trade(createTrade("-100", "21.20")), "50", "0", "18", "18", "0");
		assertPNL(pnl.tick("21.20"), "50", "0", "22", "18", "4");
	}
	
	@Test
	public void shortTest() {
		BasicCalculator pnl = new BasicCalculator();
		assertPNL(pnl.trade(createTrade("-100", "21")), "-100", "0", "0", "0", "0");
		assertPNL(pnl.tick("22"), "-100", "0", "-100", "0", "-100");
	}
	
	@Test
	public void shortTest2() {
		BasicCalculator pnl = new BasicCalculator();
		assertPNL(pnl.tick("22"), "0", "0", "0", "0", "0");
		assertPNL(pnl.trade(createTrade("-100", "21")), "-100", "0", "-100", "0", "-100");
	}
	
	@Test
	public void shortTest3() {
		BasicCalculator pnl = new BasicCalculator();
		assertPNL(pnl.tick("22"), "0", "0", "0", "0", "0");
		assertPNL(pnl.trade(createTrade("-100", "21")), "-100", "0", "-100", "0", "-100");
		assertPNL(pnl.trade(createTrade("100", "20")), "0", "0", "100", "100", "0");
	}

	private Trade createTrade(String quantity, String price) {
		return new MockTrade("ABC", "asdf", "Yoram", 
				new BigDecimal(price), new BigDecimal(quantity), ++counter );
	}

	private void assertPNL(PositionMetrics pnl, String position, String positional, String trading,
			String realized, String unrealized) {
		assertBigDecimal(position, pnl.getPosition());
		assertBigDecimal(positional, pnl.getPositionPL());
		assertBigDecimal(trading, pnl.getTradingPL());
		assertBigDecimal(realized, pnl.getRealizedPL());
		assertBigDecimal(unrealized, pnl.getUnrealizedPL());
	}

	private void assertBigDecimal(String expected, BigDecimal actual) {
		assertThat(actual, comparesEqualTo(new BigDecimal(expected)));
	}
}

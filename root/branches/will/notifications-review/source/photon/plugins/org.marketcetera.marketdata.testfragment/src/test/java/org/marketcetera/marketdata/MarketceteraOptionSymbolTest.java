package org.marketcetera.marketdata;

import org.marketcetera.core.ExpectedTestFailure;

import junit.framework.TestCase;

public class MarketceteraOptionSymbolTest extends TestCase {

	public void testGetExpirationCode() {
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol("IBM+AZ");
		assertEquals('A', symbol.getExpirationCode());
	}

	public void testGetStrikeCode() {
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol("IBM+AZ");
		assertEquals('Z', symbol.getStrikeCode());
	}

	public void testMatchesPattern() {
		assertTrue(MarketceteraOptionSymbol.matchesPattern("IBM+AZ"));
		assertTrue(MarketceteraOptionSymbol.matchesPattern("IB+AZ"));
		assertTrue(MarketceteraOptionSymbol.matchesPattern("I+AZ"));
		assertFalse(MarketceteraOptionSymbol.matchesPattern("+AZ"));
		assertFalse(MarketceteraOptionSymbol.matchesPattern("IBMI+AZ"));
		assertFalse(MarketceteraOptionSymbol.matchesPattern("IBM+Z"));
		assertFalse(MarketceteraOptionSymbol.matchesPattern("IBM+AZZ"));
		assertFalse(MarketceteraOptionSymbol.matchesPattern(""));
		assertFalse(MarketceteraOptionSymbol.matchesPattern("IBMAZ"));
	}

	public void testToString() {
		String symbolString = "IBM+AZ";
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol(symbolString);
		assertEquals(symbolString, symbol.toString());
	}

	public void testMarketceteraOptionSymbol() throws Exception {
		String symbolString = "IBM+AZ";
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol(symbolString);
		assertEquals(symbolString, symbol.toString());
		
		new ExpectedTestFailure(IllegalArgumentException.class){
			@Override
			protected void execute() throws Throwable {
				new MarketceteraOptionSymbol("IBM+R");
			}
		}.run();
	}
}

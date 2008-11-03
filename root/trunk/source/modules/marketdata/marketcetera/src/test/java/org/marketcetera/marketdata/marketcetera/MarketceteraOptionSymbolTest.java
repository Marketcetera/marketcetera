package org.marketcetera.marketdata.marketcetera;

import org.junit.Test;
import static org.junit.Assert.*;

import org.marketcetera.core.ExpectedTestFailure;

public class MarketceteraOptionSymbolTest
{
    @Test
	public void testGetExpirationCode() {
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol("IBM+AZ");
		assertEquals('A', symbol.getExpirationCode());
	}

    @Test
	public void testGetStrikeCode() {
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol("IBM+AZ");
		assertEquals('Z', symbol.getStrikeCode());
	}

    @Test
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

    @Test
	public void testToString() {
		String symbolString = "IBM+AZ";
		MarketceteraOptionSymbol symbol = new MarketceteraOptionSymbol(symbolString);
		assertEquals(symbolString, symbol.toString());
	}

    @Test
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

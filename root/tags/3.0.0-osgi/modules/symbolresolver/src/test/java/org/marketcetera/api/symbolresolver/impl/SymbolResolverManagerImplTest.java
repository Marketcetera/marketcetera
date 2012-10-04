package org.marketcetera.api.symbolresolver.impl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.marketcetera.api.symbolresolver.Messages;
import org.marketcetera.api.symbolresolver.NoInstrumentForSymbol;
import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.core.trade.Instrument;
import org.mockito.Mock;

/* $License$ */

/**
 * Test the symbol resolver manager functionality.
 *
 * @version $Id$
 * @since $Release$
 */
public class SymbolResolverManagerImplTest {
	
	@Mock SymbolResolver mockSymbolResolver1 = mock(SymbolResolver.class);
	@Mock SymbolResolver mockSymbolResolver2 = mock(SymbolResolver.class);
	private SymbolResolverManagerImpl symbolResolverManager = new SymbolResolverManagerImpl();
	private static final String DEFAULT_SYMBOL="TEST"; 
	private Instrument mockInstrument = mock(Instrument.class);

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();	
	
	@Before
	public void setUp() {
		List<SymbolResolver> symbolResolverList = new ArrayList<SymbolResolver>();
		symbolResolverList.add(mockSymbolResolver1);
		symbolResolverList.add(mockSymbolResolver2);
		symbolResolverManager.setSymbolResolvers(symbolResolverList);
	}
	
	@Test
	public void testNoMatchingSymbolResolver() {
		expectedEx.expect(NoInstrumentForSymbol.class);
		expectedEx.expectMessage(Messages.UNABLE_TO_RESOLVE_SYMBOL.getMessageId());		
		when(mockSymbolResolver1.resolve(DEFAULT_SYMBOL, null)).thenReturn(null);
		when(mockSymbolResolver2.resolve(DEFAULT_SYMBOL, null)).thenReturn(null);
		symbolResolverManager.resolve(DEFAULT_SYMBOL);
		verify(mockSymbolResolver1, times(1)).resolve(DEFAULT_SYMBOL, null);
		verify(mockSymbolResolver2, times(1)).resolve(DEFAULT_SYMBOL, null);
	}

	@Test
	public void testFirstMatchingSymbolResolver() {
		when(mockSymbolResolver1.resolve(DEFAULT_SYMBOL, null)).thenReturn(mockInstrument);
		Instrument instrument = symbolResolverManager.resolve(DEFAULT_SYMBOL);
		verify(mockSymbolResolver1, times(1)).resolve(DEFAULT_SYMBOL, null);
		assertEquals("Didn't receive the expected instrument", instrument, mockInstrument);
	}

	@Test
	public void testSecondMatchingSymbolResolver() {
		when(mockSymbolResolver1.resolve(DEFAULT_SYMBOL, null)).thenReturn(null);
		when(mockSymbolResolver2.resolve(DEFAULT_SYMBOL, null)).thenReturn(mockInstrument);
		Instrument instrument = symbolResolverManager.resolve(DEFAULT_SYMBOL);
		verify(mockSymbolResolver1, times(1)).resolve(DEFAULT_SYMBOL, null);
		verify(mockSymbolResolver2, times(1)).resolve(DEFAULT_SYMBOL, null);
		assertEquals("Didn't receive the expected instrument", instrument, mockInstrument);
	}

}

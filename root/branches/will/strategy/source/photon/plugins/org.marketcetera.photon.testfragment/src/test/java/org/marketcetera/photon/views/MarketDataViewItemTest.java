package org.marketcetera.photon.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.photon.test.IsExpectedPropertyChangeEvent;

/* $License$ */

/**
 * Test {@link MarketDataViewItem}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataViewItemTest {

	private static final MSymbol symbol1 = new MSymbol("MSFT"); //$NON-NLS-1$
	private static final MSymbol symbol2 = new MSymbol("GOOG"); //$NON-NLS-1$

	private MarketDataViewItem fixture;
	private PropertyChangeListener listener;

	@Before
	public void setUp() {
		fixture = new MarketDataViewItem(symbol1);
		assertEquals(symbol1, fixture.getSymbol());
		listener = mock(PropertyChangeListener.class);
		fixture.addPropertyChangeListener("symbol", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("bidPx", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("bidSize", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("offerPx", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("offerSize", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("lastPx", listener); //$NON-NLS-1$
		fixture.addPropertyChangeListener("lastQty", listener); //$NON-NLS-1$
	}

	@Test(expected = AssertionFailedException.class)
	public void testConstructorNegative() {
		new MarketDataViewItem(null);
	}

	@Test
	public void testConstructor() {
		assertEquals(symbol1, fixture.getSymbol());
	}

	@Test(expected = AssertionFailedException.class)
	public void testSetSymbolNegative() {
		assertEquals(symbol1, fixture.getSymbol());
		fixture.setSymbol(null);
	}

	@Test
	public void testSetSymbol() {
		fixture.setSymbol(symbol2);
		assertEquals(symbol2, fixture.getSymbol());
		assertNull(fixture.getLastPx());
		assertNull(fixture.getLastQty());
		assertNull(fixture.getBidPx());
		assertNull(fixture.getBidSize());
		assertNull(fixture.getOfferPx());
		assertNull(fixture.getOfferSize());
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("symbol", symbol1, //$NON-NLS-1$
						symbol2)));
	}

	@Test
	public void testSetBidEvent() {
		BidEvent bidEvent = new BidEvent(1L, 1L, "MSFT", "ABC", new BigDecimal(10), new BigDecimal(15)); //$NON-NLS-1$ //$NON-NLS-2$
		fixture.setBidEvent(bidEvent);
		assertEquals(new BigDecimal(10), fixture.getBidPx());
		assertEquals(new BigDecimal(15), fixture.getBidSize());
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("bidPx", null, //$NON-NLS-1$
						new BigDecimal(10))));
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("bidSize", null, //$NON-NLS-1$
						new BigDecimal(15))));
		// set it again and make sure no events are fired
		fixture.setBidEvent(bidEvent);
		verifyNoMoreInteractions(listener);
		
		// change symbol, and make sure no events are fired
		bidEvent = new BidEvent(1L, 1L, "GOOG", "ABC", new BigDecimal(20), new BigDecimal(25)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(new BigDecimal(10), fixture.getBidPx());
		assertEquals(new BigDecimal(15), fixture.getBidSize());
		verifyNoMoreInteractions(listener);
	}

	@Test
	public void testSetAskEvent() {
		AskEvent askEvent = new AskEvent(1L, 1L, "MSFT", "ABC", new BigDecimal(10), new BigDecimal(15)); //$NON-NLS-1$ //$NON-NLS-2$
		fixture.setAskEvent(askEvent);
		assertEquals(new BigDecimal(10), fixture.getOfferPx());
		assertEquals(new BigDecimal(15), fixture.getOfferSize());
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("offerPx", null, //$NON-NLS-1$
						new BigDecimal(10))));
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("offerSize", null, //$NON-NLS-1$
						new BigDecimal(15))));
		// set it again and make sure no events are fired
		fixture.setAskEvent(askEvent);
		verifyNoMoreInteractions(listener);
		
		// change symbol, and make sure no events are fired
		askEvent = new AskEvent(1L, 1L, "GOOG", "ABC", new BigDecimal(20), new BigDecimal(25)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(new BigDecimal(10), fixture.getOfferPx());
		assertEquals(new BigDecimal(15), fixture.getOfferSize());
		verifyNoMoreInteractions(listener);
	}

	@Test
	public void testSetTradeEvent() {
		TradeEvent tradeEvent = new TradeEvent(1L, 1L, "MSFT", "ABC", new BigDecimal(10), new BigDecimal(15)); //$NON-NLS-1$ //$NON-NLS-2$
		fixture.setTradeEvent(tradeEvent);
		assertEquals(new BigDecimal(10), fixture.getLastPx());
		assertEquals(new BigDecimal(15), fixture.getLastQty());
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("lastPx", null, //$NON-NLS-1$
						new BigDecimal(10))));
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("lastQty", null, //$NON-NLS-1$
						new BigDecimal(15))));
		// set it again and make sure no events are fired
		fixture.setTradeEvent(tradeEvent);
		verifyNoMoreInteractions(listener);
		
		// change symbol, and make sure no events are fired
		tradeEvent = new TradeEvent(1L, 1L, "GOOG", "ABC", new BigDecimal(20), new BigDecimal(25)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(new BigDecimal(10), fixture.getLastPx());
		assertEquals(new BigDecimal(15), fixture.getLastQty());
		verifyNoMoreInteractions(listener);
	}

}

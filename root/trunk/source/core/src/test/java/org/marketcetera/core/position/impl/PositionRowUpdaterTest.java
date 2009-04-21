package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeEvent;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeListener;
import org.marketcetera.module.ExpectedFailure;

import ca.odell.glazedlists.BasicEventList;

/* $License$ */

/**
 * Test {@link PositionRowUpdater}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionRowUpdaterTest {

	private static final String SYMBOL = "METC";
	private static final String ACCOUNT = "A1";
	private static final String TRADER = "1";
	private SymbolChangeListener mListener;
	private PositionRowUpdater mFixture;
	private PositionRowImpl mRow;
	private BasicEventList<Trade> mTrades;

	@Before
	public void before() {
		mTrades = new BasicEventList<Trade>();
		mRow = new PositionRowImpl(SYMBOL, ACCOUNT, TRADER, new BigDecimal(100));
		mFixture = new PositionRowUpdater(mRow, mTrades, new MockMarketData());
	}

	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new PositionRowUpdater(null, mTrades, new MockMarketData());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new PositionRowUpdater(mRow, null, new MockMarketData());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new PositionRowUpdater(mRow, mTrades, null);
			}
		};
	}

	@Test
	public void testGetPosition() {
		assertPosition(mFixture.getPosition(), "100", null, null, null, null, null);
	}

	@Test
	public void testClosingPrice() throws Exception {
		mTrades.add(createTrade("10", "1"));
		// only position is updated
		assertPosition(mFixture.getPosition(), "110", null, null, null, null, null);
		// set closing price
		setClosePrice("1.50");
		// now realizedPL is valid
		assertPosition(mFixture.getPosition(), "110", null, null, "0", null, null);
		// after a tick, everything is good to go
		tick("2");
		assertPosition(mFixture.getPosition(), "110", "50", "10", "0", "60", "60");
		// set closing price to null
		setClosePrice(null);
		// nothing valid anymore
		assertPosition(mFixture.getPosition(), "110", null, null, null, null, null);
	}

	private void tick(String price) {
		mListener.symbolTraded(new SymbolChangeEvent(this, new BigDecimal(price)));
	}

	private void setClosePrice(String closePrice) {
		mListener.closePriceChanged(new SymbolChangeEvent(this, closePrice == null ? null
				: new BigDecimal(closePrice)));
	}

	@Test
	public void testTradesOutOfOrder() throws Exception {
		setClosePrice("3");
		tick("5");
		mTrades.add(createTrade("-100", "5"));
		// closes incoming position for 5
		assertPosition(mFixture.getPosition(), "0", "200", "0", "200", "0", "200");
		mTrades.add(0, createTrade("-100", "3"));
		// recalculate with incoming position closed for 3
		assertPosition(mFixture.getPosition(), "-100", "200", "-200", "0", "0", "0");
	}

	private Trade createTrade(String quantity, String price) {
		return new MockTrade(SYMBOL, ACCOUNT, TRADER, new BigDecimal(price), new BigDecimal(
				quantity), 1L);
	}

	private void assertPosition(PositionRow row, String position, String positional,
			String trading, String realized, String unrealized, String total) {
		assertThat(row.getSymbol(), is(SYMBOL));
		assertThat(row.getAccount(), is(ACCOUNT));
		assertThat(row.getTraderId(), is(TRADER));
		PositionMetricsImplTest.assertPositionMetrics(row.getPositionMetrics(), "100", position,
				positional, trading, realized, unrealized, total);
	}

	class MockMarketData implements MarketDataSupport {

		@Override
		public void addSymbolChangeListener(String symbol, SymbolChangeListener listener) {
			assertThat(symbol, is(SYMBOL));
			mListener = listener;
		}

		@Override
		public BigDecimal getClosingPrice(String symbol) {
			return null;
		}

		@Override
		public BigDecimal getLastTradePrice(String symbol) {
			return null;
		}

		@Override
		public void removeSymbolChangeListener(String symbol, SymbolChangeListener listener) {
			if (listener == mListener) {
				mListener = null;
			}
		}

		@Override
		public void dispose() {
		}

	}

}

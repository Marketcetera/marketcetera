package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.MockTrade;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.Trade;
import org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent;
import org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;

import ca.odell.glazedlists.BasicEventList;

/* $License$ */

/**
 * Test {@link PositionRowUpdater}.
 * 
 */
public class CurrencyPositionRowUpdaterTest {

    private static final Instrument CURRENCY = new Currency("USD","INR","","");
    private static final String ACCOUNT = "A1";
    private static final String TRADER = "1";
    private InstrumentMarketDataListener mListener;
    private PositionRowUpdater mFixture;
    private PositionRowImpl mRow;
    private BasicEventList<Trade<?>> mTrades;

    @Before
    public void before() {
        mTrades = new BasicEventList<Trade<?>>();
        mRow = new PositionRowImpl(CURRENCY, "METC", ACCOUNT, TRADER, new BigDecimal(100));
        mFixture = new PositionRowUpdater(mRow, mTrades, new MockMarketData(
                CURRENCY));
    }

    @Test
    public void testNulls() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new PositionRowUpdater(null, mTrades,
                        new MockMarketData(CURRENCY));
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new PositionRowUpdater(mRow, mTrades, null);
            }
        };
    }

    @Test
    public void testGetPosition() {
        assertPosition(mFixture.getPosition(), CURRENCY, "100", null, null, null,
                null, null);
    }

    @Test
    public void testClosingPrice() throws Exception {
        mTrades.add(createTrade("10", "1"));
        // only position is updated
        assertPosition(mFixture.getPosition(), CURRENCY, "110", null, null, null,
                null, null);
        // set closing price
        setClosePrice("1.50");
        Thread.sleep(500);
        // now realizedPL is valid
        assertPosition(mFixture.getPosition(), CURRENCY, "110", null, null, "0",
                null, null);
        // after a tick, everything is good to go
        tick("2");
        Thread.sleep(500);
        assertPosition(mFixture.getPosition(), CURRENCY, "110", "50", "10", "0",
                "60", "60");
        // set closing price to null
        setClosePrice(null);
        // nothing valid anymore
        Thread.sleep(500);
        assertPosition(mFixture.getPosition(), CURRENCY, "110", null, null, null,
                null, null);
    }

    private void tick(String price) {
        mListener.symbolTraded(new InstrumentMarketDataEvent(this,
                new BigDecimal(price)));
    }

    private void setClosePrice(String closePrice) {
        mListener.closePriceChanged(new InstrumentMarketDataEvent(this,
                closePrice == null ? null : new BigDecimal(closePrice)));
    }

    @Test
    public void testTradesOutOfOrder() throws Exception {
        setClosePrice("3");
        tick("5");
        Thread.sleep(500);
        mTrades.add(createTrade("-100", "5"));
        // closes incoming position for 5
        assertPosition(mFixture.getPosition(), CURRENCY, "0", "200", "0", "200",
                "0", "200");
        mTrades.add(0, createTrade("-100", "3"));
        // recalculate with incoming position closed for 3
        assertPosition(mFixture.getPosition(), CURRENCY, "-100", "200", "-200",
                "0", "0", "0");
    }

    @Test
    public void testDelayedConnect() throws Exception {
        // new fixture and row for this test since the main one initializes with
        // trades
        mFixture = new PositionRowUpdater(new PositionRowImpl(CURRENCY, "METC",
                ACCOUNT, TRADER, new BigDecimal(100)), null, new MockMarketData(CURRENCY));
        setClosePrice("1.50");
        tick("2");
        Thread.sleep(1000);
        assertPosition(mFixture.getPosition(), CURRENCY, "100", "50", "0", "0",
                "50", "50");
        mTrades.add(createTrade("-100", "2"));
        assertPosition(mFixture.getPosition(), CURRENCY, "100", "50", "0", "0",
                "50", "50");
        mFixture.connect(mTrades);
        assertPosition(mFixture.getPosition(), CURRENCY, "0", "50", "0", "50",
                "0", "50");
    }


    private Trade<?> createTrade(String quantity, String price) {
        return MockTrade.createCurrencyTrade("USD","INR", ACCOUNT, TRADER, quantity,
                price);
    }


    private void assertPosition(PositionRow row, Instrument instrument,
            String position, String positional, String trading,
            String realized, String unrealized, String total) {
        assertThat(row.getInstrument(), is(instrument));
        assertThat(row.getAccount(), is(ACCOUNT));
        assertThat(row.getTraderId(), is(TRADER));
        PositionMetricsImplTest.assertPositionMetrics(row.getPositionMetrics(),
                "100", position, positional, trading, realized, unrealized,
                total);
    }

    class MockMarketData implements MarketDataSupport {

        private final Instrument mInstrument;

        public MockMarketData(Instrument instrument) {
            mInstrument = instrument;
        }

        @Override
        public void addInstrumentMarketDataListener(Instrument instrument,
                InstrumentMarketDataListener listener) {
            assertThat(instrument, is(mInstrument));
            mListener = listener;
        }

        @Override
        public BigDecimal getClosingPrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getLastTradePrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getOptionMultiplier(Option option) {
            return null;
        }
        
		@Override
		public BigDecimal getFutureMultiplier(Future future) {
			return null;
		}

        @Override
        public void removeInstrumentMarketDataListener(Instrument instrument,
                InstrumentMarketDataListener listener) {
            if (listener == mListener) {
                mListener = null;
            }
        }

        @Override
        public void dispose() {
        }
    }
}
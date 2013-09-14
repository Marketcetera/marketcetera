package org.marketcetera.photon;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent;
import org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDFactory;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link PhotonPositionMarketData}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PhotonPositionMarketDataTest {
    private static final Equity IBM = new Equity("IBM");
    private static final Option METC = new Option("METC", "201210", BigDecimal.TEN, OptionType.Call);
    private static final Future METC_FUT = new Future("METC", FutureExpirationMonth.DECEMBER,2014);
	private PhotonPositionMarketData mFixture;
	private IMarketData mMockMarketData;
	private MDLatestTick mIBMTick;
	private IMarketDataReference<MDLatestTick> mMockIBMReference;
	private MDLatestTick mMETCTick;
	private IMarketDataReference<MDLatestTick> mMockMETCReference;
	private MDLatestTick mMETC_FUTTick;
	private IMarketDataReference<MDLatestTick> mMockMETC_FUTReference;
	private MDMarketstat mIBMStat;
	private MDMarketstat mMETCStat;
	private MDMarketstat mMETCFUTStat;
	private IMarketDataReference<MDMarketstat> mMockIBMStatReference;
	private IMarketDataReference<MDMarketstat> mMockMETCStatReference;
	private IMarketDataReference<MDMarketstat> mMockMETCFUTStatReference;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		mMockMarketData = mock(IMarketData.class);
		mFixture = new PhotonPositionMarketData(mMockMarketData);
		// latest tick stubbing
		mIBMTick = MDFactory.eINSTANCE.createMDLatestTick();
		mIBMTick.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, IBM);
		mMETCTick = MDFactory.eINSTANCE.createMDLatestTick();
		mMETCTick.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, METC);
		mMETC_FUTTick = MDFactory.eINSTANCE.createMDLatestTick();
		mMETC_FUTTick.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, METC_FUT);
		mMockIBMReference = mock(IMarketDataReference.class);
		mMockMETCReference = mock(IMarketDataReference.class);
		mMockMETC_FUTReference = mock(IMarketDataReference.class);
		when(mMockIBMReference.get()).thenReturn(mIBMTick);
		when(mMockMETCReference.get()).thenReturn(mMETCTick);
		when(mMockMETC_FUTReference.get()).thenReturn(mMETC_FUTTick);
		when(mMockMarketData.getLatestTick(IBM)).thenReturn(mMockIBMReference);
		when(mMockMarketData.getLatestTick(METC)).thenReturn(mMockMETCReference);
		when(mMockMarketData.getLatestTick(METC_FUT)).thenReturn(mMockMETC_FUTReference);
		// statistic stubbing
		mIBMStat = MDFactory.eINSTANCE.createMDMarketstat();
		mIBMStat.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, IBM);
		mMETCStat = MDFactory.eINSTANCE.createMDMarketstat();
		mMETCStat.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, METC);
		mMETCFUTStat = MDFactory.eINSTANCE.createMDMarketstat();
		mMETCFUTStat.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, METC_FUT);
		mMockIBMStatReference = mock(IMarketDataReference.class);
		mMockMETCStatReference = mock(IMarketDataReference.class);
		mMockMETCFUTStatReference = mock(IMarketDataReference.class);
		when(mMockIBMStatReference.get()).thenReturn(mIBMStat);
		when(mMockMETCStatReference.get()).thenReturn(mMETCStat);
		when(mMockMETCFUTStatReference.get()).thenReturn(mMETCFUTStat);
		when(mMockMarketData.getMarketstat(IBM)).thenReturn(mMockIBMStatReference);
		when(mMockMarketData.getMarketstat(METC)).thenReturn(mMockMETCStatReference);
		when(mMockMarketData.getMarketstat(METC_FUT)).thenReturn(mMockMETCFUTStatReference);
	}

	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				new PhotonPositionMarketData(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.getLastTradePrice(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.getClosingPrice(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.addInstrumentMarketDataListener(null, mock(InstrumentMarketDataListener.class));
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.addInstrumentMarketDataListener(IBM, null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.removeInstrumentMarketDataListener(null, mock(InstrumentMarketDataListener.class));
			}
		};
		new ExpectedFailure<IllegalArgumentException>() {
			@Override
			protected void run() throws Exception {
				mFixture.removeInstrumentMarketDataListener(IBM, null);
			}
		};
	}

	@Test
	public void testAddingMultipleListenersGetsSingleReference() {
		mFixture.addInstrumentMarketDataListener(IBM, mock(InstrumentMarketDataListener.class));
		mFixture.addInstrumentMarketDataListener(IBM, mock(InstrumentMarketDataListener.class));
		verify(mMockMarketData, times(1)).getLatestTick(new Equity("IBM"));
	}

	@Test
	public void testNotificationGeneratesEvent() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(METC, mockListener);
		changeLatestTick(mMETCTick, 5);
		verify(mockListener).symbolTraded(argThat(hasNewAmount(5)));
		changeMultiplier(mMETCTick, 8);
		verify(mockListener).optionMultiplierChanged(argThat(hasNewAmount(8)));
		
	}
	
	@Test
	public void testFutureNotificationGeneratesEvent() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(METC_FUT, mockListener);
		changeLatestTick(mMETC_FUTTick, 5);
		verify(mockListener).symbolTraded(argThat(hasNewAmount(5)));
		changeMultiplier(mMETC_FUTTick, 10);
		verify(mockListener).futureMultiplierChanged(argThat(hasNewAmount(10)));
		
	}

	@Test
	public void testMultipleListenersGetNotified() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener2);
		changeLatestTick(mIBMTick, 12);
		verify(mockListener).symbolTraded(argThat(hasNewAmount(12)));
		verify(mockListener2).symbolTraded(argThat(hasNewAmount(12)));
	}

	@Test
	public void testDuplicateListenerIgnored() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		changeLatestTick(mIBMTick, 7);
		verify(mockListener, times(1)).symbolTraded(argThat(hasNewAmount(7)));
	}

	@Test
	public void testRemoveNonExistantListenerIgnored() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.removeInstrumentMarketDataListener(IBM, mockListener2);
		mFixture.removeInstrumentMarketDataListener(METC, mockListener);
		verify(mMockIBMReference, never()).dispose();
	}

	@Test
	public void testNoMoreListenersDisposesDataAndRemovesAdapter() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener2);
		mFixture.removeInstrumentMarketDataListener(IBM, mockListener);
		verify(mMockIBMReference, never()).dispose();
		mFixture.removeInstrumentMarketDataListener(IBM, mockListener2);
		verify(mMockIBMReference).dispose();
		assertThat(mIBMTick.eAdapters().size(), is(0));
	}

	@Test
	public void testGetLastTradePrice() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(METC, mockListener2);
		changeLatestTick(mIBMTick, 18);
		changeLatestTick(mMETCTick, 19);
		assertThat(mFixture.getLastTradePrice(IBM), comparesEqualTo(18));
		assertThat(mFixture.getLastTradePrice(METC), comparesEqualTo(19));
	}

	@Test
	public void testGetClosingPrice() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(METC, mockListener2);
		changePreviousClose(mIBMStat, 20);
		changePreviousClose(mMETCStat, 25);
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewAmount(20)));
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNewAmount(25)));
		assertThat(mFixture.getClosingPrice(IBM), comparesEqualTo(20));
		assertThat(mFixture.getClosingPrice(METC), comparesEqualTo(25));
		changePreviousClose(mIBMStat, null);
		changePreviousClose(mMETCStat, null);
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNullNewAmount()));
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNullNewAmount()));
		assertThat(mFixture.getClosingPrice(IBM), nullValue());
		assertThat(mFixture.getClosingPrice(METC), nullValue());
	}

    @Test
    public void testGetOptionMultiplier() throws Exception {
        InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
        mFixture.addInstrumentMarketDataListener(METC, mockListener);
        changeMultiplier(mMETCTick, 1000);
        assertThat(mFixture.getOptionMultiplier(METC), comparesEqualTo(1000));
    }
    
    @Test
    public void testGetFutureMultiplier() throws Exception {
        InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
        mFixture.addInstrumentMarketDataListener(METC_FUT, mockListener);
        changeMultiplier(mMETC_FUTTick, 1000);
        assertThat(mFixture.getFutureMultiplier(METC_FUT), comparesEqualTo(1000));

    }
	
	@Test
	public void testListenerNotNotifiedAfterRemoved() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		changeLatestTick(mIBMTick, 1);
		verify(mockListener).symbolTraded(argThat(hasNewAmount(1)));
		// remove it
		mFixture.removeInstrumentMarketDataListener(IBM, mockListener);
		changeLatestTick(mIBMTick, 2);
		verify(mockListener, never()).symbolTraded(argThat(hasNewAmount(2)));
	}

	@Test
	public void testSeparateNotifications() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(METC, mockListener2);
		changeLatestTick(mIBMTick, 12);
		changeLatestTick(mMETCTick, 150);
		verify(mockListener).symbolTraded(argThat(hasNewAmount(12)));
		verify(mockListener2).symbolTraded(argThat(hasNewAmount(150)));
		// remove one
		mFixture.removeInstrumentMarketDataListener(IBM, mockListener);
		changeLatestTick(mIBMTick, 13);
		changeLatestTick(mMETCTick, 151);
		verify(mockListener, never()).symbolTraded(argThat(hasNewAmount(13)));
		verify(mockListener2).symbolTraded(argThat(hasNewAmount(151)));
	}

	@Test
	public void testDispose() throws Exception {
		InstrumentMarketDataListener mockListener = mock(InstrumentMarketDataListener.class);
		InstrumentMarketDataListener mockListener2 = mock(InstrumentMarketDataListener.class);
		mFixture.addInstrumentMarketDataListener(IBM, mockListener);
		mFixture.addInstrumentMarketDataListener(METC, mockListener2);
		changeLatestTick(mIBMTick, 12);
		changeLatestTick(mMETCTick, 150);
		assertThat(mFixture.getLastTradePrice(IBM), comparesEqualTo(12));
		assertThat(mFixture.getLastTradePrice(METC), comparesEqualTo(150));
		mFixture.dispose();
		changeLatestTick(mIBMTick, 15);
		changeLatestTick(mMETCTick, 151);
		verify(mockListener, never()).symbolTraded(argThat(hasNewAmount(15)));
		verify(mockListener2, never()).symbolTraded(argThat(hasNewAmount(151)));
		assertThat(mFixture.getLastTradePrice(IBM), nullValue());
		assertThat(mFixture.getLastTradePrice(METC), nullValue());
	}

	private void changeLatestTick(MDLatestTick tick, int newValue) {
		// use reflection since setLatestTick isn't API
		tick.eSet(MDPackage.Literals.MD_LATEST_TICK__PRICE, new BigDecimal(newValue));
	}

	private void changePreviousClose(MDMarketstat stat, BigDecimal newPrice) throws Exception {
		// use reflection since setLatestTick isn't API
		stat.eSet(MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE, newPrice);
	}
	
	private void changeMultiplier(MDLatestTick tick, int newValue) {
        // use reflection since setLatestTick isn't API
        tick.eSet(MDPackage.Literals.MD_LATEST_TICK__MULTIPLIER, new BigDecimal(newValue));
    }

	private void changePreviousClose(MDMarketstat stat, int newPrice) throws Exception {
		changePreviousClose(stat, new BigDecimal(newPrice));
	}

	private static Matcher<InstrumentMarketDataEvent> hasNewAmount(final int newPrice) {
		return new BaseMatcher<InstrumentMarketDataEvent>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with a new price of").appendValue(newPrice);

			}

			@Override
			public boolean matches(Object item) {
				return comparesEqualTo(newPrice).matches(((InstrumentMarketDataEvent) item).getNewAmount());
			}
		};
	}

	private static Matcher<InstrumentMarketDataEvent> hasNullNewAmount() {
		return new BaseMatcher<InstrumentMarketDataEvent>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with a null new price");

			}

			@Override
			public boolean matches(Object item) {
				return nullValue().matches(((InstrumentMarketDataEvent) item).getNewAmount());
			}
		};
	}

}

package org.marketcetera.photon;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeEvent;
import org.marketcetera.core.position.MarketDataSupport.SymbolChangeListener;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDFactory;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;

/* $License$ */

/**
 * Test {@link PhotonPositionMarketData}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PhotonPositionMarketDataTest {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private PhotonPositionMarketData mFixture;
	private IMarketData mMockMarketData;
	private MDLatestTick mIBMTick;
	private IMarketDataReference<MDLatestTick> mMockIBMReference;
	private MDLatestTick mMETCTick;
	private IMarketDataReference<MDLatestTick> mMockMETCReference;
	private MDMarketstat mIBMStat;
	private MDMarketstat mMETCStat;
	private IMarketDataReference<MDMarketstat> mMockIBMStatReference;
	private IMarketDataReference<MDMarketstat> mMockMETCStatReference;
	private SessionStartTimeProvider mSessionStartTime;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		mMockMarketData = mock(IMarketData.class);
		mSessionStartTime = new SessionStartTimeProvider();
		mFixture = new PhotonPositionMarketData(mMockMarketData, mSessionStartTime);
		// latest tick stubbing
		mIBMTick = MDFactory.eINSTANCE.createMDLatestTick();
		mIBMTick.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, "IBM");
		mMETCTick = MDFactory.eINSTANCE.createMDLatestTick();
		mMETCTick.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, "METC");
		mMockIBMReference = mock(IMarketDataReference.class);
		mMockMETCReference = mock(IMarketDataReference.class);
		stub(mMockIBMReference.get()).toReturn(mIBMTick);
		stub(mMockMETCReference.get()).toReturn(mMETCTick);
		stub(mMockMarketData.getLatestTick("IBM")).toReturn(mMockIBMReference);
		stub(mMockMarketData.getLatestTick("METC")).toReturn(mMockMETCReference);
		// statistic stubbing
		mIBMStat = MDFactory.eINSTANCE.createMDMarketstat();
		mIBMStat.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, "IBM");
		mMETCStat = MDFactory.eINSTANCE.createMDMarketstat();
		mMETCStat.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, "METC");
		mMockIBMStatReference = mock(IMarketDataReference.class);
		mMockMETCStatReference = mock(IMarketDataReference.class);
		stub(mMockIBMStatReference.get()).toReturn(mIBMStat);
		stub(mMockMETCStatReference.get()).toReturn(mMETCStat);
		stub(mMockMarketData.getMarketstat("IBM")).toReturn(mMockIBMStatReference);
		stub(mMockMarketData.getMarketstat("METC")).toReturn(mMockMETCStatReference);
	}

	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new PhotonPositionMarketData(null, mSessionStartTime);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new PhotonPositionMarketData(mMockMarketData, null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getLastTradePrice(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getClosingPrice(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.addSymbolChangeListener(null, mock(SymbolChangeListener.class));
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.addSymbolChangeListener("IBM", null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.removeSymbolChangeListener(null, mock(SymbolChangeListener.class));
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.removeSymbolChangeListener("IBM", null);
			}
		};
	}

	@Test
	public void testAddingMultipleListenersGetsSingleReference() {
		mFixture.addSymbolChangeListener("IBM", mock(SymbolChangeListener.class));
		mFixture.addSymbolChangeListener("IBM", mock(SymbolChangeListener.class));
		verify(mMockMarketData, times(1)).getLatestTick("IBM");
	}

	@Test
	public void testNotificationGeneratesEvent() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		changeLatestTick(mIBMTick, 5);
		verify(mockListener).symbolTraded(argThat(hasNewPrice(5)));
	}

	@Test
	public void testMultipleListenersGetNotified() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("IBM", mockListener2);
		changeLatestTick(mIBMTick, 12);
		verify(mockListener).symbolTraded(argThat(hasNewPrice(12)));
		verify(mockListener2).symbolTraded(argThat(hasNewPrice(12)));
	}

	@Test
	public void testDuplicateListenerIgnored() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		changeLatestTick(mIBMTick, 7);
		verify(mockListener, times(1)).symbolTraded(argThat(hasNewPrice(7)));
	}

	@Test
	public void testRemoveNonExistantListenerIgnored() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.removeSymbolChangeListener("IBM", mockListener2);
		mFixture.removeSymbolChangeListener("METC", mockListener);
		verify(mMockIBMReference, never()).dispose();
	}

	@Test
	public void testNoMoreListenersDisposesDataAndRemovesAdapter() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("IBM", mockListener2);
		mFixture.removeSymbolChangeListener("IBM", mockListener);
		verify(mMockIBMReference, never()).dispose();
		mFixture.removeSymbolChangeListener("IBM", mockListener2);
		verify(mMockIBMReference).dispose();
		assertThat(mIBMTick.eAdapters().size(), is(0));
	}

	@Test
	public void testGetLastTradePrice() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("METC", mockListener2);
		changeLatestTick(mIBMTick, 18);
		changeLatestTick(mMETCTick, 19);
		assertThat(mFixture.getLastTradePrice("IBM"), comparesEqualTo(18));
		assertThat(mFixture.getLastTradePrice("METC"), comparesEqualTo(19));
	}

	@Test
	public void testGetClosingPrice() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("METC", mockListener2);
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.13 05:00:00"));
		changeClose(mIBMStat, 20, "2009.04.13");
		changeClose(mMETCStat, 25, "2009.04.13");
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(20)));
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(25)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(20));
		assertThat(mFixture.getClosingPrice("METC"), comparesEqualTo(25));
	}

	@Test
	public void testClosingPriceMatchesSessionStartTime() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.13 05:00:00"));
		changeClose(mIBMStat, 10, "2009.04.13");
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(10)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(10));
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.13 05:00:00"));
		changeClose(mIBMStat, 11, "2009.04.14");
		changePreviousClose(mIBMStat, 15, "2009.04.13");
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(15)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(15));
		changeClose(mIBMStat, 11, "2009.04.15");
		changePreviousClose(mIBMStat, 15, "2009.04.14");
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNullNewPrice()));
		assertThat(mFixture.getClosingPrice("IBM"), nullValue());
	}

	@Test
	public void testNullSessionStartTime() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		changeClose(mIBMStat, 10, "2009.04.13");
		changePreviousClose(mIBMStat, 15, "2009.04.12");
		assertThat(mFixture.getClosingPrice("IBM"), nullValue());
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.13 05:00:00"));
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(10)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(10));
		mSessionStartTime.setSessionStartTime(null);
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNullNewPrice()));
		assertThat(mFixture.getClosingPrice("IBM"), nullValue());
	}

	@Test
	public void testSessionStartTimeNotification() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("METC", mockListener2);
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.13 05:00:00"));
		changeClose(mIBMStat, 10, "2009.04.14");
		changePreviousClose(mIBMStat, 15, "2009.04.13");
		changeClose(mMETCStat, 150, "2009.04.14");
		changePreviousClose(mMETCStat, 160, "2009.04.13");
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(15)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(15));
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(160)));
		assertThat(mFixture.getClosingPrice("METC"), comparesEqualTo(160));
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.14 05:00:00"));
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(10)));
		assertThat(mFixture.getClosingPrice("IBM"), comparesEqualTo(10));
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNewPrice(150)));
		assertThat(mFixture.getClosingPrice("METC"), comparesEqualTo(150));
		mSessionStartTime.setSessionStartTime(TIME_FORMAT.parse("2009.04.15 05:00:00"));
		verify(mockListener, atLeastOnce()).closePriceChanged(argThat(hasNullNewPrice()));
		assertThat(mFixture.getClosingPrice("IBM"), nullValue());
		verify(mockListener2, atLeastOnce()).closePriceChanged(argThat(hasNullNewPrice()));
		assertThat(mFixture.getClosingPrice("METC"), nullValue());
	}
	
	@Test
	public void testListenerNotNotifiedAfterRemoved() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		changeLatestTick(mIBMTick, 1);
		verify(mockListener).symbolTraded(argThat(hasNewPrice(1)));
		// remove it
		mFixture.removeSymbolChangeListener("IBM", mockListener);
		changeLatestTick(mIBMTick, 2);
		verify(mockListener, never()).symbolTraded(argThat(hasNewPrice(2)));
	}

	@Test
	public void testSeparateNotifications() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("METC", mockListener2);
		changeLatestTick(mIBMTick, 12);
		changeLatestTick(mMETCTick, 150);
		verify(mockListener).symbolTraded(argThat(hasNewPrice(12)));
		verify(mockListener2).symbolTraded(argThat(hasNewPrice(150)));
		// remove one
		mFixture.removeSymbolChangeListener("IBM", mockListener);
		changeLatestTick(mIBMTick, 13);
		changeLatestTick(mMETCTick, 151);
		verify(mockListener, never()).symbolTraded(argThat(hasNewPrice(13)));
		verify(mockListener2).symbolTraded(argThat(hasNewPrice(151)));
	}

	@Test
	public void testDispose() throws Exception {
		SymbolChangeListener mockListener = mock(SymbolChangeListener.class);
		SymbolChangeListener mockListener2 = mock(SymbolChangeListener.class);
		mFixture.addSymbolChangeListener("IBM", mockListener);
		mFixture.addSymbolChangeListener("METC", mockListener2);
		changeLatestTick(mIBMTick, 12);
		changeLatestTick(mMETCTick, 150);
		assertThat(mFixture.getLastTradePrice("IBM"), comparesEqualTo(12));
		assertThat(mFixture.getLastTradePrice("METC"), comparesEqualTo(150));
		mFixture.dispose();
		changeLatestTick(mIBMTick, 15);
		changeLatestTick(mMETCTick, 151);
		verify(mockListener, never()).symbolTraded(argThat(hasNewPrice(15)));
		verify(mockListener2, never()).symbolTraded(argThat(hasNewPrice(151)));
		assertThat(mFixture.getLastTradePrice("IBM"), nullValue());
		assertThat(mFixture.getLastTradePrice("METC"), nullValue());
	}

	private void changeLatestTick(MDLatestTick tick, int newValue) {
		// use reflection since setLatestTick isn't API
		tick.eSet(MDPackage.Literals.MD_LATEST_TICK__PRICE, new BigDecimal(newValue));
	}

	private void changeClose(MDMarketstat stat, int newPrice, String newDate) throws Exception {
		// use reflection since setLatestTick isn't API
		stat.eSet(MDPackage.Literals.MD_MARKETSTAT__CLOSE_PRICE, new BigDecimal(newPrice));
		stat.eSet(MDPackage.Literals.MD_MARKETSTAT__CLOSE_DATE, DATE_FORMAT.parse(newDate));
	}

	private void changePreviousClose(MDMarketstat stat, int newPrice, String newDate) throws Exception {
		// use reflection since setLatestTick isn't API
		stat.eSet(MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE, new BigDecimal(newPrice));
		stat.eSet(MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE, DATE_FORMAT.parse(newDate));
	}

	private static Matcher<SymbolChangeEvent> hasNewPrice(final int newPrice) {
		return new BaseMatcher<SymbolChangeEvent>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with a new price of").appendValue(newPrice);

			}

			@Override
			public boolean matches(Object item) {
				return comparesEqualTo(newPrice).matches(((SymbolChangeEvent) item).getNewPrice());
			}
		};
	}

	private static Matcher<SymbolChangeEvent> hasNullNewPrice() {
		return new BaseMatcher<SymbolChangeEvent>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with a null new price");

			}

			@Override
			public boolean matches(Object item) {
				return nullValue().matches(((SymbolChangeEvent) item).getNewPrice());
			}
		};
	}

}

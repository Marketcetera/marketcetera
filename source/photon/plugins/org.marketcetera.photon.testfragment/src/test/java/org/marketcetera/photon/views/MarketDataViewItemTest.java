package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.marketcetera.photon.test.IsExpectedPropertyChangeEvent.isPropertyChange;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDFactory;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Test {@link MarketDataViewItem}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketDataViewItemTest {

    private static final Equity equity1 = new Equity("MSFT");
    private static final Equity equity2 = new Equity("GOOG");

    private MarketDataViewItem mFixture;
    private PropertyChangeListener mMockListener;
    private IMarketData mMockMarketData;
    private MDLatestTick mTick1;
    private MDLatestTick mTick2;
    private IMarketDataReference<MDLatestTick> mMockTick1Reference;
    private IMarketDataReference<MDLatestTick> mMockTick2Reference;
    private MDTopOfBook mTOB1;
    private MDTopOfBook mTOB2;
    private IMarketDataReference<MDTopOfBook> mMockTOB1Reference;
    private IMarketDataReference<MDTopOfBook> mMockTOB2Reference;

    @Before
    public void setUp() {
        mMockMarketData = mock(IMarketData.class);
        mTick1 = createTick(equity1);
        mTick2 = createTick(equity2);
        mMockTick1Reference = createReference(mTick1);
        mMockTick2Reference = createReference(mTick2);
        when(mMockMarketData.getLatestTick(equity1)).thenReturn(mMockTick1Reference);
        when(mMockMarketData.getLatestTick(equity2)).thenReturn(mMockTick2Reference);
        mTOB1 = createTOB(equity1);
        mTOB2 = createTOB(equity2);
        mMockTOB1Reference = createReference(mTOB1);
        mMockTOB2Reference = createReference(mTOB2);
        when(mMockMarketData.getTopOfBook(equity1)).thenReturn(mMockTOB1Reference);
        when(mMockMarketData.getTopOfBook(equity2)).thenReturn(mMockTOB2Reference);
        mFixture = new MarketDataViewItem(mMockMarketData, equity1);
        assertEquals(equity1, mFixture.getEquity());
        mMockListener = mock(PropertyChangeListener.class);
        mFixture.addPropertyChangeListener("symbol", mMockListener);
        mFixture.addPropertyChangeListener("latestTick", mMockListener);
        mFixture.addPropertyChangeListener("topOfBook", mMockListener);
    }

    private MDTopOfBook createTOB(Equity symbol) {
        MDTopOfBook item = MDFactory.eINSTANCE.createMDTopOfBook();
        item.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, symbol);
        return item;
    }

    @SuppressWarnings("unchecked")
    private <T extends MDItem> IMarketDataReference<T> createReference(T item) {
        IMarketDataReference<T> mock = mock(IMarketDataReference.class);
        when(mock.get()).thenReturn(item);
        return mock;
    }

    private MDLatestTick createTick(Equity symbol) {
        MDLatestTick tick = MDFactory.eINSTANCE.createMDLatestTick();
        tick.eSet(MDPackage.Literals.MD_ITEM__INSTRUMENT, symbol);
        return tick;
    }

    @Test
    public void testConstructorNegative() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketDataViewItem(mMockMarketData, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketDataViewItem(null, equity1);
            }
        };
    }

    @Test
    public void testSetSymbolNegative() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.setEquity(null);
            }
        };
    }

    @Test
    public void testSetEquity() {
        mFixture.setEquity(equity2);
        assertEquals(equity2, mFixture.getEquity());
        verify(mMockTick1Reference).dispose();
        verify(mMockTOB1Reference).dispose();
        verify(mMockListener).propertyChange(argThat(isPropertyChange("symbol", is(equity1.getSymbol()), is(equity2.getSymbol()))));
        verify(mMockListener).propertyChange(argThat(isPropertyChange("latestTick", hasSymbol(equity1), hasSymbol(equity2))));
        verify(mMockListener).propertyChange(argThat(isPropertyChange("topOfBook", hasSymbol(equity1), hasSymbol(equity2))));
    }

    public static Matcher<?> hasSymbol(final Equity symbol) {
        return new BaseMatcher<MDItem>() {

            @Override
            public boolean matches(Object item) {
                return ((MDItem) item).getInstrument().equals(symbol);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has symbol ").appendValue(symbol.getSymbol());
            }
        };
    }

}

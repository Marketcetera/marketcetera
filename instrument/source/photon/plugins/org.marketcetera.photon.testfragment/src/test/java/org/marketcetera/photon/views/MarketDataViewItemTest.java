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

    private static final Equity symbol1 = new Equity("MSFT");
    private static final Equity symbol2 = new Equity("GOOG");

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
        mTick1 = createTick(symbol1);
        mTick2 = createTick(symbol2);
        mMockTick1Reference = createReference(mTick1);
        mMockTick2Reference = createReference(mTick2);
        when(mMockMarketData.getLatestTick(symbol1.getSymbol())).thenReturn(mMockTick1Reference);
        when(mMockMarketData.getLatestTick(symbol2.getSymbol())).thenReturn(mMockTick2Reference);
        mTOB1 = createTOB(symbol1);
        mTOB2 = createTOB(symbol2);
        mMockTOB1Reference = createReference(mTOB1);
        mMockTOB2Reference = createReference(mTOB2);
        when(mMockMarketData.getTopOfBook(symbol1.getSymbol())).thenReturn(mMockTOB1Reference);
        when(mMockMarketData.getTopOfBook(symbol2.getSymbol())).thenReturn(mMockTOB2Reference);
        mFixture = new MarketDataViewItem(mMockMarketData, symbol1);
        assertEquals(symbol1, mFixture.getEquity());
        mMockListener = mock(PropertyChangeListener.class);
        mFixture.addPropertyChangeListener("symbol", mMockListener);
        mFixture.addPropertyChangeListener("latestTick", mMockListener);
        mFixture.addPropertyChangeListener("topOfBook", mMockListener);
    }

    private MDTopOfBook createTOB(Equity symbol) {
        MDTopOfBook item = MDFactory.eINSTANCE.createMDTopOfBook();
        item.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, symbol.getSymbol());
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
        tick.eSet(MDPackage.Literals.MD_ITEM__SYMBOL, symbol.getSymbol());
        return tick;
    }

    @Test
    public void testConstructorNegative() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new MarketDataViewItem(mMockMarketData, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new MarketDataViewItem(null, symbol1);
            }
        };
    }

    @Test
    public void testSetSymbolNegative() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.setEquity(null);
            }
        };
    }

    @Test
    public void testSetSymbol() {
        mFixture.setEquity(symbol2);
        assertEquals(symbol2, mFixture.getEquity());
        verify(mMockTick1Reference).dispose();
        verify(mMockTOB1Reference).dispose();
        verify(mMockListener).propertyChange(argThat(isPropertyChange("symbol", is(symbol1), is(symbol2))));
        verify(mMockListener).propertyChange(argThat(isPropertyChange("latestTick", hasSymbol(symbol1), hasSymbol(symbol2))));
        verify(mMockListener).propertyChange(argThat(isPropertyChange("topOfBook", hasSymbol(symbol1), hasSymbol(symbol2))));
    }

    public static Matcher<?> hasSymbol(final Equity symbol) {
        return new BaseMatcher<MDItem>() {

            @Override
            public boolean matches(Object item) {
                return ((MDItem) item).getSymbol().equals(symbol.getSymbol());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has symbol ").appendValue(symbol.getSymbol());
            }
        };
    }

}

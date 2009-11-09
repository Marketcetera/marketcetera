package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.marketdata.IDepthOfBookManager.Factory;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

/* $License$ */

/**
 * Test {@link MarketData}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketDataTest {

    private MarketData mFixture;
    private ILatestTickManager mMockLatestTickManager;
    private ITopOfBookManager mMockTopOfBookManager;
    private IMarketstatManager mMockMarketstatManager;
    private IDepthOfBookManager mMockLevel2Manager;
    private IDepthOfBookManager mMockTotalViewManager;
    private IDepthOfBookManager mMockOpenBookManager;
    private ISharedOptionLatestTickManager mSharedOptionLatestTickManager;
    private ISharedOptionMarketstatManager mSharedOptionMarketstatManager;
    private Factory mDepthOfBookFactory;
    private IMarketDataRequestSupport mMarketDataRequestAdapter;

    @Before
    public void before() {
        mMockLatestTickManager = mock(ILatestTickManager.class);
        mMockTopOfBookManager = mock(ITopOfBookManager.class);
        mMockMarketstatManager = mock(IMarketstatManager.class);
        mMockLevel2Manager = mock(IDepthOfBookManager.class);
        mMockTotalViewManager = mock(IDepthOfBookManager.class);
        mMockOpenBookManager = mock(IDepthOfBookManager.class);
        mDepthOfBookFactory = new IDepthOfBookManager.Factory() {
            @Override
            public IDepthOfBookManager create(Set<Capability> capabilities) {
                switch (capabilities.iterator().next()) {
                case LEVEL_2:
                    return mMockLevel2Manager;
                case TOTAL_VIEW:
                    return mMockTotalViewManager;
                case OPEN_BOOK:
                    return mMockOpenBookManager;
                default:
                    throw new AssertionError();
                }
            }
        };
        mSharedOptionLatestTickManager = mock(ISharedOptionLatestTickManager.class);
        mSharedOptionMarketstatManager = mock(ISharedOptionMarketstatManager.class);
        mMarketDataRequestAdapter = mock(IMarketDataRequestSupport.class);
        when(mMarketDataRequestAdapter.useFineGrainedMarketDataForOptions()).thenReturn(true);
        mFixture = new MarketData(mMockLatestTickManager,
                mMockTopOfBookManager, mMockMarketstatManager,
                mDepthOfBookFactory, mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                mMarketDataRequestAdapter);
    }

    @Test
    public void testNulls() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(null, mMockTopOfBookManager,
                        mMockMarketstatManager, mDepthOfBookFactory,
                        mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, null,
                        mMockMarketstatManager, mDepthOfBookFactory,
                        mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        null, mDepthOfBookFactory,
                        mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        mMockMarketstatManager, null,
                        mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        mMockMarketstatManager, mDepthOfBookFactory, null, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        mMockMarketstatManager, mDepthOfBookFactory,
                        mSharedOptionLatestTickManager, null, mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        mMockMarketstatManager, mDepthOfBookFactory,
                        mSharedOptionLatestTickManager, mSharedOptionMarketstatManager, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
                        mMockMarketstatManager,
                        new IDepthOfBookManager.Factory() {
                            @Override
                            public IDepthOfBookManager create(
                                    Set<Capability> capabilities) {
                                return null;
                            }
                        }, mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                        mMarketDataRequestAdapter);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getLatestTick(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getTopOfBook(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getMarketstat(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getDepthOfBook(null, Content.TOTAL_VIEW);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getDepthOfBook(new Equity("ABC"), null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                mFixture.getDepthOfBook(new Equity("ABC"), Content.MARKET_STAT);
            }
        };
    }

    /**
     * Test latest tick reference counting.
     */
    @Test
    public void testGetLatestTick() {
        new ReferenceCountTestTemplate<MDLatestTickImpl, LatestTickKey, ILatestTickManager>(
                new LatestTickKey(new Equity("IBM")), new LatestTickKey(
                        new Equity("METC")), mMockLatestTickManager) {

            @Override
            MDLatestTickImpl createItem(LatestTickKey key) {
                MDLatestTickImpl item = new MDLatestTickImpl();
                item.setInstrument(key.getInstrument());
                return item;
            }

            @Override
            IMarketDataReference<MDLatestTick> getReferenceAndValidate(
                    LatestTickKey key) {
                IMarketDataReference<MDLatestTick> ref = mFixture
                        .getLatestTick(key.getInstrument());
                assertThat(ref.get().getInstrument(), is(key.getInstrument()));
                return ref;
            }

        };
    }

    /**
     * Test latest tick with options.
     */
    @Test
    public void testGetLatestTickWithOptionsFineGrained() {
        new ReferenceCountTestTemplate<MDLatestTickImpl, LatestTickKey, ILatestTickManager>(
                new LatestTickKey(new Option("IBM", "200910", BigDecimal.ONE, OptionType.Put)), new LatestTickKey(
                        new Option("METC", "200910", BigDecimal.ONE, OptionType.Put)), mMockLatestTickManager) {

            @Override
            MDLatestTickImpl createItem(LatestTickKey key) {
                MDLatestTickImpl item = new MDLatestTickImpl();
                item.setInstrument(key.getInstrument());
                return item;
            }

            @Override
            IMarketDataReference<MDLatestTick> getReferenceAndValidate(
                    LatestTickKey key) {
                IMarketDataReference<MDLatestTick> ref = mFixture
                        .getLatestTick(key.getInstrument());
                assertThat(ref.get().getInstrument(), is(key.getInstrument()));
                return ref;
            }

        };
    }

    /**
     * Test latest tick with shared option data flow.
     */
    @Test
    public void testGetLatestTickWithSharedOptionFlows() {
        when(mMarketDataRequestAdapter.useFineGrainedMarketDataForOptions()).thenReturn(false);
        mFixture = new MarketData(mMockLatestTickManager,
                mMockTopOfBookManager, mMockMarketstatManager,
                mDepthOfBookFactory, mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                mMarketDataRequestAdapter);
        new SharedReferenceCountTestTemplate<MDLatestTickImpl, SharedOptionLatestTickKey, ISharedOptionLatestTickManager>(
                new SharedOptionLatestTickKey(new Equity("IBM")),
                new SharedOptionLatestTickKey(new Equity("METC")), new Option(
                        "IBM", "200910", BigDecimal.ONE, OptionType.Put),
                new Option("IBM", "200910", BigDecimal.ONE, OptionType.Call),
                new Option("METC", "200910", BigDecimal.ONE, OptionType.Put),
                mSharedOptionLatestTickManager) {
            @Override
            MDLatestTickImpl createItem(Option option) {
                MDLatestTickImpl item = new MDLatestTickImpl();
                item.setInstrument(option);
                return item;
            }

            @Override
            IMarketDataReference<? extends MDItem> getReferenceAndValidate(
                    Option option) {
                IMarketDataReference<MDLatestTick> ref = mFixture
                        .getLatestTick(option);
                assertThat(ref.get().getInstrument(), is((Instrument) option));
                return ref;
            }

        };
    }

    /**
     * Test top of book reference counting.
     */
    @Test
    public void testTopOfBook() {
        new ReferenceCountTestTemplate<MDTopOfBookImpl, TopOfBookKey, ITopOfBookManager>(
                new TopOfBookKey(new Equity("IBM")), new TopOfBookKey(
                        new Equity("METC")), mMockTopOfBookManager) {

            @Override
            MDTopOfBookImpl createItem(TopOfBookKey key) {
                MDTopOfBookImpl item = new MDTopOfBookImpl();
                item.setInstrument(key.getInstrument());
                return item;
            }

            @Override
            IMarketDataReference<MDTopOfBook> getReferenceAndValidate(
                    TopOfBookKey key) {
                IMarketDataReference<MDTopOfBook> ref = mFixture
                        .getTopOfBook(key.getInstrument());
                assertThat(ref.get().getInstrument(), is(key.getInstrument()));
                return ref;
            }

        };
    }

    /**
     * Test symbol statistic reference counting.
     */
    @Test
    public void testMarketstat() {
        new ReferenceCountTestTemplate<MDMarketstatImpl, MarketstatKey, IMarketstatManager>(
                new MarketstatKey(new Equity("GOOG")), new MarketstatKey(
                        new Equity("XYZ")), mMockMarketstatManager) {

            @Override
            MDMarketstatImpl createItem(MarketstatKey key) {
                MDMarketstatImpl item = new MDMarketstatImpl();
                item.setInstrument(key.getInstrument());
                return item;
            }

            @Override
            IMarketDataReference<MDMarketstat> getReferenceAndValidate(
                    MarketstatKey key) {
                IMarketDataReference<MDMarketstat> ref = mFixture
                        .getMarketstat(key.getInstrument());
                assertThat(ref.get().getInstrument(), is(key.getInstrument()));
                return ref;
            }

        };
    }

    /**
     * Test marketstat with options.
     */
    @Test
    public void testGetMarketstatWithOptionsFineGrained() {
        new ReferenceCountTestTemplate<MDMarketstatImpl, MarketstatKey, IMarketstatManager>(
                new MarketstatKey(new Option("IBM", "200910", BigDecimal.ONE, OptionType.Put)), new MarketstatKey(
                        new Option("METC", "200910", BigDecimal.ONE, OptionType.Put)), mMockMarketstatManager) {

            @Override
            MDMarketstatImpl createItem(MarketstatKey key) {
                MDMarketstatImpl item = new MDMarketstatImpl();
                item.setInstrument(key.getInstrument());
                return item;
            }

            @Override
            IMarketDataReference<MDMarketstat> getReferenceAndValidate(
                    MarketstatKey key) {
                IMarketDataReference<MDMarketstat> ref = mFixture
                        .getMarketstat(key.getInstrument());
                assertThat(ref.get().getInstrument(), is(key.getInstrument()));
                return ref;
            }

        };
    }

    /**
     * Test marketstat with shared option data flow.
     */
    @Test
    public void testGetMarketstatWithSharedOptionFlows() {
        when(mMarketDataRequestAdapter.useFineGrainedMarketDataForOptions()).thenReturn(false);
        mFixture = new MarketData(mMockLatestTickManager,
                mMockTopOfBookManager, mMockMarketstatManager,
                mDepthOfBookFactory, mSharedOptionLatestTickManager, mSharedOptionMarketstatManager,
                mMarketDataRequestAdapter);
        new SharedReferenceCountTestTemplate<MDMarketstatImpl, SharedOptionMarketstatKey, ISharedOptionMarketstatManager>(
                new SharedOptionMarketstatKey(new Equity("IBM")),
                new SharedOptionMarketstatKey(new Equity("METC")), new Option(
                        "IBM", "200910", BigDecimal.ONE, OptionType.Put),
                new Option("IBM", "200910", BigDecimal.ONE, OptionType.Call),
                new Option("METC", "200910", BigDecimal.ONE, OptionType.Put),
                mSharedOptionMarketstatManager) {
            @Override
            MDMarketstatImpl createItem(Option option) {
                MDMarketstatImpl item = new MDMarketstatImpl();
                item.setInstrument(option);
                return item;
            }

            @Override
            IMarketDataReference<? extends MDItem> getReferenceAndValidate(
                    Option option) {
                IMarketDataReference<MDMarketstat> ref = mFixture
                        .getMarketstat(option);
                assertThat(ref.get().getInstrument(), is((Instrument) option));
                return ref;
            }

        };
    }

    /**
     * Test Level 2 reference counting.
     */
    @Test
    public void testLevel2() {
        new DepthOfBookReferenceCountTestTemplate(Content.LEVEL_2,
                mMockLevel2Manager);
    }

    /**
     * Test TotalView reference counting.
     */
    @Test
    public void testTotalView() {
        new DepthOfBookReferenceCountTestTemplate(Content.TOTAL_VIEW,
                mMockTotalViewManager);
    }

    /**
     * Test OpenBook reference counting.
     */
    @Test
    public void testOpenBook() {
        new DepthOfBookReferenceCountTestTemplate(Content.OPEN_BOOK,
                mMockOpenBookManager);
    }

    @Test
    public void testSetSourceModule() throws Exception {
        IMarketDataFeed mockFeed = mock(IMarketDataFeed.class);
        when(mockFeed.getURN()).thenReturn(new ModuleURN("abc:abc:abc:abc"));
        mFixture.setSourceFeed(mockFeed);
        for (IDataFlowManager<?, ?> manager : ImmutableSet
                .<IDataFlowManager<?, ?>> of(mMockLatestTickManager,
                        mMockTopOfBookManager, mMockMarketstatManager,
                        mMockLevel2Manager, mMockTotalViewManager,
                        mMockOpenBookManager)) {
            verify(manager).setSourceFeed(mockFeed);
        }
    }

    private abstract class ReferenceCountTestTemplate<T, K extends Key, M extends IDataFlowManager<T, K>> {

        public ReferenceCountTestTemplate(K key1, K key2, M manager) {
            T mockTick1 = createItem(key1);
            T mockTick2 = createItem(key2);
            when(manager.getItem(key1)).thenReturn(mockTick1);
            when(manager.getItem(key2)).thenReturn(mockTick2);
            // get a reference
            IMarketDataReference<? extends MDItem> ref1 = getReferenceAndValidate(key1);
            // start flow should have been called
            verify(manager).startFlow(key1);
            IMarketDataReference<? extends MDItem> ref2 = getReferenceAndValidate(key1);
            ref1.dispose();
            IMarketDataReference<? extends MDItem> ref3 = getReferenceAndValidate(key1);
            // stop flow not called yet
            verify(manager, never()).stopFlow(key1);
            ref2.dispose();
            ref3.dispose();
            // now stop flow called
            verify(manager).stopFlow(key1);
            IMarketDataReference<? extends MDItem> ref4 = getReferenceAndValidate(key1);
            // start flow called a second time
            verify(manager, times(2)).startFlow(key1);
            IMarketDataReference<? extends MDItem> ref5 = getReferenceAndValidate(key2);
            verify(manager).startFlow(key2);
            ref4.dispose();
            ref5.dispose();
            verify(manager, times(2)).stopFlow(key1);
            verify(manager).stopFlow(key2);
        }

        abstract T createItem(K key);

        abstract IMarketDataReference<? extends MDItem> getReferenceAndValidate(
                K key);
    }

    private class DepthOfBookReferenceCountTestTemplate
            extends
            ReferenceCountTestTemplate<MDDepthOfBookImpl, DepthOfBookKey, IDepthOfBookManager> {

        public DepthOfBookReferenceCountTestTemplate(Content content,
                IDepthOfBookManager manager) {
            super(new DepthOfBookKey(new Equity("GOOG"), content),
                    new DepthOfBookKey(new Equity("IBM"), content), manager);
        }

        @Override
        MDDepthOfBookImpl createItem(DepthOfBookKey key) {
            MDDepthOfBookImpl item = new MDDepthOfBookImpl();
            item.setInstrument(key.getInstrument());
            item.setProduct(key.getProduct());
            return item;
        }

        @Override
        IMarketDataReference<MDDepthOfBook> getReferenceAndValidate(
                DepthOfBookKey key) {
            IMarketDataReference<MDDepthOfBook> ref = mFixture.getDepthOfBook(
                    key.getInstrument(), key.getProduct());
            assertThat(ref.get().getInstrument(), is(key.getInstrument()));
            assertThat(ref.get().getProduct(), is(key.getProduct()));
            return ref;
        }
    }
    


    private abstract class SharedReferenceCountTestTemplate<T, K extends Key, M extends IDataFlowManager<Map<Option, T>, K>> {

        public SharedReferenceCountTestTemplate(K key1, K key2,
                Option option1a, Option option1b, Option option2, M manager) {
            Function<Option, T> computer = new Function<Option, T>() {
                        @Override
                        public T apply(Option from) {
                            return createItem(from);
                        }
                    };
            Map<Option, T> map1 = new MapMaker().makeComputingMap(computer);
            Map<Option, T> map2 = new MapMaker().makeComputingMap(computer);
            when(manager.getItem(key1)).thenReturn(map1);
            when(manager.getItem(key2)).thenReturn(map2);
            // get a reference
            IMarketDataReference<? extends MDItem> ref1 = getReferenceAndValidate(option1a);
            // start flow should have been called
            verify(manager).startFlow(key1);
            // the option should exist in the map
            assertMap(map1, option1a);
            // get another reference under the same key
            IMarketDataReference<? extends MDItem> ref2 = getReferenceAndValidate(option1b);
            // the new option should exist in the map
            assertMap(map1, option1a, option1b);
            // dispose the first reference
            ref1.dispose();
            // the option should no longer exist in the map
            assertMap(map1, option1b);
            // get the first option data again
            IMarketDataReference<? extends MDItem> ref3 = getReferenceAndValidate(option1a);
            // option back in the map
            assertMap(map1, option1a, option1b);
            // get a second reference to the same option
            IMarketDataReference<? extends MDItem> ref4 = getReferenceAndValidate(option1a);
            // map should not change
            assertMap(map1, option1a, option1b);
            // dispose one of the option1a references
            ref3.dispose();
            // map should not change
            assertMap(map1, option1a, option1b);
            // stop flow not called yet
            verify(manager, never()).stopFlow(key1);
            ref2.dispose();
            ref4.dispose();
            // now stop flow called
            verify(manager).stopFlow(key1);
            IMarketDataReference<? extends MDItem> ref5 = getReferenceAndValidate(option1a);
            // start flow called a second time
            verify(manager, times(2)).startFlow(key1);
            IMarketDataReference<? extends MDItem> ref6 = getReferenceAndValidate(option2);
            verify(manager).startFlow(key2);
            // verify both maps
            assertMap(map1, option1a);
            assertMap(map2, option2);
            ref5.dispose();
            ref6.dispose();
            verify(manager, times(2)).stopFlow(key1);
            verify(manager).stopFlow(key2);
        }
        
        private void assertMap(Map<Option, T> map, Option... options) {
            assertThat(map.size(), is(options.length));
            for (Option option : options) {
                assertThat(map.containsKey(option), is(true));
            }
        }

        abstract T createItem(Option option);

        abstract IMarketDataReference<? extends MDItem> getReferenceAndValidate(Option option);
    }
}

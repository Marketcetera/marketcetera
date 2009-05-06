package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.marketcetera.photon.model.marketdata.impl.MDItemImpl;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;

import com.google.common.collect.ImmutableSet;

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
	private Factory mDepthOfBookFactory;

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
		mFixture = new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
				mMockMarketstatManager, mDepthOfBookFactory);
	}

	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(null, mMockTopOfBookManager, mMockMarketstatManager,
						mDepthOfBookFactory);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, null, mMockMarketstatManager,
						mDepthOfBookFactory);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, mMockTopOfBookManager, null,
						mDepthOfBookFactory);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
						mMockMarketstatManager, null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, mMockTopOfBookManager,
						mMockMarketstatManager, new IDepthOfBookManager.Factory() {
							@Override
							public IDepthOfBookManager create(Set<Capability> capabilities) {
								return null;
							}
						});
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getLatestTick(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getTopOfBook(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getMarketstat(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getDepthOfBook(null, Content.TOTAL_VIEW);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getDepthOfBook("ABC", null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getDepthOfBook("ABC", Content.MARKET_STAT);
			}
		};
	}

	/**
	 * Test latest tick reference counting.
	 */
	@Test
	public void testGetLatestTick() {
		new ReferenceCountTestTemplate<MDLatestTickImpl, LatestTickKey, ILatestTickManager>(
				new LatestTickKey("IBM"), new LatestTickKey("METC"), mMockLatestTickManager) {

			@Override
			MDLatestTickImpl createItem(LatestTickKey key) {
				MDLatestTickImpl item = new MDLatestTickImpl();
				item.setSymbol(key.getSymbol());
				return item;
			}

			@Override
			IMarketDataReference<MDLatestTick> getReferenceAndValidate(LatestTickKey key) {
				IMarketDataReference<MDLatestTick> ref = mFixture.getLatestTick(key.getSymbol());
				assertThat(ref.get().getSymbol(), is(key.getSymbol()));
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
				new TopOfBookKey("IBM"), new TopOfBookKey("METC"), mMockTopOfBookManager) {

			@Override
			MDTopOfBookImpl createItem(TopOfBookKey key) {
				MDTopOfBookImpl item = new MDTopOfBookImpl();
				item.setSymbol(key.getSymbol());
				return item;
			}

			@Override
			IMarketDataReference<MDTopOfBook> getReferenceAndValidate(TopOfBookKey key) {
				IMarketDataReference<MDTopOfBook> ref = mFixture.getTopOfBook(key.getSymbol());
				assertThat(ref.get().getSymbol(), is(key.getSymbol()));
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
				new MarketstatKey("GOOG"), new MarketstatKey("XYZ"), mMockMarketstatManager) {

			@Override
			MDMarketstatImpl createItem(MarketstatKey key) {
				MDMarketstatImpl item = new MDMarketstatImpl();
				item.setSymbol(key.getSymbol());
				return item;
			}

			@Override
			IMarketDataReference<MDMarketstat> getReferenceAndValidate(MarketstatKey key) {
				IMarketDataReference<MDMarketstat> ref = mFixture.getMarketstat(key.getSymbol());
				assertThat(ref.get().getSymbol(), is(key.getSymbol()));
				return ref;
			}

		};
	}

	/**
	 * Test Level 2 reference counting.
	 */
	@Test
	public void testLevel2() {
		new DepthOfBookReferenceCountTestTemplate(Content.LEVEL_2, mMockLevel2Manager);
	}

	/**
	 * Test TotalView reference counting.
	 */
	@Test
	public void testTotalView() {
		new DepthOfBookReferenceCountTestTemplate(Content.TOTAL_VIEW, mMockTotalViewManager);
	}

	/**
	 * Test OpenBook reference counting.
	 */
	@Test
	public void testOpenBook() {
		new DepthOfBookReferenceCountTestTemplate(Content.OPEN_BOOK, mMockOpenBookManager);
	}

	@Test
	public void testSetSourceModule() throws Exception {
		IMarketDataFeed mockFeed = mock(IMarketDataFeed.class);
		stub(mockFeed.getURN()).toReturn(new ModuleURN("abc:abc:abc:abc"));
		mFixture.setSourceFeed(mockFeed);
		for (IDataFlowManager<?, ?> manager : ImmutableSet.<IDataFlowManager<?, ?>> of(
				mMockLatestTickManager, mMockTopOfBookManager, mMockMarketstatManager,
				mMockLevel2Manager, mMockTotalViewManager, mMockOpenBookManager)) {
			verify(manager).setSourceFeed(mockFeed);
		}
	}

	private abstract class ReferenceCountTestTemplate<T extends MDItemImpl, K extends Key<? super T>, M extends IDataFlowManager<T, K>> {

		private K mKey2;
		private K mKey1;
		private M mManager;

		public ReferenceCountTestTemplate(K key1, K key2, M manager) {
			mKey1 = key1;
			mKey2 = key2;
			mManager = manager;
			run();
		}

		abstract T createItem(K key);

		abstract IMarketDataReference<? extends MDItem> getReferenceAndValidate(K key);

		private void run() {
			T mockTick1 = createItem(mKey1);
			T mockTick2 = createItem(mKey2);
			stub(mManager.getItem(mKey1)).toReturn(mockTick1);
			stub(mManager.getItem(mKey2)).toReturn(mockTick2);
			// get a reference
			IMarketDataReference<? extends MDItem> ref1 = getReferenceAndValidate(mKey1);
			// start flow should have been called
			verify(mManager).startFlow(mKey1);
			IMarketDataReference<? extends MDItem> ref2 = getReferenceAndValidate(mKey1);
			ref1.dispose();
			IMarketDataReference<? extends MDItem> ref3 = getReferenceAndValidate(mKey1);
			// stop flow not called yet
			verify(mManager, never()).stopFlow(mKey1);
			ref2.dispose();
			ref3.dispose();
			// now stop flow called
			verify(mManager).stopFlow(mKey1);
			IMarketDataReference<? extends MDItem> ref4 = getReferenceAndValidate(mKey1);
			// start flow called a second time
			verify(mManager, times(2)).startFlow(mKey1);
			IMarketDataReference<? extends MDItem> ref5 = getReferenceAndValidate(mKey2);
			verify(mManager).startFlow(mKey2);
			ref4.dispose();
			ref5.dispose();
			verify(mManager, times(2)).stopFlow(mKey1);
			verify(mManager).stopFlow(mKey2);
		}
	}
	
	private class DepthOfBookReferenceCountTestTemplate extends
			ReferenceCountTestTemplate<MDDepthOfBookImpl, DepthOfBookKey, IDepthOfBookManager> {

		public DepthOfBookReferenceCountTestTemplate(Content content, IDepthOfBookManager manager) {
			super(new DepthOfBookKey("GOOG", content), new DepthOfBookKey("IBM", content), manager);
		}

		@Override
		MDDepthOfBookImpl createItem(DepthOfBookKey key) {
			MDDepthOfBookImpl item = new MDDepthOfBookImpl();
			item.setSymbol(key.getSymbol());
			item.setProduct(key.getProduct());
			return item;
		}

		@Override
		IMarketDataReference<MDDepthOfBook> getReferenceAndValidate(DepthOfBookKey key) {
			IMarketDataReference<MDDepthOfBook> ref = mFixture.getDepthOfBook(key.getSymbol(), key
					.getProduct());
			assertThat(ref.get().getSymbol(), is(key.getSymbol()));
			assertThat(ref.get().getProduct(), is(key.getProduct()));
			return ref;
		}

	}
}

package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.model.marketdata.impl.MDItemImpl;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;

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

	@Before
	public void before() {
		mMockLatestTickManager = mock(ILatestTickManager.class);
		mMockTopOfBookManager = mock(ITopOfBookManager.class);
		mMockMarketstatManager = mock(IMarketstatManager.class);
		mFixture = new MarketData(mMockLatestTickManager, mMockTopOfBookManager, mMockMarketstatManager);
	}
	
	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(null, mMockTopOfBookManager, mMockMarketstatManager);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, null, mMockMarketstatManager);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, mMockTopOfBookManager, null);
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
	}

	/**
	 * Test latest tick reference counting.
	 */
	@Test
	public void testGetLatestTick() {
		new ReferenceCountTestTemplate<MDLatestTickImpl, LatestTickKey, ILatestTickManager>(new LatestTickKey("IBM"), new LatestTickKey("METC"), mMockLatestTickManager) {

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
		new ReferenceCountTestTemplate<MDTopOfBookImpl, TopOfBookKey, ITopOfBookManager>(new TopOfBookKey("IBM"), new TopOfBookKey("METC"), mMockTopOfBookManager) {

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
		new ReferenceCountTestTemplate<MDMarketstatImpl, MarketstatKey, IMarketstatManager>(new MarketstatKey("GOOG"), new MarketstatKey("XYZ"), mMockMarketstatManager) {

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

	@Test
	public void testSetSourceModule() throws Exception {
		ModuleURN moduleURN = new ModuleURN("abc:abc:abc:abc");
		mFixture.setSourceModule(moduleURN);
		verify(mMockLatestTickManager).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager).setSourceModule(moduleURN);
		verify(mMockMarketstatManager).setSourceModule(moduleURN);
		moduleURN = new ModuleURN("abc:abc:abc:abc1");
		mFixture.setLatestTickSourceModule(moduleURN);
		verify(mMockLatestTickManager).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager, never()).setSourceModule(moduleURN);
		verify(mMockMarketstatManager, never()).setSourceModule(moduleURN);
		moduleURN = new ModuleURN("abc:abc:abc:abc2");
		mFixture.setTopOfBookSourceModule(moduleURN);
		verify(mMockLatestTickManager, never()).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager).setSourceModule(moduleURN);
		verify(mMockMarketstatManager, never()).setSourceModule(moduleURN);
		moduleURN = new ModuleURN("abc:abc:abc:abc3");
		mFixture.setMarketstatSourceModule(moduleURN);
		verify(mMockLatestTickManager, never()).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager, never()).setSourceModule(moduleURN);
		verify(mMockMarketstatManager).setSourceModule(moduleURN);
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
			T mockIBMTick = createItem(mKey1);
			T mockMETCTick = createItem(mKey2);
			stub(mManager.getItem(mKey1)).toReturn(mockIBMTick);
			stub(mManager.getItem(mKey2)).toReturn(mockMETCTick);
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
}

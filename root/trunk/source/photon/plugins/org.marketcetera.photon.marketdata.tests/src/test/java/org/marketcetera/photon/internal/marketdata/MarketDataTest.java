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
import org.marketcetera.photon.model.marketdata.MDLatestTick;

/* $License$ */

/**
 * Test {@link MarketData}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataTest {

	private ILatestTickManager mMockLatestTickManager;
	private MarketData mFixture;
	private ITopOfBookManager mMockTopOfBookManager;

	@Before
	public void before() {
		mMockLatestTickManager = mock(ILatestTickManager.class);
		mMockTopOfBookManager = mock(ITopOfBookManager.class);
		mFixture = new MarketData(mMockLatestTickManager, mMockTopOfBookManager);
	}
	
	@Test
	public void testNulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(null, mMockTopOfBookManager);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketData(mMockLatestTickManager, null);
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
	}

	/**
	 * Test reference counting.
	 */
	@Test
	public void testGetLatestTick() {
		MDLatestTick mockIBMTick = mock(MDLatestTick.class);
		stub(mockIBMTick.getSymbol()).toReturn("IBM");
		MDLatestTick mockMETCTick = mock(MDLatestTick.class);
		stub(mockMETCTick.getSymbol()).toReturn("METC");
		stub(mMockLatestTickManager.getItem(new LatestTickKey("IBM"))).toReturn(mockIBMTick);
		stub(mMockLatestTickManager.getItem(new LatestTickKey("METC"))).toReturn(mockMETCTick);
		// get a reference
		IMarketDataReference<MDLatestTick> ref1 = mFixture.getLatestTick("IBM");
		assertThat(ref1.get().getSymbol(), is("IBM"));
		// start flow should have been called
		verify(mMockLatestTickManager).startFlow(new LatestTickKey("IBM"));
		IMarketDataReference<MDLatestTick> ref2 = mFixture.getLatestTick("IBM");
		assertThat(ref2.get().getSymbol(), is("IBM"));
		ref1.dispose();
		IMarketDataReference<MDLatestTick> ref3 = mFixture.getLatestTick("IBM");
		assertThat(ref3.get().getSymbol(), is("IBM"));
		// stop flow not called yet
		verify(mMockLatestTickManager, never()).stopFlow(new LatestTickKey("IBM"));
		ref2.dispose();
		ref3.dispose();
		// now stop flow called
		verify(mMockLatestTickManager).stopFlow(new LatestTickKey("IBM"));
		IMarketDataReference<MDLatestTick> ref4 = mFixture.getLatestTick("IBM");
		assertThat(ref4.get().getSymbol(), is("IBM"));
		// start flow called a second time
		verify(mMockLatestTickManager, times(2)).startFlow(new LatestTickKey("IBM"));
		IMarketDataReference<MDLatestTick> ref5 = mFixture.getLatestTick("METC");
		assertThat(ref5.get().getSymbol(), is("METC"));
		verify(mMockLatestTickManager).startFlow(new LatestTickKey("METC"));
		ref4.dispose();
		ref5.dispose();
		verify(mMockLatestTickManager, times(2)).stopFlow(new LatestTickKey("IBM"));
		verify(mMockLatestTickManager).stopFlow(new LatestTickKey("METC"));
	}

	@Test
	public void testSetSourceModule() throws Exception {
		ModuleURN moduleURN = new ModuleURN("abc:abc:abc:abc");
		mFixture.setSourceModule(moduleURN);
		verify(mMockLatestTickManager).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager).setSourceModule(moduleURN);
		moduleURN = new ModuleURN("abc:abc:abc:abc1");
		mFixture.setLatestTickSourceModule(moduleURN);
		verify(mMockLatestTickManager).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager, never()).setSourceModule(moduleURN);
		moduleURN = new ModuleURN("abc:abc:abc:abc2");
		mFixture.setTopOfBookSourceModule(moduleURN);
		verify(mMockLatestTickManager, never()).setSourceModule(moduleURN);
		verify(mMockTopOfBookManager).setSourceModule(moduleURN);
	}
}

package org.marketcetera.photon.internal.marketdata;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.marketdata.MockMarketDataModuleFactory;
import org.marketcetera.photon.marketdata.MockMarketDataModuleFactory.MockMarketDataModule;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.module.ModuleSupport;

/* $License$ */

/**
 * Base class for {@link DataFlowManager} tests.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class DataFlowManagerTestBase<T extends MDItem, K extends Key<T>> {

	/**
	 * @return the test fixture
	 */
	protected abstract IDataFlowManager<T, K> createFixture(ModuleManager moduleManager);

	/**
	 * @return a key
	 */
	protected abstract K createKey1();

	/**
	 * @return another key
	 */
	protected abstract K createKey2();

	/**
	 * Verify item for given key was initialized properly
	 */
	protected abstract void validateInitialConditions(T item, K key);

	/**
	 * @return an event to send
	 */
	protected abstract Object createEvent1(K key);

	/**
	 * @return another event to send
	 */
	protected abstract Object createEvent2(K key);

	/**
	 * Verify item received event returned by {@link #createEvent1(Key)}
	 */
	protected abstract void validateState1(T item);

	/**
	 * Verify item received event returned by {@link #createEvent2(Key)}
	 */
	protected abstract void validateState2(T item);

	/**
	 * Verify request is correct for given key
	 */
	protected abstract void validateRequest(K key, MarketDataRequest request);

	private IDataFlowManager<T, K> mFixture;
	private K mKey1;
	private T mItem1;
	private K mKey2;
	private T mItem2;
	private MockMarketDataModule mMockMarketDataModule;

	@Before
	public void before() {
		MarketDataManager.getCurrent().reconnectFeed(
				MockMarketDataModuleFactory.PROVIDER_URN.toString());
		mMockMarketDataModule = MockMarketDataModuleFactory.sInstance;
		mMockMarketDataModule.setStatus("AVAILABLE");
		mFixture = createFixture(ModuleSupport.getModuleManager());
		mFixture.setSourceModule(MockMarketDataModuleFactory.INSTANCE_URN);
		mKey1 = createKey1();
		mItem1 = mFixture.getItem(mKey1);
		validateInitialConditions(mItem1, mKey1);
		mKey2 = createKey2();
		mItem2 = mFixture.getItem(mKey2);
		validateInitialConditions(mItem2, mKey2);
	}

	@After
	public void after() {
		if (mFixture != null) {
			mFixture.stopFlow(mKey1);
			mFixture.stopFlow(mKey2);
		}
	}

	@Test
	public void testSuccessiveStartAndStops() {
		mFixture.startFlow(mKey1);
		// try again, make sure nothing bad happens
		mFixture.startFlow(mKey1);
		// stop
		mFixture.stopFlow(mKey1);
		// try again, make sure nothing bad happens
		mFixture.stopFlow(mKey1);
	}

	@Test
	public void testDataOnlyReceivedWhenStarted() {
		// emit first event
		emit(createEvent1(mKey1));
		// item should not have changed
		validateInitialConditions(mItem1, mKey1);

		// start flow
		mFixture.startFlow(mKey1);
		// emit second event
		emit(createEvent2(mKey1));
		// item should have changed
		validateState2(mItem1);

		// stop flow
		mFixture.stopFlow(mKey1);
		// emit first event
		emit(createEvent1(mKey1));
		// item should not have changed
		validateState2(mItem1);

		// start again
		mFixture.startFlow(mKey1);
		// emit first event again
		emit(createEvent1(mKey1));
		// should get it this time
		validateState1(mItem1);

		// finish
		mFixture.stopFlow(mKey1);
	}

	@Test
	public void testDataStopsWhenSourceSetToNull() {
		// start flow
		mFixture.startFlow(mKey1);
		// emit second event
		emit(createEvent2(mKey1));
		// item should have changed
		validateState2(mItem1);

		// null out the source
		mFixture.setSourceModule(null);
		// item should have been reset
		validateInitialConditions(mItem1, mKey1);
		// emit first event
		emit(createEvent1(mKey1));
		// item should not have changed
		validateInitialConditions(mItem1, mKey1);

		// set the source back
		mFixture.setSourceModule(MockMarketDataModuleFactory.INSTANCE_URN);
		// item should still be reset
		validateInitialConditions(mItem1, mKey1);
		// emit first event again
		emit(createEvent1(mKey1));
		// should get it this time
		validateState1(mItem1);

		// finish
		mFixture.stopFlow(mKey1);
	}

	@Test
	public void testSeparateDataRequests() {
		mFixture.startFlow(mKey1);
		validateRequest(mKey1);
		mFixture.startFlow(mKey2);
		validateRequest(mKey2);
	}

	private void validateRequest(K key) {
		MarketDataRequest request = (MarketDataRequest) mMockMarketDataModule.getLastRequest()
				.getData();
		validateRequest(key, request);
	}

	private void emit(Object object) {
		mMockMarketDataModule.emitData(object);
	}
}

package org.marketcetera.photon.internal.marketdata;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.marketdata.MockMarketDataModuleFactory;
import org.marketcetera.photon.marketdata.MockMarketDataModuleFactory.MockMarketDataModule;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.test.TestCaseBase;

/* $License$ */

/**
 * Base class for {@link DataFlowManager} tests.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public abstract class DataFlowManagerTestBase<T extends MDItem, K extends Key<T>> extends
		TestCaseBase {

	/**
	 * @return the test fixture
	 */
	protected abstract IDataFlowManager<? extends T, K> createFixture(ModuleManager moduleManager);

	/**
	 * @return a key
	 */
	protected abstract K createKey1();

	/**
	 * @return another key
	 */
	protected abstract K createKey2();

	/**
	 * @return a third key
	 */
	protected abstract K createKey3();

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

	protected IDataFlowManager<? extends T, K> mFixture;
	protected K mKey1;
	protected T mItem1;
	protected K mKey2;
	protected T mItem2;
	protected MockMarketDataModule mMockMarketDataModule;
	protected K mKey3;

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
		mKey3 = createKey3();
	}

	@After
	public void after() {
		if (mFixture != null) {
			mFixture.stopFlow(mKey1);
			mFixture.stopFlow(mKey2);
			mFixture.stopFlow(mKey3);
		}
	}

	@Test
	public void testSuccessiveStartsAndStops() {
		// stop before start, make sure nothing bad happens
		mFixture.stopFlow(mKey1);
		// start
		mFixture.startFlow(mKey1);
		// try again, make sure nothing bad happens
		mFixture.startFlow(mKey1);
		// stop
		mFixture.stopFlow(mKey1);
		// try again, make sure nothing bad happens
		mFixture.stopFlow(mKey1);
		// start again
		mFixture.startFlow(mKey1);
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

	@Test
	public void testUnexpectedDataReported() throws Exception {
		setLevel(mFixture.getClass().getName(), Level.WARN);
		mFixture.startFlow(mKey1);
		Object unexpected = new Object();
		emit(unexpected);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_UNEXPECTED_DATA.getText(unexpected, getLastRequest()), null);
	}

	@Test
	public void testInvalidSymbolIgnoredAndReported() throws Exception {
		setLevel(mFixture.getClass().getName(), Level.WARN);
		mFixture.startFlow(mKey1);
		validateInitialConditions(mItem1, mKey1);
		// emit data for wrong key
		emit(createEvent1(mKey2));
		validateInitialConditions(mItem1, mKey1);
		assertLastEvent(Level.WARN, mFixture.getClass().getName(),
				Messages.DATA_FLOW_MANAGER_EVENT_SYMBOL_MISMATCH.getText(mKey2.getSymbol(), mKey1
						.getSymbol()), null);
	}

	@Test
	public void testStartFlowBeforeGetItem() throws Exception {
		mFixture.startFlow(mKey3);
		emit(createEvent1(mKey3));
		validateState1(mFixture.getItem(mKey3));
	}
	
	@Test
	public void nullKeys() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.getItem(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.startFlow(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				mFixture.stopFlow(null);
			}
		};
	}

	private void validateRequest(K key) {
		MarketDataRequest request = getLastRequest();
		validateRequest(key, request);
	}

	private MarketDataRequest getLastRequest() {
		return (MarketDataRequest) mMockMarketDataModule.getLastRequest()
				.getData();
	}

	protected void emit(Object object) {
		mMockMarketDataModule.emitData(object);
	}
}

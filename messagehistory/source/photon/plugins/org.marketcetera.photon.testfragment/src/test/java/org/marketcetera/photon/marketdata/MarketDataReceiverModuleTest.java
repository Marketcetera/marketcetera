package org.marketcetera.photon.marketdata;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.MarketDataSubscriber;
import org.marketcetera.util.except.I18NException;

/* $License$ */

/**
 * Test for {@link MarketDataReceiverModule}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataReceiverModuleTest {

	private ModuleManager mModuleManager;

	@Before
	public void setUp() throws Exception {
		mModuleManager = new ModuleManager();
		mModuleManager.init();
		mModuleManager.start(MockMarketDataModuleFactory.INSTANCE_URN);
	}

	@After
	public void tearDown() throws Exception {
		mModuleManager.stop();
		mModuleManager = null;
	}

	@Test
	public void testCreation() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_RECEIVER_NO_CONFIG) {
			@Override
			protected void run() throws Exception {
				mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, null,
						new MarketDataSubscriber("ABC") {

							@Override
							public void receiveData(Object inData) {
							}
						});
			}
		};
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_RECEIVER_NO_SUBSCRIBER) {
			@Override
			protected void run() throws Exception {
				mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, new IConfigurationProvider() {

							@Override
							public ModuleURN getMarketDataSourceModule() {
								return null;
							}
						}, null);
			}
		};
	}
	
	

	@Test
	public void testStart() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_RECEIVER_NO_SOURCE) {
			@Override
			protected void run() throws Exception {
				ModuleURN receiver = mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, new IConfigurationProvider() {

							@Override
							public ModuleURN getMarketDataSourceModule() {
								return null;
							}
						}, new MarketDataSubscriber("ABC") {

							@Override
							public void receiveData(Object inData) {
							}
						});
				mModuleManager.start(receiver);
			}
		};
	}

	@Test
	public void testDataFlow() throws Exception {
		final Object[] received = new Object[1];
		ModuleURN receiver = mModuleManager.createModule(
				MarketDataReceiverFactory.PROVIDER_URN,
				new IConfigurationProvider() {

					@Override
					public ModuleURN getMarketDataSourceModule() {
						return MockMarketDataModuleFactory.INSTANCE_URN;
					}
				}, new MarketDataSubscriber("ABC") {

					@Override
					public void receiveData(Object inData) {
						received[0] = inData;
					}
				});
		mModuleManager.start(receiver);
		Object data = new Object();
		MockMarketDataModuleFactory.sInstance.emitData(data);
		assertEquals("Data not received", data, received[0]);
	}

}

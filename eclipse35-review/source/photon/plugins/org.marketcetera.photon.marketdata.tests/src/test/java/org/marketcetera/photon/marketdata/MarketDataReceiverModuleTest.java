package org.marketcetera.photon.marketdata;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.marketdata.IMarketDataSubscriber;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverFactory;
import org.marketcetera.photon.internal.marketdata.Messages;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.except.I18NException;

/* $License$ */

/**
 * Test for {@link MarketDataReceiverModule}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketDataReceiverModuleTest {

	private ModuleManager mModuleManager;

	@Before
	public void setUp() throws Exception {
		mModuleManager = ModuleSupport.getModuleManager();
		mModuleManager.start(MockMarketDataModuleFactory.INSTANCE_URN);
	}

	@After
	public void tearDown() throws Exception {
		mModuleManager.stop(MockMarketDataModuleFactory.INSTANCE_URN);
	}

	@Test
	public void testCreation() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_RECEIVER_NO_SUBSCRIBER) {
			@Override
			protected void run() throws Exception {
				mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, (IMarketDataSubscriber) null);
			}
		};
	}
	
	

	@Test
	public void testStart() throws Exception {
		new ExpectedFailure<I18NException>(Messages.MARKET_DATA_RECEIVER_NO_REQUEST) {
			@Override
			protected void run() throws Exception {
				ModuleURN receiver = mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, new IMarketDataSubscriber() {

							@Override
							public void receiveData(Object inData) {
							}

							@Override
							public MarketDataRequest getRequest() {
								return null;
							}

							@Override
							public ModuleURN getSourceModule() {
								return new ModuleURN("abc:abc:abc:abc");
							}
						});
				mModuleManager.start(receiver);
			}
		};
		new ExpectedFailure<I18NException>(Messages.MARKET_DATA_RECEIVER_NO_SOURCE) {
			@Override
			protected void run() throws Exception {
				ModuleURN receiver = mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN, new IMarketDataSubscriber() {

							@Override
							public void receiveData(Object inData) {
							}

							@Override
							public MarketDataRequest getRequest() {
								return MarketDataRequest.newRequest();
							}

							@Override
							public ModuleURN getSourceModule() {
								return null;
							}
						});
				mModuleManager.start(receiver);
			}
		};
	}

	@Test
	public void testDataFlow() throws Exception {
		final Object[] received = new Object[1];
		ModuleURN receiver = mModuleManager.createModule(MarketDataReceiverFactory.PROVIDER_URN,
				new IMarketDataSubscriber() {

					@Override
					public void receiveData(Object inData) {
						received[0] = inData;
					}

					@Override
					public MarketDataRequest getRequest() {
						return MarketDataRequest.newRequest();
					}

					@Override
					public ModuleURN getSourceModule() {
						return MockMarketDataModuleFactory.INSTANCE_URN;
					}
				});
		mModuleManager.start(receiver);
		Object data = new Object();
		MockMarketDataModuleFactory.sInstance.emitData(data);
		assertEquals("Data not received", data, received[0]);
		mModuleManager.stop(receiver);
		mModuleManager.deleteModule(receiver);
	}
}
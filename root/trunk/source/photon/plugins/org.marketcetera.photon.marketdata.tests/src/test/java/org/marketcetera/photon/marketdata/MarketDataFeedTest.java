package org.marketcetera.photon.marketdata;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ProviderNotFoundException;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.photon.internal.marketdata.Messages;
import org.marketcetera.photon.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.except.I18NException;

public class MarketDataFeedTest {

	@Test
	public void nullURN() throws Exception {
		// not expecting a particular message since it is thrown from infrastructure
		new ExpectedFailure<InvalidURNException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(null);
			}
		};
	}

	@Test
	public void gargabeURN() throws Exception {
		// not expecting a particular message since it is thrown from infrastructure
		new ExpectedFailure<InvalidURNException>(null) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(new ModuleURN("abc"));
			}
		};
	}

	@Test
	public void notMarketData() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_FEED_INVALID_PROVIDER_TYPE) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(new ModuleURN("metc:nothing:nothing"));
			}
		};
	}

	@Test
	public void noSuchModule() throws Exception {
		new ExpectedFailure<ProviderNotFoundException>(
				null) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(new ModuleURN("metc:mdata:nothing"));
			}
		};
	}

	@Test
	public void notSingleton() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_FEED_NOT_SINGLETON) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(NotSingletonFactory.PROVIDER_URN);
			}
		};
	}

	@Test
	public void notEmitter() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_FEED_NOT_EMITTER) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(NotEmitterFactory.PROVIDER_URN);
			}
		};
	}

	@Test
	public void incompleteInterface() throws Exception {
		new ExpectedFailure<I18NException>(
				Messages.MARKET_DATA_FEED_INVALID_INTERFACE) {
			@Override
			protected void run() throws Exception {
				new MarketDataFeed(PartialInterfaceFactory.PROVIDER_URN);
			}
		};
	}

	@Test
	public void testGetters() throws Exception {
		ModuleURN providerURN = MockMarketDataModuleFactory.PROVIDER_URN;
		ModuleURN instanceURN = MockMarketDataModuleFactory.INSTANCE_URN;
		MarketDataFeed fixture = new MarketDataFeed(providerURN);
		assertEquals(instanceURN, fixture.getURN());
		assertEquals(providerURN.toString(), fixture.getId());
		assertEquals(ModuleSupport.getModuleManager()
				.getProviderInfo(providerURN).getDescription(), fixture
				.getName());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testReconnectUnsupported() throws Exception {
		MarketDataFeed fixture = new MarketDataFeed(ReconnectUnsupportedFactory.PROVIDER_URN);
		fixture.reconnect();
	}
	
	@Test
	public void testFeedStatus() throws Exception {
		ModuleURN providerURN = new ModuleURN("metc:mdata:mock");
		MarketDataFeed fixture = new MarketDataFeed(providerURN);
		MockMarketDataModuleFactory.sInstance.setStatus("%^.");
		assertEquals(FeedStatus.UNKNOWN, fixture.getStatus());
		MockMarketDataModuleFactory.sInstance.setStatus("AVAILABLE");
		assertEquals(FeedStatus.AVAILABLE, fixture.getStatus());
		MockMarketDataModuleFactory.sInstance.setStatus(null);
	}
	
	@Test
	public void testFeedCapabilities() throws Exception {
		ModuleURN providerURN = new ModuleURN("metc:mdata:mock");
		MarketDataFeed fixture = new MarketDataFeed(providerURN);
		assertThat(fixture.getCapabilities(), is(MockMarketDataModuleFactory.sInstance.getCapabilities()));
	}
	
	@Test
	public void testFeedNotifications() throws Exception {
		ModuleURN providerURN = new ModuleURN("metc:mdata:mock");
		final MarketDataFeed fixture = new MarketDataFeed(providerURN);
		final Exception[] result = new Exception[1];
		fixture.addFeedStatusChangedListener(new MarketDataFeed.IFeedStatusChangedListener() {
		
			@Override
			public void feedStatusChanged(FeedStatusEvent event) {
				try {
					assertTrue("Wrong event source", event.getSource() == fixture);
					assertEquals(FeedStatus.OFFLINE, event.getOldStatus());
					assertEquals(FeedStatus.AVAILABLE, event.getNewStatus());
				} catch (Exception e) {
					result[0] = e;
				}
		
			}
		});
		MockMarketDataModuleFactory.sInstance.fireNotification("OFFLINE", "AVAILABLE");
		if (result[0] != null) {
			throw result[1];
		}
	}
	
	public static class NotEmitterFactory extends ModuleFactory {

		static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:invalid");
		static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");

		public NotEmitterFactory() {
			super(PROVIDER_URN, new MockI18NMessage("Invalid Feed"), false, false);
		}

		@Override
		public Module create(Object... inParameters) throws ModuleCreationException {
			return new DummyModule(INSTANCE_URN, false);
		}
	}
	
	public static class NotSingletonFactory extends ModuleFactory {

		static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:invalid2");
		static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");

		public NotSingletonFactory() {
			super(PROVIDER_URN, new MockI18NMessage("Invalid Feed 2"), true, false);
		}

		@Override
		public Module create(Object... inParameters) throws ModuleCreationException {
			return new DummyModule(INSTANCE_URN, false);
		}
	}
	
	public static class ReconnectUnsupportedFactory extends ModuleFactory {

		static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:invalid3");
		static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");

		public ReconnectUnsupportedFactory() {
			super(PROVIDER_URN, new MockI18NMessage("Invalid Feed 3"), false, false);
		}

		@Override
		public Module create(Object... inParameters) throws ModuleCreationException {
			return new MockMarketDataModuleFactory.MockMarketDataModule(INSTANCE_URN) {
				@Override
				public void reconnect() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
	
	public static class PartialInterfaceFactory extends ModuleFactory {

		static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:invalid4");
		static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");

		public PartialInterfaceFactory() {
			super(PROVIDER_URN, new MockI18NMessage("Invalid Feed 4"), false, false);
		}

		@Override
		public Module create(Object... inParameters) throws ModuleCreationException {
			return new PartialModule(INSTANCE_URN, false);
		}
		
		private static class PartialModule extends DummyModule implements DataEmitter {

			protected PartialModule(ModuleURN inURN, boolean inAutoStart) {
				super(inURN, inAutoStart);
			}

			@Override
			public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
			}

			@Override
			public void requestData(DataRequest inRequest,
					DataEmitterSupport inSupport)
					throws UnsupportedRequestParameterType,
					IllegalRequestParameterValue {
			}
			
		}
	}

}

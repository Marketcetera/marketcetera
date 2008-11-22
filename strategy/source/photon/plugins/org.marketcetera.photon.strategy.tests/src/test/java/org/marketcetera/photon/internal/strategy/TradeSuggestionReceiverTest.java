package org.marketcetera.photon.internal.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ProviderInfo;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.photon.module.ModulePlugin;
import org.marketcetera.trade.Factory;

public class TradeSuggestionReceiverTest {

	private ModuleManager mManager;

	@Test
	public void setUp() throws Exception {
		ModulePlugin plugin = ModulePlugin.getDefault();
		if (plugin != null) {
			mManager = plugin.getModuleManager();
		}
		if (mManager == null) {
			mManager = new ModuleManager();
			mManager.init();
		}
		ProviderInfo info = mManager
				.getProviderInfo(TradeSuggestionReceiverFactory.PROVIDER_URN);
		assertEquals(Messages.TRADE_SUGGESTION_RECEIVER_DESCRIPTION.getText(),
				info.getDescription());
		assertTrue(info.getParameterTypeNames().isEmpty());
		ModuleInfo minfo = mManager
				.getModuleInfo(TradeSuggestionReceiverFactory.INSTANCE_URN);
		assertTrue(minfo.getState().isStarted());
	}

	@Test
	public void testReceiveData() throws Exception {
		new ExpectedFailure<UnsupportedDataTypeException>(
				Messages.TRADE_SUGGESTION_RECEIVER_INVALID_DATA_TYPE,
				Object.class) {
			@Override
			protected void run() throws Exception {
				new TradeSuggestionReceiverModule(
						TradeSuggestionReceiverFactory.INSTANCE_URN)
						.receiveData(new DataFlowID("1"), new Object());
			}

		};
		new ExpectedFailure<UnsupportedDataTypeException>(
				Messages.TRADE_SUGGESTION_RECEIVER_INVALID_DATA_NO_ORDER) {
			@Override
			protected void run() throws Exception {
				new TradeSuggestionReceiverModule(
						TradeSuggestionReceiverFactory.INSTANCE_URN)
						.receiveData(new DataFlowID("1"), Factory.getInstance().createOrderSingleSuggestion());
			}

		};
	}

}

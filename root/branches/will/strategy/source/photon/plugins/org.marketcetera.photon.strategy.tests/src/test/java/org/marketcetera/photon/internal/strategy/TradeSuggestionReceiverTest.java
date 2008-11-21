package org.marketcetera.photon.internal.strategy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ProviderInfo;

public class TradeSuggestionReceiverTest {

	private ModuleManager mManager;

	@Before
	public void setUp() throws Exception {
		mManager = new ModuleManager();
		mManager.init();
		ProviderInfo info = mManager.getProviderInfo(TradeSuggestionReceiverFactory.PROVIDER_URN);
		assertEquals(Messages.TRADE_SUGGESTION_RECEIVER_DESCRIPTION.getText(), info.getDescription());
		assertTrue(info.getParameterTypeNames().isEmpty());
		ModuleInfo minfo = mManager.getModuleInfo(TradeSuggestionReceiverFactory.INSTANCE_URN);
		assertTrue(minfo.getState().isStarted());
	}
	
	@Test
	public void testReceiveData() {
		fail("Not yet implemented");
	}

}

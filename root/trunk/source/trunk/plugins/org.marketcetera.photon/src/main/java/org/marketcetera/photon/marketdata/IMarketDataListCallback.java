package org.marketcetera.photon.marketdata;

import java.util.List;

import org.marketcetera.core.MSymbol;

import quickfix.Message;

public interface IMarketDataListCallback {
	void onMarketDataListAvailable(List<Message> messages);
	
	void onMarketDataFailure(MSymbol symbol);
}

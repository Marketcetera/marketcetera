package org.marketcetera.photon.views;

import org.marketcetera.trade.Instrument;

public interface IMSymbolListener {
	void onAssertSymbol(Instrument instrument);
	
	boolean isListeningSymbol(Instrument instrument);
}

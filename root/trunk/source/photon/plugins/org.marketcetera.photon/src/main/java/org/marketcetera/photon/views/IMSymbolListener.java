package org.marketcetera.photon.views;

import org.marketcetera.trade.Equity;

public interface IMSymbolListener {
	void onAssertSymbol(Equity symbol);
	
	boolean isListeningSymbol(Equity symbol);
}

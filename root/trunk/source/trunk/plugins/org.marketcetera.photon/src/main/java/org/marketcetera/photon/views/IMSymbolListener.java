package org.marketcetera.photon.views;

import org.marketcetera.core.MSymbol;

public interface IMSymbolListener {
	void onAssertSymbol(MSymbol symbol);
	
	boolean isListeningSymbol(MSymbol symbol);
}

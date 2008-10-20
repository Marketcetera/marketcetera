package org.marketcetera.photon.marketdata;

import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.IMarketDataListener;

public interface IMarketDataListCallback extends IMarketDataListener {
	
	void onMarketDataFailure(MSymbol symbol);
}

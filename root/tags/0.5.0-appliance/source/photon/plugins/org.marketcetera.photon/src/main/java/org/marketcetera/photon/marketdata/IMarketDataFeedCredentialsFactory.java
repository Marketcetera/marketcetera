package org.marketcetera.photon.marketdata;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.IMarketDataFeedCredentials;

public interface IMarketDataFeedCredentialsFactory<T extends IMarketDataFeedCredentials> {

	T getCredentials(ScopedPreferenceStore store) throws FeedException;
	
}

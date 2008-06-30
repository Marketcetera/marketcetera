package org.marketcetera.photon.marketdata;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.IMarketDataFeedCredentials;

public abstract class AbstractMarketDataFeedPreferencePage<C extends IMarketDataFeedCredentials> extends FieldEditorPreferencePage{

	private static final Map<String, IMarketDataFeedCredentials> credentialsMap = new HashMap<String, IMarketDataFeedCredentials>();

	public abstract C getCredentials() throws FeedException;

	public abstract String getFeedID();
	
	@Override
	public boolean performOk() {
		boolean returnVal = super.performOk();

		try {
			C credentials = getCredentials();
			credentialsMap.put(getFeedID(), credentials);
		} catch (FeedException e) {
			return false;
		}
		return returnVal;
	}


	public static IMarketDataFeedCredentials getCredentialsForFeed(String feedID){
		return credentialsMap.get(feedID);
	}
}

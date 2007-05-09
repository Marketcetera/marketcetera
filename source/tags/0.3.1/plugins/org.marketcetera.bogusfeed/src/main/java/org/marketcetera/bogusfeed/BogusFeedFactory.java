package org.marketcetera.bogusfeed;

import java.util.Map;

import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedFactory;

public class BogusFeedFactory implements IMarketDataFeedFactory {

	public IMarketDataFeed getInstance(String url, String userName, String passwordString, Map<String,Object> properties) {
		return new BogusFeed();
	}

	public String[] getAllowedPropertyKeys() {
		return new String[0];
	}

}

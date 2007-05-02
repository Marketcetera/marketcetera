package org.marketcetera.marketdata;

import java.util.Map;

import org.marketcetera.core.MarketceteraException;

public class MarketceteraFeedFactory implements IMarketDataFeedFactory {

	public IMarketDataFeed getInstance(String url, String userName,
			String password, Map<String, Object> properties) throws MarketceteraException {
		return new MarketceteraFeed(url, userName, password, properties);
	}

	public String[] getAllowedPropertyKeys() {
		return new String [] {MarketceteraFeed.SETTING_SENDER_COMP_ID, MarketceteraFeed.SETTING_TARGET_COMP_ID};
	}
	

}

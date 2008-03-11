package org.marketcetera.marketdata;

import java.util.Map;

import org.apache.log4j.Logger;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoOpLogger;

public class MarketceteraFeedFactory implements IMarketDataFeedFactory {

	public IMarketDataFeed getInstance(String url, String userName,
			String password, Map<String, Object> properties) throws MarketceteraException {
		return new MarketceteraFeed(url, userName, password, properties, new NoOpLogger(MarketceteraFeed.class.toString()));
	}

	public IMarketDataFeed getInstance(String url, String userName,
			String password, Map<String, Object> properties, Logger logger) throws MarketceteraException {
		return new MarketceteraFeed(url, userName, password, properties, logger);
	}

	public String[] getAllowedPropertyKeys() {
		return new String [] {MarketceteraFeed.SETTING_SENDER_COMP_ID, MarketceteraFeed.SETTING_TARGET_COMP_ID};
	}

	public String getProviderName() {
		return "Marketcetera";
	}

	public IMarketDataFeed getMarketDataFeed(MarketDataFeedCredentials inCredentials) throws FeedException {
		return null;
	}

}

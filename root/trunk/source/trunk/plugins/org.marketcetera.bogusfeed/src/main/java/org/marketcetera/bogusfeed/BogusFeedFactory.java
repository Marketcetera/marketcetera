package org.marketcetera.bogusfeed;

import java.util.Map;

import org.apache.log4j.Logger;
import org.marketcetera.core.NoOpLogger;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedFactory;
import org.marketcetera.marketdata.MarketDataFeedCredentials;

public class BogusFeedFactory implements IMarketDataFeedFactory {

	public IMarketDataFeed getInstance(String url, String userName, String passwordString, Map<String,Object> properties) {
		return getInstance(url, userName, passwordString, properties, new NoOpLogger("BogusFeed"));
	}
	public IMarketDataFeed getInstance(String url, String userName, String passwordString, Map<String,Object> properties, Logger logger) {
		return new BogusFeed();
	}

	public String[] getAllowedPropertyKeys() {
		return new String[0];
	}
	
	public String getProviderName(){
		return "Marketcetera (Bogus)";
	}
	public IMarketDataFeed getMarketDataFeed(MarketDataFeedCredentials inCredentials) throws FeedException {
		return new BogusFeed();
	}
}

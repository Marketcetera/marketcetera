package org.marketcetera.marketdata;

import java.util.Map;

import org.apache.log4j.Logger;
import org.marketcetera.core.MarketceteraException;

public interface IMarketDataFeedFactory {
	public IMarketDataFeed getInstance(String url, String userName, String password, Map<String, Object> properties) throws MarketceteraException;
	public IMarketDataFeed getInstance(String url, String userName, String password, Map<String, Object> properties, Logger logger) throws MarketceteraException;

	public String [] getAllowedPropertyKeys();
}

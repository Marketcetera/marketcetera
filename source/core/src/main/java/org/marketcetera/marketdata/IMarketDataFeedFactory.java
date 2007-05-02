package org.marketcetera.marketdata;

import java.util.Map;

import org.marketcetera.core.MarketceteraException;

public interface IMarketDataFeedFactory {
	public IMarketDataFeed getInstance(String url, String userName, String password, Map<String, Object> properties) throws MarketceteraException;

	public String [] getAllowedPropertyKeys();
}

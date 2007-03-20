package org.marketcetera.marketdata;

import java.util.Map;

public interface IMarketDataFeedFactory {
	public IMarketDataFeed getInstance(String url, String userName, String password);
}

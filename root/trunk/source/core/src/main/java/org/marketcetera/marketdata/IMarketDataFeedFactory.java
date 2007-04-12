package org.marketcetera.marketdata;

public interface IMarketDataFeedFactory {
	public IMarketDataFeed getInstance(String url, String userName, String password);
}

package org.marketcetera.quotefeed;

public interface IQuoteFeedFactory {
	public IQuoteFeed getInstance(String url, String userName, String password);
}

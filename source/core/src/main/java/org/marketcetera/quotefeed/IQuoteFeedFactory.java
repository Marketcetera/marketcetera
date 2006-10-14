package org.marketcetera.quotefeed;

import java.util.Map;

public interface IQuoteFeedFactory {
	public IQuoteFeed getInstance(String url, String userName, String password);
}

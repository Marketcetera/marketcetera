package org.marketcetera.bogusfeed;

import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedFactory;

public class BogusFeedFactory implements IMarketDataFeedFactory {

	public IMarketDataFeed getInstance(String url, String userName, String password) {
		return new BogusFeed();
	}

}

package org.marketcetera.bogusfeed;

import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;

public class BogusQuoteFeedFactory implements IQuoteFeedFactory {

	public IQuoteFeed getInstance(String url, String userName, String password) {
		return new BogusFeed();
	}

}

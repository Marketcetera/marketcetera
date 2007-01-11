package org.marketcetera.photon.quotefeed;

import org.marketcetera.core.IFeedComponent;
import org.marketcetera.photon.DelegatingFeedComponentAdapter;
import org.marketcetera.quotefeed.IQuoteFeed;

public class QuoteFeedService extends DelegatingFeedComponentAdapter {
	IQuoteFeed quoteFeed;

	public IQuoteFeed getQuoteFeed() {
		return quoteFeed;
	}

	public void setQuoteFeed(IQuoteFeed quoteFeed) {
		this.quoteFeed = quoteFeed;
	}

	@Override
	public IFeedComponent getDelegateFeedComponent() {
		return quoteFeed;
	}



}

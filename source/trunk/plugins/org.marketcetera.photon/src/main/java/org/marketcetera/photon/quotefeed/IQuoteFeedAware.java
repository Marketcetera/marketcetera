package org.marketcetera.photon.quotefeed;

import org.marketcetera.quotefeed.IQuoteFeed;

public interface IQuoteFeedAware {

	public void setQuoteFeedAdapter(QuoteFeedComponentAdapter feed);
}

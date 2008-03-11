package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IFeedComponentListener;

@ClassVersion("$Id")
public interface IFeedComponent {

	public enum FeedType {
		LIVE, DELAYED, SIMULATED, UNKNOWN
	}

    public FeedType getFeedType();

    public FeedStatus getFeedStatus();

    public String getID();

    public void addFeedComponentListener(IFeedComponentListener listener);

    public void removeFeedComponentListener(IFeedComponentListener listener);

}
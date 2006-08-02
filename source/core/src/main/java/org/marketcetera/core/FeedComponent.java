package org.marketcetera.core;

@ClassVersion("$Id")
public interface FeedComponent {

	public enum FeedType {
		LIVE, DELAYED, SIMULATED, UNKNOWN
	}

    public enum FeedStatus {
        OFFLINE, ERROR, AVAILABLE, UNKNOWN
    }

    public FeedType getFeedType();

    public FeedStatus getFeedStatus();

    public String getID();

    public void addFeedComponentListener(IFeedComponentListener listener);

    public void removeFeedComponentListener(IFeedComponentListener listener);

}
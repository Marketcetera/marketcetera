package org.marketcetera.marketdata;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.util.misc.ClassVersion;

@ClassVersion("$Id: IFeedComponent.java 16063 2012-01-31 18:21:55Z colin $")
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

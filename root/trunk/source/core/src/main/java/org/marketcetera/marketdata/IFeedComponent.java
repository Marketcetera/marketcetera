package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id")
public interface IFeedComponent 
{
	public enum FeedType 
    {
		LIVE, DELAYED, SIMULATED, UNKNOWN
	}

    public FeedType getFeedType();

    public FeedStatus getFeedStatus();
}
package org.marketcetera.marketdata;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;


/**
 * An abstract base class for all market data feeds. Contains logic common to all market data feed impls with 
 * the mechanics of adding/removing symbol/feed component listeners and keeping track of the feed 
 * status.
 *
 * @author andrei@lissovski.org
 * @author gmiller
 */
public abstract class MarketDataFeedBase implements IMarketDataFeed {

	protected FeedStatus feedStatus;
	private List<IFeedComponentListener> feedComponentListeners = new LinkedList<IFeedComponentListener>();
	private IMarketDataListener marketDataListener;


	public void addFeedComponentListener(IFeedComponentListener arg0) {
		feedComponentListeners.add(arg0);
	}

	public FeedStatus getFeedStatus() {
		return feedStatus;
	}

	public void removeFeedComponentListener(IFeedComponentListener arg0) {
		feedComponentListeners.remove(arg0);
	}

	public void fireFeedStatusChanged() {
		for (IFeedComponentListener listener: feedComponentListeners) {
			listener.feedComponentChanged(this);
		}
	}


	protected void setFeedStatus(FeedStatus status) {
		feedStatus = status;
		fireFeedStatusChanged();
	}

	public IMarketDataListener getMarketDataListener() {
		return marketDataListener;
	}

	public void setMarketDataListener(IMarketDataListener listener) {
		marketDataListener = listener;
	}



}

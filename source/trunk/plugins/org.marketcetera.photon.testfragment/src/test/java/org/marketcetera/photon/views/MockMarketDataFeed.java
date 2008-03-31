package org.marketcetera.photon.views;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.osgi.framework.BundleContext;

import quickfix.Message;

public class MockMarketDataFeed implements IMarketDataFeed {
	private IMarketDataListener marketDataListener;

	public void addFeedComponentListener(IFeedComponentListener listener) {
	}

	public FeedStatus getFeedStatus() {
		return FeedStatus.AVAILABLE;
	}

	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	public String getID() {
		return "";
	}

	public void removeFeedComponentListener(IFeedComponentListener listener) {
	}

	public boolean isRunning() {
		return true;
	}

	public void start() {
	}

	public void stop() {
	}

	public IMarketDataListener getMarketDataListener() {
		return marketDataListener;
	}

	public void setMarketDataListener(IMarketDataListener listener) {
		marketDataListener = listener;
	}

	public ISubscription subscribe(Message subscription) {
		return null;
	}

	public MSymbol symbolFromString(String symbolString) {
		return null;
	}

	public boolean unsubscribe(ISubscription selector) {
		return false;
	}

	public void sendMessage(Message aMessage) {
		if(marketDataListener != null) {
			marketDataListener.onMessage(aMessage);
		}
	}

	public ISubscription asyncQuery(Message query) throws MarketceteraException {
		return null;
	}

	public void asyncUnsubscribe(ISubscription subscription)
			throws MarketceteraException {
	}

	public List<Message> syncQuery(Message query, long timeout, TimeUnit units)
			throws MarketceteraException, TimeoutException {
		return null;
	}

	public static MarketDataFeedService registerMockMarketDataFeed() {
		BundleContext bundleContext = PhotonPlugin.getDefault()
				.getBundleContext();
		MarketDataFeedService feedService = new MarketDataFeedService(
				new MockMarketDataFeed());
		bundleContext.registerService(MarketDataFeedService.class.getName(),
				feedService, null);
		return feedService;
	}

	public static MockMarketDataFeed getMockMarketDataFeed(
			MarketDataFeedService feedService) {
		IMarketDataFeed feed = feedService.getMarketDataFeed();
		if (!(feed instanceof MockMarketDataFeed)) {
			TestCase.fail("Feed was not a " + MockMarketDataFeed.class
					+ " but was: " + (feed != null ? feed.getClass() : null)
					+ "   Service was: " + feedService);
		}
		return (MockMarketDataFeed) feed;
	}
}

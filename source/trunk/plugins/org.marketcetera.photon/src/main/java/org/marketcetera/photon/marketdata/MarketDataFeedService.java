package org.marketcetera.photon.marketdata;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.core.IFeedComponent.FeedType;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.IMessageSelector;

import quickfix.Message;

public class MarketDataFeedService {
	IMarketDataFeed feed;
	private DelegatingListener delegatingListener;

	public MarketDataFeedService(IMarketDataFeed aFeed){
		feed = aFeed;
		delegatingListener = new DelegatingListener();
		feed.setMarketDataListener(delegatingListener);
	}

	public final void addFeedComponentListener(IFeedComponentListener listener) {
		feed.addFeedComponentListener(listener);
	}

	public final FeedStatus getFeedStatus() {
		return feed.getFeedStatus();
	}

	public final FeedType getFeedType() {
		return feed.getFeedType();
	}

	public final String getID() {
		return feed.getID();
	}

	public final boolean isRunning() {
		return feed.isRunning();
	}

	public final void removeFeedComponentListener(IFeedComponentListener listener) {
		feed.removeFeedComponentListener(listener);
	}

	public final void start() {
		feed.start();
	}

	public final void stop() {
		feed.stop();
	}

	public final void subscribe(IMessageSelector selector) {
		feed.subscribe(selector);
	}

	public final MSymbol symbolFromString(String symbolString) {
		return feed.symbolFromString(symbolString);
	}

	public final boolean unsubscribe(IMessageSelector selector) {
		return feed.unsubscribe(selector);
	}
	
	public final IMarketDataFeed getMarketDataFeed()
	{
		return feed;
	}
	
	public void addMarketDataListener(IMarketDataListener listener)
	{
		if (listener == null)
			throw new NullPointerException();
		delegatingListener.addMarketDataListener(listener);
	}
	
	public void removeMarketDataListener(IMarketDataListener listener)
	{
		if (listener == null)
			throw new NullPointerException();
		delegatingListener.removeMarketDataListener(listener);
	}

	class DelegatingListener implements IMarketDataListener {

		List<IMarketDataListener> delegatedListeners = new LinkedList<IMarketDataListener>();
		public void onMessage(Message aMessage) {
			synchronized (delegatedListeners){
				for (IMarketDataListener aListener : delegatedListeners) {
					aListener.onMessage(aMessage);
				}
			}
		}
		public void onMessages(Message[] messages) {
			synchronized (delegatedListeners){
				for (IMarketDataListener aListener : delegatedListeners) {
					aListener.onMessages(messages);
				}
			}
		}

		public void removeMarketDataListener(IMarketDataListener listener) {
			synchronized (delegatedListeners) {
				delegatedListeners.remove(listener);
			}
		}
		public void addMarketDataListener(IMarketDataListener listener) {
			synchronized (delegatedListeners) {
				delegatedListeners.add(listener);
			}
		}

		public void onLevel2Quote(Message aQuote) {}
		public void onLevel2Quotes(Message[] quotes) {}
		public void onQuote(Message aQuote) {}
		public void onQuotes(Message[] messages) {}
		public void onTrade(Message aTrade) {}
		public void onTrades(Message[] trades) {}
		
	}

	

}

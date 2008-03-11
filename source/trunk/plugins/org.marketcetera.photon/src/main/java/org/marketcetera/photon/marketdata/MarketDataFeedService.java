package org.marketcetera.photon.marketdata;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.ISubscription;
import org.osgi.framework.ServiceRegistration;

import quickfix.Message;

public class MarketDataFeedService implements IFeedComponentListener, IFeedComponent {
	IMarketDataFeed feed;
	private DelegatingListener delegatingListener;
	private ServiceRegistration serviceRegistration;

	public MarketDataFeedService(IMarketDataFeed aFeed){
		feed = aFeed;
		delegatingListener = new DelegatingListener();
		feed.setMarketDataListener(delegatingListener);
	}

	public final void addFeedComponentListener(IFeedComponentListener listener) {
		feed.addFeedComponentListener(listener);
	}

	public final void removeFeedComponentListener(IFeedComponentListener listener) {
		feed.removeFeedComponentListener(listener);
	}

	public final FeedStatus getFeedStatus() {
		if (feed == null)
			return FeedStatus.UNKNOWN;
		else
			return feed.getFeedStatus();
	}

	public final FeedType getFeedType() {
		if (feed == null)
			return FeedType.UNKNOWN;
		else
			return feed.getFeedType();
	}

	public final String getID() {
		if (feed == null)
			return "";
		else
			return feed.getID();
	}

	public final boolean isRunning() {
		return feed.isRunning();
	}


	public final void start() {
		feed.start();
	}

	public final void stop() {
		feed.stop();
	}

	public final ISubscription subscribe(Message subscriptionMessage) throws MarketceteraException {
		return feed.asyncQuery(subscriptionMessage);
	}

	public final MSymbol symbolFromString(String symbolString) {
		return feed.symbolFromString(symbolString);
	}

	public final void unsubscribe(ISubscription subscription) throws MarketceteraException {
		if (subscription != null){
			feed.asyncUnsubscribe(subscription);
		}
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
	
	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.serviceRegistration = serviceRegistration;
	}

	public ServiceRegistration getServiceRegistration() {
		return serviceRegistration;
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



	
	public void afterPropertiesSet() throws Exception {
		feed.addFeedComponentListener(this);
	}

	public void feedComponentChanged(IFeedComponent component) {
		if (serviceRegistration != null) {
			try {
				serviceRegistration.setProperties(null);
			} catch (IllegalStateException illegalStateEx) {
				// During shutdown the service may already be unregistered.
				serviceRegistration = null;
			}
		}
	}
}

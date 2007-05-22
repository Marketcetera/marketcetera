package org.marketcetera.photon.marketdata;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.Pair;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import quickfix.Message;

public class MarketDataFeedTracker extends ServiceTracker {

	protected HashMap<MSymbol, Pair<ISubscription, Message>> simpleSubscriptions = new HashMap<MSymbol, Pair<ISubscription, Message>>();
	private IMarketDataListener currentListener;
	private HashSet<FeedEventListener> feedEventListeners = new HashSet<FeedEventListener>();
	
	public MarketDataFeedTracker(BundleContext context) {
		super(context, MarketDataFeedService.class.getName(), null);
	}
	
	public ISubscription simpleSubscribe(MSymbol symbol) throws MarketceteraException {
		MarketDataFeedService marketDataFeed = getMarketDataFeedService();
		return simpleSubscribe(symbol, marketDataFeed);
	}
	
	private ISubscription simpleSubscribe(MSymbol symbol, MarketDataFeedService marketDataFeed) throws MarketceteraException {
		Message subscribeMessage = MarketDataUtils.newSubscribeBBO(symbol);
		ISubscription subscription = null;
		if (marketDataFeed != null){
			subscription = marketDataFeed.subscribe(subscribeMessage);
		}
		simpleSubscriptions.put(symbol, new Pair<ISubscription, Message>(subscription, subscribeMessage));
		return subscription;
	}
	
	public void simpleUnsubscribe(MSymbol symbol){
		if (symbol != null){
			MarketDataFeedService marketDataFeed = getMarketDataFeedService();
			if (marketDataFeed != null){
				Pair<ISubscription, Message> pair = simpleSubscriptions.remove(symbol);
				ISubscription subscription = pair.getFirstMember();
				if (pair != null && subscription!=null){
					try {
						marketDataFeed.unsubscribe(subscription);
					} catch (MarketceteraException e) {
						PhotonPlugin.getMainConsoleLogger().warn("Error unsubscribing to updates for "+symbol);
					}
				}
			}
		}
	}

	@Override
	public Object addingService(ServiceReference reference) {
		Object service = super.addingService(reference);
		if (service instanceof MarketDataFeedService){
			addSubscriptions((MarketDataFeedService)service);
			addListeners((MarketDataFeedService)service);
		}
		conditionallyNotifyFeedEventListeners(service);
		return service;
	}
	
	
	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		super.modifiedService(reference, service);
		conditionallyNotifyFeedEventListeners(service);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		conditionallyNotifyFeedEventListeners(service);
	}
	
	private void conditionallyNotifyFeedEventListeners(Object service) {
		if (service instanceof IFeedComponent){
			IFeedComponent feedComponent = (IFeedComponent)service;
			FeedStatus status = feedComponent.getFeedStatus();
			notifyFeedEventListeners(status);
		}
	}

	public MarketDataFeedService getMarketDataFeedService()
	{
		return (MarketDataFeedService) getService();
	}


	public void setMarketDataListener(IMarketDataListener listener) {
		MarketDataFeedService service = getMarketDataFeedService();
		if (service != null){
			if (currentListener != null){
				service.removeMarketDataListener(currentListener);
			}
			if (listener != null){
				service.addMarketDataListener(listener);
			}
		}
		currentListener = listener;
	}


	private void addSubscriptions(MarketDataFeedService feed) {
		for (MSymbol symbol : simpleSubscriptions.keySet()) {
			try {
				simpleSubscribe(symbol, feed);
			} catch (MarketceteraException e) {
				PhotonPlugin.getMainConsoleLogger().warn("Error subscribing to updates for "+symbol);
			}
		}
	}

	private void addListeners(MarketDataFeedService service) {
		if (currentListener != null)
			service.addMarketDataListener(currentListener);
	}


	@Override
	public synchronized void close() {
		setMarketDataListener(null);
		super.close();
	}

	public enum FeedEventType {
		FEED_ADDED, FEED_REMOVED
	};

	public interface FeedEventListener {
		void handleEvent(FeedStatus event);
	}

	public void addFeedEventListener(FeedEventListener listener) {
		feedEventListeners.add(listener);
	}

	public void removeFeedEventListener(FeedEventListener listener) {
		feedEventListeners.remove(listener);
	}

	private void notifyFeedEventListeners(final FeedStatus status) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			notifyFeedEventListenersImpl(status);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					notifyFeedEventListenersImpl(status);
				}
			});
		}
	}

	private void notifyFeedEventListenersImpl(FeedStatus status) {
		for (FeedEventListener listener : feedEventListeners) {
			if (listener != null) {
				try {
					listener.handleEvent(status);
				} catch (Exception anyException) {
					PhotonPlugin.getMainConsoleLogger().warn(anyException);
				}
			}
		}
	}
}

package org.marketcetera.photon.marketdata;

import java.util.HashSet;

import org.eclipse.swt.widgets.Display;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class MarketDataFeedTracker extends ServiceTracker {

	private HashSet<FeedEventListener> feedEventListeners = new HashSet<FeedEventListener>();
	
	public MarketDataFeedTracker(BundleContext context) {
		super(context, MarketDataFeedService.class.getName(), null);
	}
	

	@Override
	public Object addingService(ServiceReference reference) {
		Object service = super.addingService(reference);
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

package org.marketcetera.photon.marketdata;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedCredentials;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.osgi.framework.ServiceRegistration;

import quickfix.Message;

public class MarketDataFeedService<C extends IMarketDataFeedCredentials> implements IFeedComponentListener, IFeedComponent {
	C credentials;
	IMarketDataFeed<?, C> feed;
	private ServiceRegistration serviceRegistration;

	public MarketDataFeedService(IMarketDataFeed<?,C> aFeed, C credentials){
		feed = aFeed;
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

	public final IMarketDataFeedToken<C> execute(Message message, ISubscriber subscriber) 
		throws FeedException{
		return feed.execute(message, subscriber);
	}

	public final MSymbol symbolFromString(String symbolString) {
		return new MSymbol(symbolString);
	}

	
	public final IMarketDataFeed<?,?> getMarketDataFeed()
	{
		return feed;
	}

	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.serviceRegistration = serviceRegistration;
	}

	public ServiceRegistration getServiceRegistration() {
		return serviceRegistration;
	}


	
	public void afterPropertiesSet() throws Exception {
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

package org.marketcetera.photon.marketdata;

import java.util.HashMap;

import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.IMessageSelector;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class MarketDataFeedTracker extends ServiceTracker {

	protected HashMap<MSymbol, IMessageSelector> simpleSubscriptions = new HashMap<MSymbol, IMessageSelector>();
	private IMarketDataListener currentListener;

	public MarketDataFeedTracker(BundleContext context) {
		super(context, MarketDataFeedService.class.getName(), null);
	}
	
	
	public IMessageSelector simpleSubscribe(MSymbol symbol){
		ConjunctionMessageSelector selector = new ConjunctionMessageSelector(
						new SymbolMessageSelector(symbol),
						new MessageTypeSelector(true, true, false));
		simpleSubscriptions.put(symbol, selector);
		MarketDataFeedService marketDataFeed = getMarketDataFeedService();
		if (marketDataFeed != null){
			marketDataFeed.subscribe(selector);
		}
		return selector;
	}
	
	public void simpleUnsubscribe(MSymbol symbol){
		if (symbol != null){
			MarketDataFeedService marketDataFeed = getMarketDataFeedService();
			if (marketDataFeed != null){
				IMessageSelector sel = simpleSubscriptions.remove(symbol);
				if (sel != null){
					marketDataFeed.unsubscribe(sel);
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
		return service;
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
			simpleSubscribe(symbol);
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

	

}

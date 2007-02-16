package org.marketcetera.photon.quotefeed;

import org.marketcetera.core.IFeedComponent;
import org.marketcetera.photon.DelegatingFeedComponentAdapter;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.osgi.framework.ServiceRegistration;

public class QuoteFeedService extends DelegatingFeedComponentAdapter {
	IQuoteFeed quoteFeed;
	private ServiceRegistration serviceRegistration;

	public IQuoteFeed getQuoteFeed() {
		return quoteFeed;
	}

	public void setQuoteFeed(IQuoteFeed quoteFeed) {
		this.quoteFeed = quoteFeed;
	}

	@Override
	public IFeedComponent getDelegateFeedComponent() {
		return quoteFeed;
	}

	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.serviceRegistration = serviceRegistration;
	}

	@Override
	protected void fireFeedComponentChanged() {
		if (serviceRegistration != null)
			serviceRegistration.setProperties(null);
	}


}

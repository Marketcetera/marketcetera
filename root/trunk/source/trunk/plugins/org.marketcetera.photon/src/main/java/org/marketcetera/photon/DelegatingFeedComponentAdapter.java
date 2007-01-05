package org.marketcetera.photon;

import org.marketcetera.core.IFeedComponent;

public abstract class DelegatingFeedComponentAdapter extends FeedComponentAdapterBase {
	public abstract IFeedComponent getDelegateFeedComponent();

	public void afterPropertiesSet() throws Exception {
		getDelegateFeedComponent().addFeedComponentListener(this);
	}

	public FeedStatus getFeedStatus() {
		IFeedComponent delegateFeedComponent = getDelegateFeedComponent();
		if (delegateFeedComponent != null) {
			return delegateFeedComponent.getFeedStatus();
		} else {
			return getAdapterFeedStatus();
		}
	}

	/**
	 * In cases where there is no delegated feed, return what this
	 * adapter believes its status is.
	 * 
	 * @return The feed status of this adapter
	 */
	protected FeedStatus getAdapterFeedStatus() {
		return FeedStatus.UNKNOWN;
	}

	
	public FeedType getFeedType() {
		IFeedComponent delegateFeedComponent = getDelegateFeedComponent();
		if (delegateFeedComponent != null) {
			return delegateFeedComponent.getFeedType();
		} else {
			return FeedType.UNKNOWN;
		}
	}

	public String getID() {
		IFeedComponent delegateFeedComponent = getDelegateFeedComponent();
		if (delegateFeedComponent != null) {
			return delegateFeedComponent.getID();
		} else {
			return "";
		}
	}

}

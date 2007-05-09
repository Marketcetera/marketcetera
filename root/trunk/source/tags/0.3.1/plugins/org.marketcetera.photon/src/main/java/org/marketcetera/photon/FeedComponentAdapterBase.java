package org.marketcetera.photon;

import java.util.ArrayList;

import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.springframework.beans.factory.InitializingBean;

public abstract class FeedComponentAdapterBase implements IFeedComponent,
		InitializingBean, IFeedComponentListener {
	
	ArrayList<IFeedComponentListener> listeners = new ArrayList<IFeedComponentListener>();
	
	public void addFeedComponentListener(IFeedComponentListener listener) {
		listeners.add(listener);
	}

	public void removeFeedComponentListener(IFeedComponentListener listener) {
		listeners.remove(listener);
	}

	public FeedStatus getFeedStatus() {
		return FeedStatus.UNKNOWN;
	}

	public FeedType getFeedType() {
		return FeedType.UNKNOWN;
	}

	public void feedComponentChanged(IFeedComponent component) {
		fireFeedComponentChanged();
	}

	protected void fireFeedComponentChanged() {
		for (IFeedComponentListener listener : listeners) {
			listener.feedComponentChanged(this);
		}
	}
	
	
}

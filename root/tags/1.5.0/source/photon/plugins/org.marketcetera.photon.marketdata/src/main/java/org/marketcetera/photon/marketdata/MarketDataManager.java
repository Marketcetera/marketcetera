package org.marketcetera.photon.marketdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.marketdata.Activator;
import org.marketcetera.photon.internal.marketdata.MarketData;
import org.marketcetera.photon.internal.marketdata.Messages;
import org.marketcetera.photon.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.marketdata.MarketDataFeed.IFeedStatusChangedListener;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Photon interface to market data services.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataManager {

	private static final String USER_MSG_CATEGORY = org.marketcetera.core.Messages.USER_MSG_CATEGORY;

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static MarketDataManager getCurrent() {
		return Activator.getDefault().getMarketDataManager();
	}

	private final ModuleManager mModuleManager;
	private final Map<String, MarketDataFeed> mFeeds = new HashMap<String, MarketDataFeed>();
	private final ListenerList mActiveFeedListeners = new ListenerList();
	private final AtomicBoolean mReconnecting = new AtomicBoolean(false);

	private volatile MarketDataFeed mActiveFeed;

	private final IFeedStatusChangedListener mFeedStatusChangesListener = new IFeedStatusChangedListener() {
		@Override
		public void feedStatusChanged(final FeedStatusEvent event) {
			if (event.getSource() == mActiveFeed) {
				notifyListeners(event);
			}
		}
	};
	private MarketData mMarketData;

	/**
	 * Constructor.
	 */
	@Inject
	public MarketDataManager(ModuleManager moduleManager, MarketData marketData) {
		mModuleManager = moduleManager;
		mMarketData = marketData;
		List<ModuleURN> providers = mModuleManager.getProviders();
		for (ModuleURN providerURN : providers) {
			if (providerURN.providerType().equals(MarketDataFeed.MARKET_DATA_PROVIDER_TYPE)) {
				try {
					MarketDataFeed feed = new MarketDataFeed(providerURN);
					feed.addFeedStatusChangedListener(mFeedStatusChangesListener);
					mFeeds.put(providerURN.toString(), feed);
				} catch (Exception e) {
					Messages.MARKET_DATA_MANAGER_IGNORING_PROVIDER.warn(this, e, providerURN);
				}
			}
		}
	}

	public IMarketData getMarketData() {
		return mMarketData;
	}

	/**
	 * Returns an unmodifiable list of the registered market data providers.
	 * 
	 * @return an unmodifiable list of the registered market data providers
	 */
	public Collection<MarketDataFeed> getProviders() {
		return Collections.unmodifiableCollection(mFeeds.values());
	}

	/**
	 * Attempts to reconnect to the default active market data feed.
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	public void reconnectFeed() {
		reconnectFeed(getDefaultActiveFeed());
	}

	/**
	 * This is only public for testing purposes. 
	 * TODO: Refactor for better encapsulation
	 */
	public void reconnectFeed(String providerId) {
		if (!mReconnecting.compareAndSet(false, true)) {
			return;
		}
		try {
			synchronized (this) {
				final MarketDataFeed oldFeed = mActiveFeed;
				mActiveFeed = mFeeds.get(providerId);
				if (mActiveFeed == null && oldFeed == null) {
					// mReconnecting is reset in the finally block
					return;
				}
				if (mActiveFeed != null) {
					try {
						if (!mModuleManager.getModuleInfo(mActiveFeed.getURN()).getState()
								.isStarted()) {
							mModuleManager.start(mActiveFeed.getURN());
						} else {
							mActiveFeed.reconnect();
						}
					} catch (ModuleException e) {
						// TODO: May be better to propagate the exception so there can be dialog boxes, etc
						Messages.MARKET_DATA_MANAGER_FEED_START_FAILED.error(USER_MSG_CATEGORY,
								mActiveFeed.getName());
					} catch (UnsupportedOperationException e) {
						// TODO: May be better to propagate the exception so there can be dialog boxes, etc
						Messages.MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED.error(USER_MSG_CATEGORY,
								e, mActiveFeed.getName());
					}
				}
				mMarketData.setSourceFeed(mActiveFeed);
				final FeedStatusEvent event;
				final FeedStatus oldStatus = oldFeed == null ? FeedStatus.OFFLINE : oldFeed
						.getStatus();
				if (mActiveFeed == null) {
					event = oldFeed.createFeedStatusEvent(oldStatus, FeedStatus.OFFLINE);
				} else {
					event = mActiveFeed.createFeedStatusEvent(oldStatus, mActiveFeed.getStatus());
				}
				notifyListeners(event);
			}
		} finally {
			mReconnecting.set(false);
		}
	}

	/**
	 * Adds a listener to the manager that tracks the status of the active feed.
	 * 
	 * @param listener
	 *            to be notified when the active feed status changes
	 */
	public void addActiveFeedStatusChangedListener(IFeedStatusChangedListener listener) {
		mActiveFeedListeners.add(listener);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeActiveFeedStatusChangedListener(IFeedStatusChangedListener listener) {
		mActiveFeedListeners.remove(listener);
	}

	private void notifyListeners(FeedStatusEvent event) {
		Object[] listeners = mActiveFeedListeners.getListeners();
		for (Object object : listeners) {
			((IFeedStatusChangedListener) object).feedStatusChanged(event);
		}
	}

	/**
	 * Returns the default active feed to use (saved in plug-in preferences).
	 * 
	 * @return returns the default active feed
	 */
	private String getDefaultActiveFeed() {
		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
				MarketDataPreferences.DEFAULT_ACTIVE_MARKETDATA_PROVIDER, "", //$NON-NLS-1$
				null);
	}

	/**
	 * Returns the human readable name of the active market data feed. If there is no active feed,
	 * <code>null</code> will be returned.
	 * 
	 * @return the human readable name of the active market data feed or <code>null</code> if none
	 *         exists
	 */
	public String getActiveFeedName() {
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? null : feed.getName();
	}

	/**
	 * Returns the status of the active market data feed. If there is no active feed,
	 * FeedStatus.OFFLINE will be returned.
	 * 
	 * @return the status of the active market data feed or FeedStatus.OFFLINE if none exists
	 */
	public FeedStatus getActiveFeedStatus() {
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? FeedStatus.OFFLINE : feed.getStatus();
	}

    /**
	 * Returns the capabilities supported by the active market data feed.
	 * 
	 * @return the supported capabilities, will not be null but may be empty set if there is no
	 *         active feed (or it has no capabilities)
	 */
	public Set<Capability> getActiveFeedCapabilities() {
		Set<Capability> emptySet = Collections.emptySet();
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? emptySet : feed.getCapabilities();
	}

}

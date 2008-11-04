package org.marketcetera.photon.marketdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.marketdata.MarketDataFeed.IFeedStatusChangedListener;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.MarketDataSubscriber;
import org.marketcetera.photon.module.ModulePlugin;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Photon interface to market data services. This class is not synchronized. All
 * methods must be called from the UI thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public final class MarketDataManager {

	private final ModuleManager mModuleManager = ModulePlugin.getDefault()
			.getModuleManager();
	private final Map<String, MarketDataFeed> mFeeds = new HashMap<String, MarketDataFeed>();
	private final Map<MarketDataSubscriber, ModuleURN> mSubscribers = new HashMap<MarketDataSubscriber, ModuleURN>();
	private final ListenerList mActiveFeedListeners = new ListenerList();

	private MarketDataFeed mActiveFeed;

	private final IConfigurationProvider mModuleConfigProvider = new IConfigurationProvider() {

		@Override
		public ModuleURN getMarketDataSourceModule() {
			return mActiveFeed == null ? null : mActiveFeed.getURN();
		}
	};

	private final IFeedStatusChangedListener mFeedStatusChangesListener = new IFeedStatusChangedListener() {
		@Override
		public void feedStatusChanged(final FeedStatusEvent event) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (event.getSource() == mActiveFeed) {
						notifyListeners(event);
					}
				}
			});
		}
	};

	/**
	 * Constructor.
	 */
	public MarketDataManager() {
		List<ModuleURN> providers = mModuleManager.getProviders();
		for (ModuleURN providerURN : providers) {
			if (providerURN.providerType().equals(
					MarketDataFeed.MARKET_DATA_PROVIDER_TYPE)) {
				try {
					MarketDataFeed feed = new MarketDataFeed(providerURN);
					feed
							.addFeedStatusChangedListener(mFeedStatusChangesListener);
					mFeeds.put(providerURN.toString(), feed);
				} catch (Exception e) {
					Messages.MARKET_DATA_MANAGER_IGNORING_PROVIDER.warn(this,
							e, providerURN);
				}
			}
		}
	}

	/**
	 * Returns an unmodifiable list of the registered market data providers.
	 * 
	 * @return an unmodifiable list of the registered market data providers
	 */
	public Collection<MarketDataFeed> getProviders() {
		return Collections.unmodifiableCollection(mFeeds.values());
	}

	private String getPreferencesProviderId() {
		return PhotonPlugin.getDefault().getPreferenceStore().getString(
				ConnectionConstants.MARKETDATA_STARTUP_KEY);
	}

	/**
	 * Attempts to reconnect the active market data feed.
	 */
	public void reconnectFeed() {
		final MarketDataFeed newFeed = mFeeds.get(getPreferencesProviderId());
		if (mActiveFeed == null && newFeed == null) {
			return;
		}
		if (mActiveFeed != null && newFeed != mActiveFeed) {
			stopDataFlows();
		}
		if (newFeed != null) {
			try {
				if (!mModuleManager.getModuleInfo(newFeed.getURN()).getState()
						.isStarted()) {
					mModuleManager.start(newFeed.getURN());
				} else if (newFeed.getStatus() != FeedStatus.AVAILABLE) {
					newFeed.reconnect();
				}
				if (newFeed != mActiveFeed) {
					mActiveFeed = newFeed;
					startDataFlows();
					PlatformUI.getWorkbench().getDisplay().asyncExec(
							new Runnable() {
								@Override
								public void run() {
									notifyListeners(mActiveFeed
											.createFeedStatusEvent(null,
													mActiveFeed.getStatus()));
								}
							});
				}
			} catch (ModuleException e) {
				// TODO: May be better to propagate message to UI
				Messages.MARKET_DATA_MANAGER_FEED_START_FAILED.error(this,
						mActiveFeed.getName());
			} catch (UnsupportedOperationException e) {
				// TODO: May be better to propagate message to UI
				Messages.MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED.error(this,
						e, mActiveFeed.getName());
			}
		} else {
			final FeedStatusEvent event = mActiveFeed.createFeedStatusEvent(
					mActiveFeed.getStatus(), FeedStatus.OFFLINE);
			mActiveFeed = null;
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					notifyListeners(event);
				}
			});
		}
	}

	private void startDataFlows() {
		Iterator<Entry<MarketDataSubscriber, ModuleURN>> iterator = mSubscribers
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<MarketDataSubscriber, ModuleURN> entry = iterator.next();
			try {
				mModuleManager.start(entry.getValue());
			} catch (ModuleException e) {
				Messages.MARKET_DATA_MANAGER_RECEIVER_START_FAILED.error(this,
						e, entry.getKey().getSymbol());
				iterator.remove();
				try {
					mModuleManager.deleteModule(entry.getValue());
				} catch (ModuleException ex) {
					Messages.MARKET_DATA_MANAGER_DELETE_FAILED.error(this, ex,
							entry.getKey().getSymbol());
				}
			}
		}
	}

	private void stopDataFlows() {
		Iterator<Entry<MarketDataSubscriber, ModuleURN>> iterator = mSubscribers
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<MarketDataSubscriber, ModuleURN> entry = iterator.next();
			try {
				if (mModuleManager.getModuleInfo(entry.getValue()).getState()
						.isStarted()) {
					mModuleManager.stop(entry.getValue());
				}
			} catch (ModuleException e) {
				Messages.MARKET_DATA_MANAGER_RECEIVER_STOP_FAILED.error(this,
						e, entry.getKey().getSymbol());
				try {
					mModuleManager.deleteModule(entry.getValue());
				} catch (ModuleException ex) {
					Messages.MARKET_DATA_MANAGER_DELETE_FAILED.error(this, ex,
							entry.getKey().getSymbol());
				}
				iterator.remove();
			}
		}
	}

	/**
	 * Add a subscriber to process incoming market data for a given symbol.
	 * 
	 * @param subscriber
	 *            the new subscriber
	 */
	public void addSubscriber(MarketDataSubscriber subscriber) {
		// subscriber will receive market data for it's symbol and will persist
		// even if feed changes or restarts
		try {
			ModuleURN subscriberURN = mModuleManager.createModule(
					MarketDataReceiverFactory.PROVIDER_URN,
					mModuleConfigProvider, subscriber);
			if (mActiveFeed != null) {
				mModuleManager.start(subscriberURN);
			}
			mSubscribers.put(subscriber, subscriberURN);
		} catch (ModuleException e) {
			// TODO: May be better to propagate message to UI
			Messages.MARKET_DATA_MANAGER_SUBSCRIBE_FAILED.error(this, e,
					subscriber.getSymbol());
		}
	}

	/**
	 * Removes the subscriber if it has been registered. This is a no-op if the
	 * subscriber was never registered.
	 * 
	 * @param subscriber
	 *            subscriber to remove
	 */
	public void removeSubscriber(MarketDataSubscriber subscriber) {
		ModuleURN subscriberURN = mSubscribers.get(subscriber);
		if (subscriberURN == null)
			return;
		mSubscribers.remove(subscriber);
		try {
			mModuleManager.deleteModule(subscriberURN);
		} catch (ModuleException e) {
			Messages.MARKET_DATA_MANAGER_DELETE_FAILED.error(this, e,
					subscriber.getSymbol());
		}
	}

	/**
	 * Adds a listener to the manager that tracks the status of the active feed.
	 * 
	 * @param listener
	 *            to be notified when the active feed status changes
	 */
	public void addActiveFeedStatusChangedListener(
			IFeedStatusChangedListener listener) {
		mActiveFeedListeners.add(listener);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeActiveFeedStatusChangedListener(
			IFeedStatusChangedListener listener) {
		mActiveFeedListeners.remove(listener);
	}

	private void notifyListeners(FeedStatusEvent event) {
		Object[] listeners = mActiveFeedListeners.getListeners();
		for (Object object : listeners) {
			((IFeedStatusChangedListener) object).feedStatusChanged(event);
		}
	}

	/**
	 * Returns the human readable name of the active market data feed. If there
	 * is no active feed, <code>null</code> will be returned.
	 * 
	 * @return the human readable name of the active market data feed or
	 *         <code>null</code> if none exists
	 */
	public String getActiveFeedName() {
		return mActiveFeed == null ? null : mActiveFeed.getName();
	}

	/**
	 * Returns the status of the active market data feed. If there is no active
	 * feed, FeedStatus.OFFLINE will be returned.
	 * 
	 * @return the status of the active market data feed or FeedStatus.OFFLINE
	 *         if none exists
	 */
	public FeedStatus getActiveFeedStatus() {
		return mActiveFeed == null ? FeedStatus.OFFLINE : mActiveFeed
				.getStatus();
	}

}

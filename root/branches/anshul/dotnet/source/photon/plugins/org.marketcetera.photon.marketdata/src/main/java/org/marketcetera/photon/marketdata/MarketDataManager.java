package org.marketcetera.photon.marketdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.marketdata.Activator;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverFactory;
import org.marketcetera.photon.internal.marketdata.Messages;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverFactory.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.marketdata.MarketDataFeed.IFeedStatusChangedListener;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Photon interface to market data services.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: MarketDataManager.java 10229 2008-12-09 21:48:48Z klim $
 * @since 1.0.0
 */
@ClassVersion("$Id: MarketDataManager.java 10229 2008-12-09 21:48:48Z klim $")
public final class MarketDataManager {

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static MarketDataManager getCurrent() {
		return Activator.getDefault().getMarketDataManager();
	}

	private final ModuleManager mModuleManager = ModuleSupport
			.getModuleManager();
	private final Map<String, MarketDataFeed> mFeeds = new HashMap<String, MarketDataFeed>();
	private final Map<MarketDataSubscriber, ModuleURN> mSubscribers = new HashMap<MarketDataSubscriber, ModuleURN>();
	private final ListenerList mActiveFeedListeners = new ListenerList();
	private final AtomicBoolean mReconnecting = new AtomicBoolean(false);

	private volatile MarketDataFeed mActiveFeed;

	private final IConfigurationProvider mModuleConfigProvider = new IConfigurationProvider() {

		@Override
		public ModuleURN getMarketDataSourceModule() {
			MarketDataFeed feed = mActiveFeed;
			return feed == null ? null : feed.getURN();
		}
	};

	private final IFeedStatusChangedListener mFeedStatusChangesListener = new IFeedStatusChangedListener() {
		@Override
		public void feedStatusChanged(final FeedStatusEvent event) {
			if (event.getSource() == mActiveFeed) {
				notifyListeners(event);
			}
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

	/**
	 * Attempts to reconnect to the default active market data feed.
	 */
	public void reconnectFeed() {
		reconnectFeed(getDefaultActiveFeed());
	}

	private void reconnectFeed(String providerId) {
		if (!mReconnecting.compareAndSet(false, true)) {
			return;
		}
		synchronized (mSubscribers) {
			final MarketDataFeed oldFeed = mActiveFeed;
			mActiveFeed = mFeeds.get(providerId);
			if (mActiveFeed == null && oldFeed == null) {
				return;
			}
			if (oldFeed != mActiveFeed && oldFeed != null) {
				stopDataFlows();
			}
			if (mActiveFeed != null) {
				try {
					if (!mModuleManager.getModuleInfo(mActiveFeed.getURN())
							.getState().isStarted()) {
						mModuleManager.start(mActiveFeed.getURN());
					} else if (mActiveFeed.getStatus() != FeedStatus.AVAILABLE) {
						mActiveFeed.reconnect();
					}
					if (oldFeed != mActiveFeed) {
						startDataFlows();
					}
				} catch (ModuleException e) {
					// TODO: May be better to propagate message to UI
					// PhotonPlugin.getMainConsoleLogger().error(
					// Messages.MARKET_DATA_MANAGER_FEED_START_FAILED
					// .getText(mActiveFeed.getName()));
					Messages.MARKET_DATA_MANAGER_FEED_START_FAILED.error(this,
							mActiveFeed.getName());
				} catch (UnsupportedOperationException e) {
					// TODO: May be better to propagate message to UI
					// PhotonPlugin.getMainConsoleLogger().error(
					// Messages.MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED
					// .getText(mActiveFeed.getName()));
					Messages.MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED.error(
							this, e, mActiveFeed.getName());
				}
			}

			final FeedStatusEvent event;
			if (mActiveFeed == null) {
				event = oldFeed.createFeedStatusEvent(oldFeed.getStatus(),
						FeedStatus.OFFLINE);
			} else {
				event = mActiveFeed.createFeedStatusEvent(FeedStatus.OFFLINE,
						mActiveFeed.getStatus());
			}
			mReconnecting.set(false);
			notifyListeners(event);
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
		synchronized (mSubscribers) {
			// subscriber will receive market data for it's symbol and will
			// persist even if feed changes or restarts
			try {
				ModuleURN subscriberURN = mModuleManager.createModule(
						MarketDataReceiverFactory.PROVIDER_URN,
						mModuleConfigProvider, subscriber);
				if (getActiveFeedStatus() == FeedStatus.AVAILABLE) {
					mModuleManager.start(subscriberURN);
				}
				mSubscribers.put(subscriber, subscriberURN);
			} catch (ModuleException e) {
				// TODO: May be better to propagate message to UI
				// PhotonPlugin.getMainConsoleLogger().error(
				// Messages.MARKET_DATA_MANAGER_SUBSCRIBE_FAILED
				// .getText(subscriber.getSymbol()));
				Messages.MARKET_DATA_MANAGER_SUBSCRIBE_FAILED.error(this, e,
						subscriber.getSymbol());
			}
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
		synchronized (mSubscribers) {
			ModuleURN subscriberURN = mSubscribers.get(subscriber);
			if (subscriberURN == null)
				return;
			mSubscribers.remove(subscriber);
			try {
				if (mModuleManager.getModuleInfo(subscriberURN).getState()
						.isStarted()) {
					mModuleManager.stop(subscriberURN);
				}
				mModuleManager.deleteModule(subscriberURN);
			} catch (ModuleException e) {
				Messages.MARKET_DATA_MANAGER_DELETE_FAILED.error(this, e,
						subscriber.getSymbol());
			}
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
	 * Returns the human readable name of the active market data feed. If there
	 * is no active feed, <code>null</code> will be returned.
	 * 
	 * @return the human readable name of the active market data feed or
	 *         <code>null</code> if none exists
	 */
	public String getActiveFeedName() {
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? null : feed.getName();
	}

	/**
	 * Returns the status of the active market data feed. If there is no active
	 * feed, FeedStatus.OFFLINE will be returned.
	 * 
	 * @return the status of the active market data feed or FeedStatus.OFFLINE
	 *         if none exists
	 */
	public FeedStatus getActiveFeedStatus() {
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? FeedStatus.OFFLINE : feed.getStatus();
	}

}

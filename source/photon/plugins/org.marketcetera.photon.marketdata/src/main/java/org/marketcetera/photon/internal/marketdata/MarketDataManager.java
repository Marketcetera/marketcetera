package org.marketcetera.photon.internal.marketdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.AttributeChangeNotification;
import javax.management.NotificationEmitter;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.marketdata.MarketDataConstants;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Internal implementation of {@link IMarketDataManager}.
 * 
 * <h4>Market Data Module Abstraction</h4>
 * 
 * This class discovers available market data modules that follow the following conventions:
 * <ol>
 * <li>Have a module provider type of "mdata"</li>
 * <li>Are singleton modules</li>
 * <li>Implement the {@link AbstractMarketDataModuleMXBean} interface</li>
 * <li>Implement the {@link NotificationEmitter} interface</li>
 * </ol>
 * Modules that don't adhere to these conventions will not be supported. Additionally, although it
 * is not validated, the module must send {@link AttributeChangeNotification attribute change
 * notifications} for the "FeedStatus" attribute when the feed's status changes in order for this
 * class to function properly. Typically, all market data modules will extend
 * {@link AbstractMarketDataModule}, which provides much of the needed functionality.
 * <p>
 * Each market data module is proxied by a {@link MarketDataFeed} instance that handles the
 * interactions with the underlying modules.
 * 
 * <h4>Active Feed Management</h4>
 * 
 * The current market data UI paradigm associates all market data display with a single feed. This
 * class supports this model by maintaining an active feed and delegating all requests to that feed.
 * It also has an internal {@link IFeedStatusChangedListener} that listens to feed status changes on
 * all its feeds and directs notifications from the active feed to listeners registered with this
 * class.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataManager implements IMarketDataManager {

	private static final String USER_MSG_CATEGORY = org.marketcetera.core.Messages.USER_MSG_CATEGORY;

	private final ModuleManager mModuleManager;
	private final Map<String, MarketDataFeed> mFeeds = new HashMap<String, MarketDataFeed>();
	private final ListenerList mActiveFeedListeners = new ListenerList();
	private final AtomicBoolean mReconnecting = new AtomicBoolean(false);

	private volatile MarketDataFeed mActiveFeed;

	private final IFeedStatusChangedListener mFeedStatusChangesListener = new IFeedStatusChangedListener() {
		@Override
		public void feedStatusChanged(final IFeedStatusEvent event) {
			if (event.getSource() == mActiveFeed) {
				notifyListeners(event);
			}
		}
	};
	private final MarketData mMarketData;

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager to use
	 * @param marketData
	 *            the MarketData instance
	 */
	@Inject
	public MarketDataManager(final ModuleManager moduleManager, final MarketData marketData) {
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

	@Override
	public IMarketData getMarketData() {
		return mMarketData;
	}

	@Override
	public Collection<? extends IMarketDataFeed> getProviders() {
		return Collections.unmodifiableCollection(mFeeds.values());
	}

	@Override
	public void reconnectFeed() {
		reconnectFeed(getDefaultActiveFeed());
	}

	/**
	 * Private implementation method, only public for testing purposes.
	 * <p>
	 * TODO: Refactor for better encapsulation
	 */
	public void reconnectFeed(final String providerId) {
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
						// TODO: May be better to propagate the exception so
						// there can be dialog boxes, etc
						Messages.MARKET_DATA_MANAGER_FEED_START_FAILED.error(USER_MSG_CATEGORY,
								mActiveFeed.getName());
					} catch (UnsupportedOperationException e) {
						// TODO: May be better to propagate the exception so
						// there can be dialog boxes, etc
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

	@Override
	public void addActiveFeedStatusChangedListener(final IFeedStatusChangedListener listener) {
		mActiveFeedListeners.add(listener);
	}

	@Override
	public void removeActiveFeedStatusChangedListener(final IFeedStatusChangedListener listener) {
		mActiveFeedListeners.remove(listener);
	}

	private void notifyListeners(final IFeedStatusEvent event) {
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
		return Platform.getPreferencesService().getString(MarketDataConstants.PLUGIN_ID,
				MarketDataConstants.DEFAULT_ACTIVE_MARKETDATA_PROVIDER, "", //$NON-NLS-1$
				null);
	}

	@Override
	public String getActiveFeedName() {
		IMarketDataFeed feed = mActiveFeed;
		return feed == null ? null : feed.getName();
	}

	@Override
	public FeedStatus getActiveFeedStatus() {
		MarketDataFeed feed = mActiveFeed;
		return feed == null ? FeedStatus.OFFLINE : feed.getStatus();
	}

	@Override
	public Set<Capability> getActiveFeedCapabilities() {
		Set<Capability> emptySet = Collections.emptySet();
		IMarketDataFeed feed = mActiveFeed;
		return feed == null ? emptySet : feed.getCapabilities();
	}

}

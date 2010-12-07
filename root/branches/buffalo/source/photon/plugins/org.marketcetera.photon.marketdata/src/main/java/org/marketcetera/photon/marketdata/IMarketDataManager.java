package org.marketcetera.photon.marketdata;

import java.util.Collection;
import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Provides access to all services of this plug-in.
 * 
 * <h4>Feed Management</h4>
 * 
 * When the plug-in starts, it queries the module framework for available market data feed
 * providers. These can be obtained via {@link #getProviders()}. There can be multiple feeds, but
 * there is always at most one "active" feed. Information about the active feed can be obtained from
 * the <code>
 * getActiveFeedXXX</code> methods. If the feed goes down for some reason, the
 * {@link #reconnectFeed()} operation can be used to attempt a reconnect. Clients can also subscribe
 * to be notified when the active feed changes by registering a listener using
 * {@link #addActiveFeedStatusChangedListener(IFeedStatusChangedListener)}.
 * <p>
 * The default active feed is stored in plug-in preferences. To change the active feed, you must set
 * the {@link MarketDataConstants#DEFAULT_ACTIVE_MARKETDATA_PROVIDER associated preference} and then
 * call {@link #reconnectFeed()}.
 * 
 * <h4>Market Data</h4>
 * 
 * Market data is accessed with {@link #getMarketData()}. See {@link IMarketData} for more details.
 * Note that the provided market data objects always correspond to the data being streamed from the
 * active feed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface IMarketDataManager {

	/**
	 * Provides access to market data.
	 * 
	 * @return the associated IMarketData interface entry point to market data
	 */
	IMarketData getMarketData();

	/**
	 * Returns an unmodifiable list of the registered market data providers. This list is build
	 * during plug-in initialization and does not change during the plug-in lifetime.
	 * 
	 * @return an unmodifiable list of the registered market data providers
	 */
	Collection<? extends IMarketDataFeed> getProviders();

	/**
	 * Attempts to reconnect to the default active market data feed. Active feed status listeners
	 * will be notified of changes resulting from this operation.
	 * 
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	void reconnectFeed();

	/**
	 * Adds a listener to the manager to tracks the status of the active feed. This operation does
	 * nothing if the listener is already registered.
	 * 
	 * @param listener
	 *            to be notified when the active feed status changes
	 */
	void addActiveFeedStatusChangedListener(IFeedStatusChangedListener listener);

	/**
	 * Removes the listener. This operation does nothing if the listener is not registered.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	void removeActiveFeedStatusChangedListener(IFeedStatusChangedListener listener);

	/**
	 * Returns the human readable name of the active market data feed. If there is no active feed,
	 * <code>null</code> will be returned.
	 * 
	 * @return the human readable name of the active market data feed or <code>null</code> if none
	 *         exists
	 */
	String getActiveFeedName();

	/**
	 * Returns the status of the active market data feed. If there is no active feed,
	 * <code>FeedStatus.OFFLINE</code> will be returned.
	 * 
	 * @return the status of the active market data feed or <code>FeedStatus.OFFLINE</code> if none
	 *         exists
	 */
	FeedStatus getActiveFeedStatus();

	/**
	 * Returns the capabilities supported by the active market data feed.
	 * 
	 * @return the supported capabilities, will not be <code>null</code> but may be empty set if
	 *         there is no active feed (or it has no capabilities)
	 */
	Set<Capability> getActiveFeedCapabilities();

}
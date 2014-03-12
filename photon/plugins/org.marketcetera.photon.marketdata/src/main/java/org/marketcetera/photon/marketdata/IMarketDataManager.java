package org.marketcetera.photon.marketdata;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.core.ICredentialsService;
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
public interface IMarketDataManager
{
	/**
	 * Provides access to market data.
	 * 
	 * @return the associated IMarketData interface entry point to market data
	 */
	IMarketData getMarketData();
	/**
	 * Attempts to reconnect to the default active market data feed. Active feed status listeners
	 * will be notified of changes resulting from this operation.
	 * 
	 * @throws IllegalStateException if the module framework is in an unexpected state, or if an unrecoverable error occurs
	 */
	void reconnectFeed();
	/**
	 * Adds a listener to the manager to tracks the status of the active feed. This operation does
	 * nothing if the listener is already registered.
	 * 
	 * @param listener to be notified when the active feed status changes
	 */
	void addActiveFeedStatusChangedListener(IFeedStatusChangedListener listener);
	/**
	 * Removes the listener. This operation does nothing if the listener is not registered.
	 * 
	 * @param listener listener to remove
	 */
	void removeActiveFeedStatusChangedListener(IFeedStatusChangedListener listener);
	/**
	 * Returns the status of the market data nexus connection.
	 * 
	 * @return a <code>FeedStatus</code> value
	 */
	FeedStatus getFeedStatus();
	/**
	 * 
	 *
	 *
	 * @param inCredentialsService
	 */
	void setCredentialsService(ICredentialsService inCredentialsService);
	/**
	 * 
	 *
	 *
	 * @param inHostname
	 */
	void setHostname(String inHostname);
	/**
	 * 
	 *
	 *
	 * @param inPort
	 */
	void setPort(int inPort);
    /**
     * Indicates if the market data connection is ready.
     *
     * @return a <code>boolean</code> value
     */
    boolean isRunning();
    /**
     *
     *
     */
    void close();
}

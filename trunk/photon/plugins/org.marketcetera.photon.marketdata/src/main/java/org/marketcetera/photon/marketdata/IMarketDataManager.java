package org.marketcetera.photon.marketdata;

import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Provides access to all services of this plug-in.
 * 
 * <p>When the plug-in starts, it starts a connection to the Market Data Nexus. If the connection goes down for some reason, the
 * {@link #reconnectFeed()} operation can be used to attempt a reconnect. Clients can also subscribe
 * to be notified when the active feed changes by registering a listener using
 * {@link #addActiveFeedStatusChangedListener(IFeedStatusChangedListener)}.
 * 
 * <h4>Market Data</h4>
 * 
 * Market data is accessed with {@link #getMarketData()}. See {@link IMarketData} for more details.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
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
	 * Sets the credentials service value.
	 *
	 * @param inCredentialsService an <code>ICredentialsService</code> value
	 */
	void setCredentialsService(ICredentialsService inCredentialsService);
	/**
	 * Sets the market data nexus host.
	 *
	 * @param inHostname a <code>String</code> value
	 */
	void setHostname(String inHostname);
	/**
	 * Sets the market data nexus port.
	 *
	 * @param inPort an <code>int</code> value
	 */
	void setPort(int inPort);
    /**
     * Indicates if the market data connection is ready.
     *
     * @return a <code>boolean</code> value
     */
    boolean isRunning();
    /**
     * Indicates if the market data connection is currently being reconnected.
     *
     * @return a <code>boolean</code> value
     */
    boolean isReconnecting();
    /**
     * Closes the market data manager connections.
     */
    void close();
    /**
     * Gets the available capabilities;
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    Set<Capability> getAvailabilityCapability();
}

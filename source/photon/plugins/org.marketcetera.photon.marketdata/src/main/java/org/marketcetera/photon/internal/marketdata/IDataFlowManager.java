package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.model.marketdata.impl.MDItemImpl;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A data flow manager is responsible for managing data flows from market data feeds to
 * {@link MarketDataReceiverModule} instances.
 * 
 * It produces live data item (via {@link #getItem(Key)}) that are dynamically updated as data
 * arrives.
 * 
 * It is also responsible for stopping and restarting data flows when the market data source feed
 * changes (see {@link #setSourceFeed(IMarketDataFeed)}).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 * @param <T>
 *            the type of data item this manager will produce
 * @param <K>
 *            the key that identifies a unique data request
 */
@ClassVersion("$Id$")
public interface IDataFlowManager<T extends MDItemImpl, K extends Key<? super T>> {

	/**
	 * Returns a data item for the given key. The item will not have any data unless
	 * {@link #startFlow(Key)} is called (or has already been called).
	 * 
	 * @param key
	 *            the data key
	 * @return the data item
	 * @throws IllegalArgumentException
	 *             if key is null
	 */
	T getItem(K key);

	/**
	 * Sets the feed that will provide the market data. Until this is called, data items returned
	 * from {@link #getItem(Key)} will not have any data.
	 * 
	 * If this is called after data flows are started, it will rewire them to the new feed.
	 * 
	 * Setting the feed to null will reset all data items to their initial state (with null
	 * data).
	 * 
	 * Successive calls with the same source feed recreates all data flows.
	 * 
	 * @param feed
	 *            the market data feed, can be null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	void setSourceFeed(IMarketDataFeed feed);

	/**
	 * Starts the data flow for the given key.
	 * 
	 * This operation has no effect if the flow is already started.
	 * 
	 * @param key
	 *            the data key
	 * @throws IllegalArgumentException
	 *             if key is null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	void startFlow(K key);

	/**
	 * Stops the data flow for the given key.
	 * 
	 * This operation has no effect if the flow is already stopped.
	 * 
	 * @param key
	 *            the data key
	 * @throws IllegalArgumentException
	 *             if key is null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	void stopFlow(K key);
}

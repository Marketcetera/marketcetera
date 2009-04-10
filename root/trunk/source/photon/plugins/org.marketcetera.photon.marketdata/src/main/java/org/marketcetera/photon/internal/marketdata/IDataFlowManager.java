package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A data flow manager is responsible for managing data flows from market data modules to
 * {@link MarketDataReceiverModule} instances.
 * 
 * It produces live data item (via {@link #getItem(Key)}) that are dynamically updated as data
 * arrives.
 * 
 * It is also responsible for stopping and restarting data flows when the market data source module
 * changes (see {@link #setSourceModule(ModuleURN)}).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 * @param <T>
 *            the type of data item this manager will produce
 * @param <K>
 *            the key that identifies a unique data request
 */
@ClassVersion("$Id$")
public interface IDataFlowManager<T extends MDItem, K extends Key<T>> {

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
	 * Sets the module that will provide the market data. Until this is called, data items returned
	 * from {@link #getItem(Key)} will not have any data.
	 * 
	 * If this is called after data flows are started, it will rewire them to the new source module.
	 * 
	 * Setting the source module to null will reset all data items to their intial state (with null
	 * data).
	 * 
	 * Successive calls with the same source module is a no-op.
	 * 
	 * @param module
	 *            the instance URN of the market data module, can be null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	void setSourceModule(ModuleURN module);

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

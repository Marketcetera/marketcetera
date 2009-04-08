package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base interface for data flow managers.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
	 * Sets the module that will provide the market data. This must be called before any data flow
	 * can be started.
	 * 
	 * If this is called while data flows are started, they will be stopped and restarted with the
	 * new source module.
	 * 
	 * Setting the source module to null stops any running flows.
	 * 
	 * Successive calls with the same source module is a no-op.
	 * 
	 * @param module
	 *            the instance URN of the market data module, can be null
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
	 */
	void stopFlow(K key);
}

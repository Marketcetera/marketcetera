package org.marketcetera.photon.marketdata;

import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handle to a market data item that allows it to be disposed when no longer needed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface IMarketDataReference<T extends MDItem> {

	/**
	 * Returns an instance of the the market data item that reflects dynamically updated market
	 * data. Clients should not modify the returned item except to add/remove adapters.
	 * 
	 * @return the market data item, null if this reference has been disposed
	 */
	public T get();

	/**
	 * Disposes the reference. After this is called, {@link #get()} will return null and any cached
	 * references to the market data item are not guaranteed to reflect the latest data.
	 */
	public void dispose();
}

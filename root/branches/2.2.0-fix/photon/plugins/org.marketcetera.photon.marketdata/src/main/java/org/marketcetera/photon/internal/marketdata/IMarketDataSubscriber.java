package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Interface for market data receiver module subscribers that configure/handle market data requests.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface IMarketDataSubscriber {

	/**
	 * Returns the market data request.
	 * 
	 * @return the market data request, cannot be null
	 */
	MarketDataRequest getRequest();

	/**
	 * Returns the source module URN.
	 * 
	 * @return the source module URN, cannot be null
	 */
	ModuleURN getSourceModule();

	/**
	 * Callback to provide market data to be processed.
	 * 
	 * @param inData
	 *            data from the market data flow
	 */
	void receiveData(Object inData);
}
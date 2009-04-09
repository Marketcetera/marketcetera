package org.marketcetera.photon.marketdata;

import org.marketcetera.photon.internal.marketdata.MarketData;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for accessing common market data.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@ImplementedBy(MarketData.class)
public interface IMarketData {

	/**
	 * Returns a reference to the latest tick data for the given symbol. If the data does not exist,
	 * it will be created and wired up. The {@link IMarketDataReference#dispose() dispose} method
	 * should be called when the data is no longer needed.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return a reference to the data
	 * @throws IllegalArgumentException
	 *             if symbol is null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	IMarketDataReference<MDLatestTick> getLatestTick(String symbol);

	/**
	 * Returns a reference to the top of book data for the given symbol. If the data does not exist,
	 * it will be created and wired up. The {@link IMarketDataReference#dispose() dispose} method
	 * should be called when the data is no longer needed.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return a reference to the data
	 * @throws IllegalArgumentException
	 *             if symbol is null
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	IMarketDataReference<MDTopOfBook> getTopOfBook(String symbol);
}
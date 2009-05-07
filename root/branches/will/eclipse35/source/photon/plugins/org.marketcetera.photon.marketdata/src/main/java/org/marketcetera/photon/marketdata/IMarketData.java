package org.marketcetera.photon.marketdata;

import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.internal.marketdata.MarketData;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for accessing common market data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
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

	/**
	 * Returns a reference to the market statistic data for the given symbol. If the data does not
	 * exist, it will be created and wired up. The {@link IMarketDataReference#dispose() dispose}
	 * method should be called when the data is no longer needed.
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
	IMarketDataReference<MDMarketstat> getMarketstat(String symbol);

	/**
	 * Returns a reference to the market depth data for the given symbol and product. If the data does not
	 * exist, it will be created and wired up. The {@link IMarketDataReference#dispose() dispose}
	 * method should be called when the data is no longer needed.
	 * 
	 * @param symbol
	 *            the symbol
	 * @param product
	 *            the product
	 * @return a reference to the data
	 * @throws IllegalArgumentException
	 *             if symbol or product is null, or if product is not a valid market depth product
	 * @throws IllegalStateException
	 *             if the module framework is in an unexpected state, or if an unrecoverable error
	 *             occurs
	 */
	IMarketDataReference<MDDepthOfBook> getDepthOfBook(String symbol, Content product);
}
package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Key for market statistic data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketstatKey extends Key<MDMarketstat> {

	/**
	 * Constructor.
	 * 
	 * @param symbol
	 *            the symbol
	 */
	public MarketstatKey(String symbol) {
		super(symbol);
	}
}
package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.trade.Instrument;
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
public class MarketstatKey extends Key {

	/**
	 * Constructor.
	 * 
	 * @param instrument
	 *            the instrument
	 */
	public MarketstatKey(final Instrument instrument) {
		super(instrument);
	}
}
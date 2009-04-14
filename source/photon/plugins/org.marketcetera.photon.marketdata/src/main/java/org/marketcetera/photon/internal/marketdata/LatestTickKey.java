package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Key for latest tick data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class LatestTickKey extends Key<MDLatestTick> {

	/**
	 * Constructor.
	 * 
	 * @param symbol
	 *            the symbol
	 */
	public LatestTickKey(String symbol) {
		super(symbol);
	}
}
package org.marketcetera.photon.marketdata;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constants for this plug-in.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class MarketDataConstants {

	// Suppress default constructor for noninstantiability
	private MarketDataConstants() {
		throw new AssertionError();
	}

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.marketdata"; //$NON-NLS-1$

	/**
	 * This constant is the key in the plug-in preferences that identifies the desired default
	 * market data feed. The value is the {@link ModuleURN} of the feed's module provider, which is
	 * also the feed's id, accessible via {@link IMarketDataFeed#getId()}.
	 */
	public static final String DEFAULT_ACTIVE_MARKETDATA_PROVIDER = "DEFAULT_ACTIVE_MARKETDATA_PROVIDER"; //$NON-NLS-1$
}

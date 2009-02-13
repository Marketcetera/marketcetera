package org.marketcetera.photon.marketdata;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Preferences for this plug-in.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface MarketDataPreferences {
	
	/**
	 * A single market data provider it active at once.  The {@link ModuleURN} of the
	 * default active provider is saved in the plug-in preferences.
	 */
	String DEFAULT_ACTIVE_MARKETDATA_PROVIDER = "DEFAULT_ACTIVE_MARKETDATA_PROVIDER"; //$NON-NLS-1$
}

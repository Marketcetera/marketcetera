package org.marketcetera.photon.marketdata;

import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Abstraction of a market data feed.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface IMarketDataFeed {

	/**
	 * Returns the singleton instance URN for the feed provider.
	 * 
	 * @return the singleton instance URN
	 */
	ModuleURN getURN();

	/**
	 * Returns the feed's capabilities.
	 * 
	 * @return the feed capabilities
	 */
	Set<Capability> getCapabilities();

	/**
	 * Returns a human readable name for the feed.
	 * 
	 * @return human readable name for the feed
	 */
	String getName();

}
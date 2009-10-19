package org.marketcetera.photon.marketdata;

import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
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
	 * Returns the feed's capabilities. The value returned reflects the capabilities reported by
	 * feed at the time the method is called. Some feed's capabilities may change after login.
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

	/**
	 * Returns the feed's unique identifier, which is the {@link ModuleURN} of the feed's module
	 * provider.
	 * <p>
	 * This is the value that should be saved to the
	 * {@link MarketDataConstants#DEFAULT_ACTIVE_MARKETDATA_PROVIDER} preference if this feed is
	 * desired to be the default active feed.
	 * 
	 * @return unique identifier for the feed
	 */
	String getId();

	/**
	 * Returns the feed status.
	 * 
	 * @return the feed status
	 */
	FeedStatus getStatus();
}
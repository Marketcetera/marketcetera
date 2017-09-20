package org.marketcetera.marketdata;

import java.util.Collection;

/* $License$ */

/**
 * Broadcasts market data capabilities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataCapabilityBroadcaster
{
    /**
     * Report capabilities to the broadcaster.
     *
     * @param inCapabilities a <code>Collection&lt;Capability&gt;</code> value
     */
    void reportCapability(Collection<Capability> inCapabilities);
}

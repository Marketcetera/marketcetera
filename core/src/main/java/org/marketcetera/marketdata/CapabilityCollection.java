package org.marketcetera.marketdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* $License$ */

/**
 * Records and publishes all capabilities from all market data adapters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class CapabilityCollection
{
    /**
     * Report the given capabilities.
     *
     * @param inCapabilities a <code>Collection&lt;Capability&gt;</code> value
     */
    public static void reportCapability(Collection<Capability> inCapabilities)
    {
        allReportedCapabilities.addAll(inCapabilities);
    }
    /**
     * Get all reported capabilities.
     *
     * @return a <code>Set&lt;Capability</code> value
     */
    public static Set<Capability> getReportedCapabilities()
    {
        return Collections.unmodifiableSet(allReportedCapabilities);
    }
    /**
     * holds all capabilities reported by 
     */
    private static Set<Capability> allReportedCapabilities = new HashSet<>();
}

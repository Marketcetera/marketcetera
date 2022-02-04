package org.marketcetera.util.ws.stateful;

import java.util.Collection;

/* $License$ */

/**
 * Indicates that the implementor consumes a port.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface UsesPort
{
    /**
     * Get the ports that this implementor uses.
     *
     * @return a <code>Collection&lt;PortDescriptor&gt;</code> value
     */
    Collection<PortDescriptor> getPortDescriptors();
}

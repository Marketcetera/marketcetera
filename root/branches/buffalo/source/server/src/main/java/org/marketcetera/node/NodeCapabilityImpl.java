package org.marketcetera.node;

import org.marketcetera.api.nodes.NodeCapability;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum NodeCapabilityImpl
        implements NodeCapability
{
    ORDER_ROUTING,
    MODULE_EXECUTION;
    /* (non-Javadoc)
     * @see org.marketcetera.api.nodes.NodeCapability#getValue()
     */
    @Override
    public String getValue()
    {
        return name();
    }
}

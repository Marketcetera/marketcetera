package org.marketcetera.core.resourcepool;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the status of a {@link ResourcePool}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public enum ResourcePoolStatus
{
    /**
     * the resource pool is ready for use
     */
    READY,
    /**
     * the resource pool is on the way to READY
     */
    STARTING,
    /**
     * the resource pool is on the way to NOT_READY
     */
    STOPPING,
    /**
     * the resource pool cannot be used
     */
    NOT_READY;
}

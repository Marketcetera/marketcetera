package org.marketcetera.core.resourcepool;

import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the status of a {@link ResourcePool} {@link Resource}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public enum ResourceStatus
{
    /**
     * the resource is ready to be allocated
     */
    READY,
    /**
     * the resource is active, but cannot be allocated for further use yet - this may change
     */
    AT_CAPACITY,
    /**
     * the resource has encountered an error that makes it unable to be used
     */
    BROKEN,
    /**
     * the resource is not ready to be allocated
     */
    NOT_READY;
    /**
     * Indicates if the resource is ready to be allocated.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isReady()
    {
        return READY_STATUSES.contains(this);
    }
    /**
     * stores status values that indicate the resource is ready to be allocated 
     */
    private static final Set<ResourceStatus> READY_STATUSES = EnumSet.of(READY);
}

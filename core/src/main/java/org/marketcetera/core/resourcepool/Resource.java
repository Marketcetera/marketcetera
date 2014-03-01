package org.marketcetera.core.resourcepool;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Identifies a resource for use in a {@link ResourcePool}.
 * 
 * <p>The type <code>ResourceAllocationHintClazz</code> will be used
 * to guide the allocation of resources in the associated {@link ResourcePool}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Resource<ResourceAllocationHintClazz>
        extends Lifecycle
{
    /**
     * Gets the status of this resource.
     *
     * @return a <code>ResourceStatus</code> value
     */
    ResourceStatus getResourceStatus();
    /**
     * Indicates if this resource is suitable for the given allocation.
     *
     * @param inHint a <code>ResourceAllocationHintClazz</code> or <code>null</code>
     * @return a <code>boolean</code> value indicating if this resource can handle the request implied by the given hint
     */
    boolean isSuitable(ResourceAllocationHintClazz inHint);
    /**
     * Notifies this resource that it has been allocated.
     */
    void allocated();
    /**
     * Notifies this resource that is has been returned.
     */
    void returned();
    /**
     * Notifies this resource that is has been released.
     */
    void released();
}

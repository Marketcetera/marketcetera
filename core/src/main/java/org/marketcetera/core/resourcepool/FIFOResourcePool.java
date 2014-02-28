package org.marketcetera.core.resourcepool;

import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

import org.marketcetera.core.Messages;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link ResourcePool} that allocates <code>Resource</code> objects
 * in FIFO order.
 * 
 * <p><code>Resource</code> access, return, and membership check are all performed in
 * constant time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public abstract class FIFOResourcePool<ResourceClazz extends Resource>
        extends ResourcePool<ResourceClazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getPoolLock()
     */
    @Override
    protected Object getPoolLock()
    {
        return resources;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getPoolIterator()
     */
    @Override
    protected Iterator<ResourceClazz> getPoolIterator()
    {
        return resources.iterator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getNextResource(java.lang.Object)
     */
    @Override
    protected ResourceClazz getNextResource(Object inData)
            throws ResourcePoolException
    {
        try {
            // inside the parent synchronization lock
            if(resources.isEmpty()) {
                // try to add a resource
                ResourceClazz newResource = createResource(inData);
                addResourceToPool(newResource);
            }
            return allocateNextResource(inData);
        } catch (Exception e) {
            throw new ResourcePoolException(e,
                                            Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#addResourceToPool(org.marketcetera.core.resourcepool.Resource)
     */
    @Override
    public void addResourceToPool(ResourceClazz inResource)
    {
        if(inResource == null) {
            throw new NullPointerException();
        }
        resources.add(inResource);
        resourceHash.add(inResource);
    }
    /**
     * Create a new <code>FIFOResourcePool</code> object.
     */
    protected FIFOResourcePool()
    {
        resources = Lists.newLinkedList();
        resourceHash = Sets.newHashSet();
    }
    /**
     * Allocates the next resource from the pool.
     *
     * @param inData an <code>Object</code> value
     * @return a <code>ResourceClazz</code> value
     */
    protected ResourceClazz allocateNextResource(Object inData)
    {
        ResourceClazz r = resources.removeFirst();
        resourceHash.remove(r);
        return r;
    }
    /**
     * Determines if the pool contains the given resource.
     * 
     * @param inResource a <code>ResourceClazz</code> value
     */
    protected boolean poolContains(ResourceClazz inResource)
    {
        return resourceHash.contains(inResource);
    }
    /**
     * the resource objects are stored here
     */
    private final Deque<ResourceClazz> resources;
    /**
     * this collection should always be in sync with {@link #resources}
     */
    private final Set<ResourceClazz> resourceHash;
}

package org.marketcetera.core.resourcepool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.marketcetera.core.Messages;

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
public abstract class FIFOResourcePool
        extends ResourcePool
{
    /**
     * the resource objects are stored here
     */
    private final LinkedList<Resource> mResources;
    /**
     * this collection should always be in sync with {@link #mResources}
     */
    private final HashSet<Resource> mResourceHash;

    /**
     * Create a new <code>FIFOResourcePool</code> object.
     */
    protected FIFOResourcePool()
    {
        super();
        mResources = new LinkedList<Resource>();
        mResourceHash = new HashSet<Resource>();
    }
    
    protected Object getPoolLock()
    {
        return mResources;
    }
    
    protected Iterator<Resource> getPoolIterator()
    {
        return mResources.iterator();
    }
    
    protected void addResourceToPool(Resource inResource)
    {
        if(inResource == null) {
            throw new NullPointerException();
        }
        mResources.add(inResource);
        mResourceHash.add(inResource);
    }
    
    protected Resource allocateNextResource(Object inData)
    {
        Resource r = mResources.removeFirst();
        mResourceHash.remove(r);
        return r;
    }
    
    protected Resource getNextResource(Object inData)
        throws ResourcePoolException
    {
        try {
            // inside the parent synchronization lock
            if(mResources.isEmpty()) {
                // try to add a resource
                Resource newResource = createResource(inData);
                addResourceToPool(newResource);
            }
            return allocateNextResource(inData);
        } catch (Throwable t) {
            throw new ResourcePoolException(t,
                                            Messages.ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL);
        }
    }
    
    protected boolean poolContains(Resource inResource)
    {
        return mResourceHash.contains(inResource);
    }    
}

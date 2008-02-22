package org.marketcetera.core.resourcepool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Implementation of {@link ResourcePool} that allocates <code>Resource</code> objects
 * in FIFO order.
 * 
 * <p><code>Resource</code> access, return, and membership check are all performed in
 * constant time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class FIFOResourcePool
        extends ResourcePool
{
    /**
     * the resource objects are stored here
     */
    private LinkedList<Resource> mResources;
    private HashSet<Resource> mResourceHash;

    /**
     * Create a new <code>FIFOResourcePool</code> object.
     */
    public FIFOResourcePool()
    {
        super();
        mResources = new LinkedList<Resource>();
        mResourceHash = new HashSet<Resource>();
    }
    
    protected Object getPoolLock()
    {
        return mResources;
    }
    
    protected int getCurrentPoolSize()
    {
        return mResources.size();
    }
    
    protected Iterator<Resource> getPoolIterator()
    {
        return mResources.iterator();
    }
    
    protected void addResource(Resource inResource) 
        throws ResourcePoolException
    {
        mResources.add(inResource);
        mResourceHash.add(inResource);
    }
    
    protected Resource getNextResource(Object inData)
        throws ResourcePoolException
    {
        Resource r = mResources.removeFirst();
        mResourceHash.remove(r);
        return r;
    }
    
    protected boolean poolContains(Resource inResource)
    {
        return mResourceHash.contains(inResource);
    }
    
    protected void emptyPool()
    {
        synchronized(mResources) {
            mResources.clear();
            mResourceHash.clear();
        }
    }
}

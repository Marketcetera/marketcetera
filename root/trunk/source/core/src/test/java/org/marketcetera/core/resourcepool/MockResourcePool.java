package org.marketcetera.core.resourcepool;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MockResourcePool
        extends ResourcePool
{
    private final LinkedList<Resource> mResources;
    private Throwable mThrowDuringGetNextResource = null;
    private boolean mThrowDuringVerify = false;
    private Boolean mThrowDuringAddToPool = null;
    private boolean mInReturn = false;
    private Throwable mThrowDuringReturnResource = null;
    
    /**
     * Create a new <code>TestResourcePool</code> object.
     *
     */
    public MockResourcePool()
    {
        mResources = new LinkedList<Resource>();        
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#addResourceToPool(org.marketcetera.core.resourcepool.Resource)
     */
    protected void addResourceToPool(Resource inResource)
    {
        // there are 3 values for mThrowDuringAddToPool: null, true, false
        // use these values for a trinary flag - null means don't throw, true means throw before, false means throw after
        // it's kludgey, but it will work
        // do this only during resource return
        if(getInReturn()) {
            Boolean flag = getThrowDuringAddToPool();
            if(flag == null ||
               flag.equals(new Boolean(false))) {
                mResources.addLast((MockResource)inResource);
            } 
            if(flag != null) {
                throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
            }
        } else {
            mResources.addLast((MockResource)inResource);
        }
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#createResource(java.lang.Object)
     */
    protected Resource createResource(Object inData)
            throws ResourcePoolException
    {
        return new MockResource();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getNextResource(java.lang.Object)
     */
    protected Resource getNextResource(Object inData)
            throws ResourcePoolException
    {
        Throwable t = getThrowDuringGetNextResource();
        if(t != null) {
            if(t instanceof ResourcePoolException) {
                throw (ResourcePoolException)t;
            } 
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        if(mResources.isEmpty()) {
            // try to add a resource
            Resource newResource = createResource(inData);
            addResourceToPool(newResource);
        }
        return mResources.removeFirst();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getPoolIterator()
     */
    protected Iterator<Resource> getPoolIterator()
    {
        return mResources.iterator();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getPoolLock()
     */
    protected Object getPoolLock()
    {
        return mResources;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#verifyResourceReturn(org.marketcetera.core.resourcepool.Resource)
     */
    protected void verifyResourceReturn(Resource inResource)
            throws ResourcePoolException
    {
        if(getThrowDuringVerify()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#rejectNewRequests()
     */
    boolean testRejectNewRequests()
    {
        return super.rejectNewRequests();
    }

    /**
     * @return the throwDuringGetNextResource
     */
    Throwable getThrowDuringGetNextResource()
    {
        return mThrowDuringGetNextResource;
    }

    /**
     * @param inThrowDuringGetNextResource the throwDuringGetNextResource to set
     */
    void setThrowDuringGetNextResource(Throwable inThrowDuringGetNextResource)
    {
        mThrowDuringGetNextResource = inThrowDuringGetNextResource;
    }
    
    void emptyPool()
    {
        Iterator<Resource> iterator = getPoolIterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    /**
     * @return the throwDuringVerify
     */
    boolean getThrowDuringVerify()
    {
        return mThrowDuringVerify;
    }

    /**
     * @param inThrowDuringVerify the throwDuringVerify to set
     */
    void setThrowDuringVerify(boolean inThrowDuringVerify)
    {
        mThrowDuringVerify = inThrowDuringVerify;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#requestResource(java.lang.Object)
     */
    protected MockResource requestResource(Object inData)
            throws ResourcePoolException
    {
        setInReturn(false);
        return (MockResource)super.requestResource(inData);
    }

    /**
     * @return the throwDuringAddToPool
     */
    Boolean getThrowDuringAddToPool()
    {
        return mThrowDuringAddToPool;
    }

    /**
     * @param inThrowDuringAddToPool the throwDuringAddToPool to set
     */
    void setThrowDuringAddToPool(Boolean inThrowDuringAddToPool)
    {
        mThrowDuringAddToPool = inThrowDuringAddToPool;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#poolContains(org.marketcetera.core.resourcepool.Resource)
     */
    protected boolean poolContains(Resource inResource)
    {
        return mResources.contains(inResource);
    }

    /**
     * @return the inReturn
     */
    boolean getInReturn()
    {
        return mInReturn;
    }

    /**
     * @param inInReturn the inReturn to set
     */
    void setInReturn(boolean inInReturn)
    {
        mInReturn = inInReturn;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#returnResource(org.marketcetera.core.resourcepool.Resource)
     */
    protected void returnResource(Resource inResource)
            throws ResourcePoolException
    {
        setInReturn(true);
        Throwable t = getThrowDuringReturnResource();
        if(t != null) {
            if(t instanceof ResourcePoolException) {
                throw (ResourcePoolException)t;
            } else {
                throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
            }
        }
        super.returnResource(inResource);
    }

    /**
     * @return the throwDuringReturnResource
     */
    Throwable getThrowDuringReturnResource()
    {
        return mThrowDuringReturnResource;
    }

    /**
     * @param inThrowDuringReturnResource the throwDuringReturnResource to set
     */
    void setThrowDuringReturnResource(Throwable inThrowDuringReturnResource)
    {
        mThrowDuringReturnResource = inThrowDuringReturnResource;
    }
}

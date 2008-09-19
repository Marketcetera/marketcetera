package org.marketcetera.core.resourcepool;

import java.util.Iterator;

class MockFIFOResourcePool
    extends FIFOResourcePool 
{
    private boolean mThrowDuringCreateResource = false;
    private boolean mThrowDuringAddResource = false;
    private boolean mThrowDuringAllocateResource = false;
    private boolean mEmptyPoolBeforeAllocation = false;
    private boolean mThrowDuringPoolContains = false;
    
    MockFIFOResourcePool()
    {
        super();
    }
    
    protected MockResource createResource(Object inData)
            throws ResourcePoolException
    {
        if(getThrowDuringCreateResource()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        return new MockResource();
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.FIFOResourcePool#addResourceToPool(org.marketcetera.core.resourcepool.Resource)
     */
    protected void addResourceToPool(Resource inResource)
    {
        if(getThrowDuringAddResource()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        super.addResourceToPool(inResource);
    }    

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.FIFOResourcePool#allocateNextResource(java.lang.Object)
     */
    protected Resource allocateNextResource(Object inData)
    {
        if(getEmptyPoolBeforeAllocation()) {
            synchronized(getPoolLock()) {
                while(getPoolSize() > 0) {
                    super.allocateNextResource(inData);
                }
            }
        }
        if(getThrowDuringAllocateResource()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        return super.allocateNextResource(inData);
    }

    int getPoolSize()
    {
        Iterator<Resource> iterator = getPoolIterator();
        int counter = 0;
        while(iterator.hasNext()) {
            iterator.next();
            counter += 1;
        }
        
        return counter;
    }

    /**
     * @return the throwDuringCreateResource
     */
    boolean getThrowDuringCreateResource()
    {
        return mThrowDuringCreateResource;
    }

    /**
     * @param inThrowDuringCreateResource the throwDuringCreateResource to set
     */
    void setThrowDuringCreateResource(boolean inThrowDuringCreateResource)
    {
        mThrowDuringCreateResource = inThrowDuringCreateResource;
    }

    /**
     * @return the throwDuringAddResource
     */
    boolean getThrowDuringAddResource()
    {
        return mThrowDuringAddResource;
    }

    /**
     * @param inThrowDuringAddResource the throwDuringAddResource to set
     */
    void setThrowDuringAddResource(boolean inThrowDuringAddResource)
    {
        mThrowDuringAddResource = inThrowDuringAddResource;
    }

    /**
     * @return the throwDuringAllocateResource
     */
    boolean getThrowDuringAllocateResource()
    {
        return mThrowDuringAllocateResource;
    }

    /**
     * @param inThrowDuringAllocateResource the throwDuringAllocateResource to set
     */
    void setThrowDuringAllocateResource(boolean inThrowDuringAllocateResource)
    {
        mThrowDuringAllocateResource = inThrowDuringAllocateResource;
    }

    /**
     * @return the emptyPoolBeforeAllocation
     */
    boolean getEmptyPoolBeforeAllocation()
    {
        return mEmptyPoolBeforeAllocation;
    }

    /**
     * @param inEmptyPoolBeforeAllocation the emptyPoolBeforeAllocation to set
     */
    void setEmptyPoolBeforeAllocation(boolean inEmptyPoolBeforeAllocation)
    {
        mEmptyPoolBeforeAllocation = inEmptyPoolBeforeAllocation;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.FIFOResourcePool#poolContains(org.marketcetera.core.resourcepool.Resource)
     */
    protected boolean poolContains(Resource inResource)
    {
        if(getThrowDuringPoolContains()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        return super.poolContains(inResource);
    }

    /**
     * @return the throwDuringPoolContains
     */
    boolean getThrowDuringPoolContains()
    {
        return mThrowDuringPoolContains;
    }

    /**
     * @param inThrowDuringPoolContains the throwDuringPoolContains to set
     */
    void setThrowDuringPoolContains(boolean inThrowDuringPoolContains)
    {
        mThrowDuringPoolContains = inThrowDuringPoolContains;
    }

}

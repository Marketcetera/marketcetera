package org.marketcetera.core.resourcepool;

import org.marketcetera.core.resourcepool.Resource;
import org.marketcetera.core.resourcepool.ResourceCreationException;
import org.marketcetera.core.resourcepool.ResourcePool;
import org.marketcetera.core.resourcepool.ResourcePoolException;
import org.marketcetera.core.resourcepool.TestResource;

public class TestFIFOResourcePool 
    extends FIFOResourcePool 
{
    private int mMaxCreationFailures = 5;
    private int mMaxResources = 10;
    private int mMinResources = 3;
    private int mTimesToFailCreationBeforeSuccess = 0;
    private int mCreationFailureCounter = 0;
    private int mActualFailureCounter = 0;
    private boolean mPoolBroken = false;
    private boolean mFixPool = false;
    private boolean mResourceCreationFailedException = false;
    
	protected Resource createResource(Object inData) 
        throws ResourceCreationException 
    {
        if((mTimesToFailCreationBeforeSuccess > 0 &&                
           mCreationFailureCounter++ < mTimesToFailCreationBeforeSuccess) ||
           getPoolBroken()) {
            throw new ResourceCreationException("This exception is expected");
        }
        resetCreationFailureCounter();
		return new TestResource();
	}
    
    private void resetCreationFailureCounter()
    {
        mCreationFailureCounter = 0;
    }

	protected int getMaxResourceCreationFailures() 
    {
		return mMaxCreationFailures;
	}

	protected int getMaxResources() 
    {
		return mMaxResources;
	}

	protected int getMinResources() 
    {
		return mMinResources;
	}

    /**
     * @param inMaxResources the maxResources to set
     */
    void setMaxResources(int inMaxResources)
    {
        mMaxResources = inMaxResources;
    }

    /**
     * @param inMinResources the minResources to set
     */
    void setMinResources(int inMinResources)
    {
        mMinResources = inMinResources;
    }

    int getResourcePoolSize()
    {
        return getCurrentPoolSize();
    }

    /**
     * @return the creationFailureCounter
     */
    int getCreationFailureCounter()
    {
        return mCreationFailureCounter;
    }

    /**
     * @return the timesToFailCreationBeforeSuccess
     */
    int getTimesToFailCreationBeforeSuccess()
    {
        return mTimesToFailCreationBeforeSuccess;
    }

    /**
     * @param inTimesToFailCreationBeforeSuccess the timesToFailCreationBeforeSuccess to set
     */
    void setTimesToFailCreationBeforeSuccess(int inTimesToFailCreationBeforeSuccess)
    {
        mTimesToFailCreationBeforeSuccess = inTimesToFailCreationBeforeSuccess;
    }

    /**
     * @return the maxCreationFailures
     */
    int getMaxCreationFailures()
    {
        return mMaxCreationFailures;
    }

    /**
     * @param inMaxCreationFailures the maxCreationFailures to set
     */
    void setMaxCreationFailures(int inMaxCreationFailures)
    {
        mMaxCreationFailures = inMaxCreationFailures;
    }

    protected void resourceCreationFailed()
            throws ResourcePoolException
    {
        super.resourceCreationFailed();
        mActualFailureCounter += 1;
        if(getFixPool()) {
            setPoolBroken(false);
        }
        if(getResourceCreationFailedException()) {
            throw new NullPointerException("This exception is expected");
        }
    }

    /**
     * @return the actualFailureCounter
     */
    int getActualFailureCounter()
    {
        return mActualFailureCounter;
    }
    
    void resetActualFailureCounter()
    {
        mActualFailureCounter = 0;
    }
    
    void emptyParentPool()
    {
        super.emptyPool();
    }

    /**
     * @return the poolBroken
     */
    private boolean getPoolBroken()
    {
        return mPoolBroken;
    }

    /**
     * @param inPoolBroken the poolBroken to set
     */
    void setPoolBroken(boolean inPoolBroken)
    {
        mPoolBroken = inPoolBroken;
    }

    /**
     * @return the fixPool
     */
    private boolean getFixPool()
    {
        return mFixPool;
    }

    /**
     * @param inFixPool the fixPool to set
     */
    void setFixPool(boolean inFixPool)
    {
        mFixPool = inFixPool;
    }

    /**
     * @return the resourceCreationFailedException
     */
    private boolean getResourceCreationFailedException()
    {
        return mResourceCreationFailedException;
    }

    /**
     * @param inResourceCreationFailedException a <code>boolean</code> value
     */
    void setResourceCreationFailedException(boolean inResourceCreationFailedException)
    {
        mResourceCreationFailedException = inResourceCreationFailedException;
    }
    
    ResourcePool.STATUS getParentStatus()
    {
        return getStatus();
    }
}

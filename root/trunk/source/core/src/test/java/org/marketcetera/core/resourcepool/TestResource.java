package org.marketcetera.core.resourcepool;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">colin</a>
 * @version $Id: $
 */
public class TestResource
        implements Resource, Comparable
{
    static enum STATE { UNITIALIZED, INITIALIZED, ALLOCATED, DAMAGED, RETURNED, RELEASED, SHUTDOWN };
    
    private STATE mState;
    private int mID;
    private static Throwable sInitializeException = null;
    private static Throwable sAllocateException = null;
    private Throwable mReturnException = null;
    private Throwable mReleaseException = null;
    private boolean mIsFunctionalException = false;
    private int mContentionCounter;
    
    private static int sIDCounter = 0;
    private String mUser;
    private String mPassword;
    
    /**
     * Create a new TestResource instance.
     */
    public TestResource()
    {
        setState(STATE.UNITIALIZED);
        setID(getNextID());
        setContentionCounter(0);
    }
    
    public TestResource(String inUser,
                        String inPassword)
    {
        this();
        setUser(inUser);
        setPassword(inPassword);
    }

    /**
     * Returns the object state.
     * 
     * @return a <code>TestResource.STATE</code> value
     */
    public STATE getState()
    {
        return mState;
    }

    /**
     * Sets the object state.
     * 
     * @param inState a <code>TestResource.STATE</code> value
     */
    void setState(STATE inState)
    {
        mState = inState;
    }

    public void allocated(ResourcePool inPool)
        throws Throwable
    {
        if(getAllocateException() != null) {
            throw getAllocateException();
        }
        setState(STATE.ALLOCATED);        
    }

    public void initialize(ResourcePool inPool)
        throws Throwable
    {
        if(getInitializeException() != null) {
            throw getInitializeException();
        }
        setState(STATE.INITIALIZED);
    }

    public boolean isFunctional()
    {
        if(getIsFunctionalException()) {
            throw new NullPointerException("This exception is expected");
        }
        return !getState().equals(STATE.DAMAGED);
    }

    public void released(ResourcePool inPool)
        throws Throwable
    {
        if(getReleaseException() != null) {
            throw getReleaseException();
        }
        setState(STATE.RELEASED);
    }

    public void returned(ResourcePool inPool)
        throws Throwable
    {
        if(getReturnException() != null) {
            throw getReturnException();
        }
        setState(STATE.RETURNED);
    }

    public int compareTo(Object inOther)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(inOther == null) {
            return AFTER;
        }
        
        if(!(inOther instanceof TestResource)) {
            return BEFORE;
        }

        if (this == inOther) return EQUAL;
        
        TestResource other = (TestResource)inOther;
        return (new Integer(getID()).compareTo(new Integer(other.getID())));
    }

    /**
     * @return the iD
     */
    int getID()
    {
        return mID;
    }

    /**
     * @param inId the iD to set
     */
    void setID(int inId)
    {
        mID = inId;
    }

    private static int getNextID()
    {
        return ++sIDCounter;
    }

    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + mID;
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TestResource other = (TestResource) obj;
        if (mID != other.mID)
            return false;
        return true;
    }

    /**
     * @return the initializeException
     */
    private static Throwable getInitializeException()
    {
        return sInitializeException;
    }

    /**
     * @param inInitializeException the initializeException to set
     */
    static void setInitializeException(Throwable inInitializeException)
    {
        sInitializeException = inInitializeException;
    }

    /**
     * @return the allocateException
     */
    private static Throwable getAllocateException()
    {
        return sAllocateException;
    }

    /**
     * @param inAllocateException the allocateException to set
     */
    static void setAllocateException(Throwable inAllocateException)
    {
        sAllocateException = inAllocateException;
    }

    /**
     * @return the releaseException
     */
    private Throwable getReleaseException()
    {
        return mReleaseException;
    }

    /**
     * @param inReleaseException the releaseException to set
     */
    void setReleaseException(Throwable inReleaseException)
    {
        mReleaseException = inReleaseException;
    }

    /**
     * @return the returnException
     */
    private Throwable getReturnException()
    {
        return mReturnException;
    }

    /**
     * @param inReturnException the returnException to set
     */
    void setReturnException(Throwable inReturnException)
    {
        mReturnException = inReturnException;
    }
    
    public String toString()
    {
        return "r" + getID();
    }

    /**
     * @return the isFunctionalException
     */
    private boolean getIsFunctionalException()
    {
        return mIsFunctionalException;
    }

    /**
     * @param inIsFunctionalException the isFunctionalException to set
     */
    void setIsFunctionalException(boolean inIsFunctionalException)
    {
        mIsFunctionalException = inIsFunctionalException;
    }

    public void shutdown(ResourcePool inPool)
        throws Throwable
    {
        setState(STATE.SHUTDOWN);
    }

    /**
     * @return the password
     */
    String getPassword()
    {
        return mPassword;
    }

    /**
     * @return the user
     */
    String getUser()
    {
        return mUser;
    }

    /**
     * @param inPassword the password to set
     */
    protected void setPassword(String inPassword)
    {
        mPassword = inPassword;
    }

    /**
     * @param inUser the user to set
     */
    protected void setUser(String inUser)
    {
        mUser = inUser;
    }

    /**
     * @return the contentionCounter
     */
    int getContentionCounter()
    {
        return mContentionCounter;
    }

    /**
     * @param inContentionCounter the contentionCounter to set
     */
    void setContentionCounter(int inContentionCounter)
    {
        mContentionCounter = inContentionCounter;
    }
}

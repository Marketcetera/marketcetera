package org.marketcetera.core.resourcepool;

/**
 * Sample implementation of {@link Resource} for testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">colin</a>
 * @version $Id$
 */
public class MockResource
        implements Resource, Comparable
{
    static enum STATE { UNITIALIZED, INITIALIZED, ALLOCATED, DAMAGED, RETURNED, RELEASED, SHUTDOWN };
    
    private STATE mState;
    private int mID;
    private static Throwable sInitializeException = null;
    private static Throwable sAllocateException = null;
    private boolean mReturnException = false;
    private boolean mReleaseException = false;
    private boolean mIsFunctionalException = false;
    private long mContentionStamp;
    private boolean mThrowDuringStop = false;
    
    private static int sIDCounter = 0;
    private String mUser;
    private String mPassword;
    
    /**
     * Create a new TestResource instance.
     */
    public MockResource()
    {
        this("user_" + System.nanoTime(), //$NON-NLS-1$
             "password_" + System.nanoTime()); //$NON-NLS-1$
    }
    
    public MockResource(String inUser,
                        String inPassword)
    {
        setState(STATE.UNITIALIZED);
        setID(getNextID());
        setContentionStamp(0);
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

    public void allocated()
        throws Throwable
    {
        if(getAllocateException() != null) {
            throw getAllocateException();
        }
        setState(STATE.ALLOCATED);        
    }

    public boolean isFunctional()
    {
        if(getIsFunctionalException()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        return !getState().equals(STATE.DAMAGED);
    }

    public void released()
        throws Throwable
    {
        setState(STATE.RELEASED);
        if(getReleaseException()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
    }

    public void returned()
        throws Throwable
    {
        setState(STATE.RETURNED);
        if(getReturnException()) {
            throw new NullPointerException();
        }
    }

    public int compareTo(Object inOther)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(inOther == null) {
            return AFTER;
        }
        
        if(!(inOther instanceof MockResource)) {
            return BEFORE;
        }

        if (this == inOther) return EQUAL;
        
        MockResource other = (MockResource)inOther;
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
        final MockResource other = (MockResource) obj;
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
    private boolean getReleaseException()
    {
        return mReleaseException;
    }

    /**
     * @param inReleaseException the releaseException to set
     */
    void setReleaseException(boolean inReleaseException)
    {
        mReleaseException = inReleaseException;
    }

    /**
     * @return the returnException
     */
    private boolean getReturnException()
    {
        return mReturnException;
    }

    /**
     * @param inReturnException the returnException to set
     */
    void setReturnException(boolean inReturnException)
    {
        mReturnException = inReturnException;
    }
    
    public String toString()
    {
        return "r" + getID(); //$NON-NLS-1$
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
    long getContentionStamp()
    {
        return mContentionStamp;
    }

    void setContentionStamp(long inContentionStamp)
    {
        mContentionStamp = inContentionStamp;
    }

    public boolean isRunning()
    {
        return getState().equals(STATE.ALLOCATED) ||
               getState().equals(STATE.INITIALIZED) ||
               getState().equals(STATE.RETURNED);
    }

    public void start()
    {
        if(getInitializeException() != null) {
            throw new NullPointerException();
        }
        setState(STATE.INITIALIZED);
    }

    public void stop()
    {
        setState(STATE.SHUTDOWN);
        if(getThrowDuringStop()) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
    }

    public void afterPropertiesSet()
            throws Exception
    {
    }

    /**
     * @return the throwDuringStop
     */
    boolean getThrowDuringStop()
    {
        return mThrowDuringStop;
    }

    /**
     * @param inThrowDuringStop the throwDuringStop to set
     */
    void setThrowDuringStop(boolean inThrowDuringStop)
    {
        mThrowDuringStop = inThrowDuringStop;
    }
}

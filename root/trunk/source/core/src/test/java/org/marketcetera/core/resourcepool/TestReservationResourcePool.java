package org.marketcetera.core.resourcepool;



public class TestReservationResourcePool
        extends ReservationResourcePool
{
    private boolean mReturnNullRenderKey;
    private boolean mRenderThrowsDuringCreate;
    private boolean mRenderThrowsDuringReturn;
    private boolean mInCreate;
    private boolean mInReturn;
    private int mTestMaxResourceCreationFailures;
    private int mTestMaxResources;
    private int mTestMinResources;
    private Throwable mCreateResourceException;
    private Throwable mResourceContentionException;
    
    public TestReservationResourcePool() 
        throws InterruptedException
    {
        setReturnNullRenderKey(false);
        setRenderThrowsDuringCreate(false);
        setRenderThrowsDuringReturn(false);
        setInCreate(false);
        setInReturn(false);
        setTestMaxResourceCreationFailures(10);
        setTestMaxResources(5);
        setTestMinResources(2);
        setCreateResourceException(null);
        setResourceContentionException(null);
    }

    protected Object renderReservationKey(Resource inResource)
    {
        if(getReturnNullRenderKey()) {
            return null;
        }
        if(getRenderThrowsDuringCreate() &&
           getInCreate()) {
            throw new NullPointerException();
        }
        if(getRenderThrowsDuringReturn() &&
           getInReturn()) {
            throw new NullPointerException();
        }
        TestResource r = (TestResource)inResource;
        return new ReservationData(r.getUser(),
                                   r.getPassword());
    }
    
    Resource lookupParentReservation(Object inKey)
    {
        return lookupReservation(inKey);
    }
    
    protected Resource createResource(Object inData)
            throws ResourceCreationException
    {
        setInCreate(true);
        setInReturn(false);
        if(getCreateResourceException() != null) {
            if(getCreateResourceException() instanceof ResourceCreationException)
                throw ((ResourceCreationException)getCreateResourceException());
            else {
                throw new NullPointerException("this exception is expected");
            }
        }
        if(inData != null) {
            ReservationData data = (ReservationData)inData;
            return new TestResource(data.getUsername(),
                                    data.getPassword());
        }
        return new TestResource("user_" + System.nanoTime(),
                                "password_" + System.nanoTime());
    }
    
    public void returnResource(Resource inResource)
            throws ResourcePoolException
    {
        setInCreate(false);
        setInReturn(true);
        super.returnResource(inResource);
    }

    protected int getMaxResourceCreationFailures()
    {
        return getTestMaxResourceCreationFailures();
    }

    protected int getMaxResources()
    {
        return getTestMaxResources();
    }

    protected int getMinResources()
    {
        return getTestMinResources();
    }

    /**
     * @return the returnNullRenderKey
     */
    protected boolean getReturnNullRenderKey()
    {
        return mReturnNullRenderKey;
    }

    /**
     * @param inReturnNullRenderKey the returnNullRenderKey to set
     */
    void setReturnNullRenderKey(boolean inReturnNullRenderKey)
    {
        mReturnNullRenderKey = inReturnNullRenderKey;
    }

    /**
     * @return the testMaxResourceCreationFailures
     */
    protected int getTestMaxResourceCreationFailures()
    {
        return mTestMaxResourceCreationFailures;
    }

    /**
     * @return the testMaxResources
     */
    protected int getTestMaxResources()
    {
        return mTestMaxResources;
    }

    /**
     * @return the testMinResources
     */
    protected int getTestMinResources()
    {
        return mTestMinResources;
    }

    /**
     * @param inTestMaxResourceCreationFailures the testMaxResourceCreationFailures to set
     */
    void setTestMaxResourceCreationFailures(int inTestMaxResourceCreationFailures)
    {
        mTestMaxResourceCreationFailures = inTestMaxResourceCreationFailures;
    }

    /**
     * @param inTestMaxResources the testMaxResources to set
     */
    void setTestMaxResources(int inTestMaxResources)
    {
        mTestMaxResources = inTestMaxResources;
    }

    /**
     * @param inTestMinResources the testMinResources to set
     */
    void setTestMinResources(int inTestMinResources)
    {
        mTestMinResources = inTestMinResources;
    }

    /**
     * @return the renderThrowsDuringCreate
     */
    boolean getRenderThrowsDuringCreate()
    {
        return mRenderThrowsDuringCreate;
    }

    /**
     * @param inRenderThrowsDuringCreate the renderThrowsDuringCreate to set
     */
    void setRenderThrowsDuringCreate(boolean inRenderThrowsDuringCreate)
    {
        mRenderThrowsDuringCreate = inRenderThrowsDuringCreate;
    }

    /**
     * @return the renderThrowsDuringReturn
     */
    boolean getRenderThrowsDuringReturn()
    {
        return mRenderThrowsDuringReturn;
    }

    /**
     * @param inRenderThrowsDuringReturn the renderThrowsDuringReturn to set
     */
    void setRenderThrowsDuringReturn(boolean inRenderThrowsDuringReturn)
    {
        mRenderThrowsDuringReturn = inRenderThrowsDuringReturn;
    }

    /**
     * @return the inCreate
     */
    boolean getInCreate()
    {
        return mInCreate;
    }

    /**
     * @param inInCreate the inCreate to set
     */
    void setInCreate(boolean inInCreate)
    {
        mInCreate = inInCreate;
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
    
    static class ReservationData
    {
        private String mUsername;
        private String mPassword;
        
        ReservationData(String inUsername,
                        String inPassword)
        {
            setUsername(inUsername);
            setPassword(inPassword);
        }

        /**
         * @param inPassword the password to set
         */
        private void setPassword(String inPassword)
        {
            mPassword = inPassword;
        }

        /**
         * @param inUsername the username to set
         */
        private void setUsername(String inUsername)
        {
            mUsername = inUsername;
        }

        /**
         * @return the password
         */
        String getPassword()
        {
            return mPassword;
        }

        /**
         * @return the username
         */
        String getUsername()
        {
            return mUsername;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((mPassword == null) ? 0 : mPassword.hashCode());
            result = PRIME * result + ((mUsername == null) ? 0 : mUsername.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final ReservationData other = (ReservationData) obj;
            if (mPassword == null) {
                if (other.mPassword != null)
                    return false;
            } else if (!mPassword.equals(other.mPassword))
                return false;
            if (mUsername == null) {
                if (other.mUsername != null)
                    return false;
            } else if (!mUsername.equals(other.mUsername))
                return false;
            return true;
        }
        
        public String toString()
        {
            return getUsername() + "/" + getPassword();
        }
    }

    /**
     * @return the createResourceException
     */
    Throwable getCreateResourceException()
    {
        return mCreateResourceException;
    }

    /**
     * @param inCreateResourceException the createResourceException to set
     */
    void setCreateResourceException(Throwable inCreateResourceException)
    {
        mCreateResourceException = inCreateResourceException;
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ReservationResourcePool#resourceContention(java.lang.Object, org.marketcetera.core.resourcepool.Resource)
     */
    protected void resourceContention(Object inData,
                                      Resource inDesiredResource)
            throws ResourcePoolException
    {
        super.resourceContention(inData, 
                                 inDesiredResource);
        TestResource r = (TestResource)inDesiredResource;
        r.setContentionStamp(System.currentTimeMillis());
        dumpReservationTable();
        dumpResourcePool();
        Throwable t = getResourceContentionException();
        if(t != null) {
            if(t instanceof ResourcePoolException) {
                throw ((ResourcePoolException)t);
            } else {
                throw new NullPointerException("This exception is expected");
            }
        }
    }

    /**
     * @return the resourceContentionException
     */
    Throwable getResourceContentionException()
    {
        return mResourceContentionException;
    }

    /**
     * @param inResourceContentionException the resourceContentionException to set
     */
    void setResourceContentionException(Throwable inResourceContentionException)
    {
        mResourceContentionException = inResourceContentionException;
    }
}

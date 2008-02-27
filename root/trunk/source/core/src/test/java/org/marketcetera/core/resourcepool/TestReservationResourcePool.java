package org.marketcetera.core.resourcepool;


public class TestReservationResourcePool
        extends ReservationResourcePool
{
    protected Resource createResource(Object inData)
            throws ResourcePoolException
    {
        ReservationData data = (ReservationData)inData;
        return new TestResource(data.getUser(),
                                data.getPassword());
    }

    protected Object renderReservationKey(Resource inResource)
    {
        TestResource r = (TestResource) inResource;
        return new ReservationData(r.getUser(),
                                   r.getPassword());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "TestReservationResourcePool(" + hashCode() + ")";
    }

    static class ReservationData
    {
        private String mUser;
        private String mPassword;
        
        ReservationData(String inUser,
                        String inPassword)
        {
            setUser(inUser);
            setPassword(inPassword);
        }

        /**
         * @return the password
         */
        String getPassword()
        {
            return mPassword;
        }

        /**
         * @param inPassword the password to set
         */
        void setPassword(String inPassword)
        {
            mPassword = inPassword;
        }

        /**
         * @return the user
         */
        String getUser()
        {
            return mUser;
        }

        /**
         * @param inUser the user to set
         */
        void setUser(String inUser)
        {
            mUser = inUser;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((mPassword == null) ? 0 : mPassword.hashCode());
            result = PRIME * result + ((mUser == null) ? 0 : mUser.hashCode());
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
            if (mUser == null) {
                if (other.mUser != null)
                    return false;
            } else if (!mUser.equals(other.mUser))
                return false;
            return true;
        }
        
        public String toString()
        {
            return "ReservationData(" + hashCode() + ") " + getUser() + "/" + getPassword();
        }
    }
}

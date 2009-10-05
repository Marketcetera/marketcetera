package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * A session holder. It is created after successful authentication,
 * and initially retains just the authenticated user name and client
 * context at that time. In later calls during the same session, the
 * service implementations may associate session data with the holder;
 * it is the responsibility of the service implementations to ensure
 * thread safety in manipulating that data.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SessionHolder<T>
{

    // INSTANCE DATA.

    private final String mUser;
    private final StatelessClientContext mCreationContext;
    private long mLastAccess;
    private T mSession;


    // CONSTRUCTORS.

    /**
     * Creates a new holder with the given authenticated user name and
     * creation context.
     *
     * @param user The user name.
     * @param creationContext The context.
     */

    public SessionHolder
        (String user,
         StatelessClientContext creationContext)
    {
        mUser=user;
        mCreationContext=creationContext;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's user.
     *
     * @return The user.
     */

    public String getUser()
    {
        return mUser;
    }

    /**
     * Returns the receiver's creation context.
     *
     * @return The context.
     */

    public StatelessClientContext getCreationContext()
    {
        return mCreationContext;
    }

    /**
     * Sets the receiver's most recent access timestamp to the present
     * time.
     */

    void markAccess()
    {
        mLastAccess=System.currentTimeMillis();
    }

    /**
     * Returns the receiver's most recent access timestamp.
     *
     * @return The timestamp.
     */

    long getLastAccess()
    {
        return mLastAccess;
    }

    /**
     * Sets the receiver's session data to the given value.
     *
     * @param session The data, which may be null.
     */

    public void setSession
        (T session)
    {
        mSession=session;
    }

    /**
     * Returns the receiver's session data.
     *
     * @return The data, which may be null.
     */

    public T getSession()
    {
        return mSession;
    }
}

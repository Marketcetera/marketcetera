package org.marketcetera.ors;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The principals associated with a trade report.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Principals
{

    // CLASS DATA.

    /**
     * A sentinel value indicating unknown principals.
     */

    public static final Principals UNKNOWN=
        new Principals(null,null);


    // INSTANCE DATA.

    private final UserID mActorID;
    private final UserID mViewerID;


    // CONSTRUCTORS.

    /**
     * Creates a new principals container, for the given actor and
     * viewer IDs.
     *
     * @param actorID The actorID. It may be null.
     * @param viewerID The viewerID. It may be null.
     */

    public Principals
        (UserID actorID,
         UserID viewerID)
    {
        mActorID=actorID;
        mViewerID=viewerID;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's actor ID.
     *
     * @return The actor ID. It may be null.
     */

    public UserID getActorID()
    {
        return mActorID;
    }

    /**
     * Returns the receiver's viewer ID.
     *
     * @return The viewer ID. It may be null.
     */

    public UserID getViewerID()
    {
        return mViewerID;
    }


    // Object.

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getActorID())+
                ObjectUtils.hashCode(getViewerID()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        Principals o=(Principals)other;
        return (ObjectUtils.equals(getActorID(),o.getActorID()) &&
                ObjectUtils.equals(getViewerID(),o.getViewerID()));
    }
}

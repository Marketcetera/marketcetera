package org.marketcetera.ors.info;

import org.marketcetera.security.User;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A store for key-value pairs specific to a session.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SessionInfo
    extends ReadWriteInfo
{

    /**
     * The {@link SessionId} key for the session.
     */

    static final String SESSION_ID=
        "SESSION_ID"; //$NON-NLS-1$

    /**
     * The {@link UserID} key for the session's actor.
     */

    static final String ACTOR_ID=
        "ACTOR_ID"; //$NON-NLS-1$

    /**
     * The {@link User} key for the session's actor.
     */

    static final String ACTOR=
        "ACTOR"; //$NON-NLS-1$

    /**
     * Returns the receiver's system store.
     *
     * @return The system store.
     */

    SystemInfo getSystemInfo();
}
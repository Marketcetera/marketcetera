package org.marketcetera.web.events;

import org.marketcetera.web.SessionUser;

/* $License$ */

/**
 * Indicates that current user session has newly started.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LoginEvent
{
    /**
     * Create a new LoginEvent instance.
     *
     * @param inSessionUser a <code>SessionUser</code> value
     */
    public LoginEvent(SessionUser inSessionUser)
    {
        sessionUser = inSessionUser;
        message = new StringBuilder().append(sessionUser).append(" logged in").toString();
    }
    /**
     * Get the sessionUser value.
     *
     * @return a <code>SessionUser</code> value
     */
    public SessionUser getSessionUser()
    {
        return sessionUser;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return message;
    }
    /**
     * describes this event
     */
    private final String message;
    /**
     * session user value
     */
    private final SessionUser sessionUser;
}

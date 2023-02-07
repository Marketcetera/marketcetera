package org.marketcetera.web.events;

import org.marketcetera.webui.security.AuthenticatedUser;

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
     * @param inAuthenticatedUser a <code>AuthenticatedUser</code> value
     */
    public LoginEvent(AuthenticatedUser inAuthenticatedUser)
    {
        AuthenticatedUser = inAuthenticatedUser;
        message = new StringBuilder().append(AuthenticatedUser).append(" logged in").toString();
    }
    /**
     * Get the AuthenticatedUser value.
     *
     * @return a <code>AuthenticatedUser</code> value
     */
    public AuthenticatedUser getAuthenticatedUser()
    {
        return AuthenticatedUser;
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
    private final AuthenticatedUser AuthenticatedUser;
}

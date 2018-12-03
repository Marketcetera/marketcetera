package org.marketcetera.dare;

import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Provides information for a DARE client session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DareClientSession
{
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionId</code> value
     */
    public SessionId getSessionId()
    {
        return sessionId;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Create a new DareClientSession instance.
     *
     * @param inSessionId a <code>SessionId</code> value
     * @param inUsername a <code>String</code> value
     */
    DareClientSession(SessionId inSessionId,
                      String inUsername)
    {
        sessionId = inSessionId;
        username = inUsername;
    }
    /**
     * session id value
     */
    private final SessionId sessionId;
    /**
     * username value
     */
    private final String username;
}

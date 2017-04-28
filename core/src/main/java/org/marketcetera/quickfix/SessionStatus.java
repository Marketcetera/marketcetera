package org.marketcetera.quickfix;

import quickfix.SessionID;

/* $License$ */

/**
 * Notification provided upon session status change.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionStatus
{
    /**
     * Create a new SessionNotification instance.
     *
     * @param inInSessionId a <code>SessionID</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     * @param inIsInitiator a <code>boolean</code> value
     */
    public SessionStatus(SessionID inInSessionId,
                         boolean inIsInitiator,
                         boolean inIsLoggedOn)
    {
        inSessionId = inInSessionId;
        isLoggedOn = inIsLoggedOn;
        isInitiator = inIsInitiator;
    }
    /**
     * Get the inSessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    public SessionID getSessionId()
    {
        return inSessionId;
    }
    /**
     * Sets the inSessionId value.
     *
     * @param inInSessionId a <code>SessionID</code> value
     */
    public void setInSessionId(SessionID inInSessionId)
    {
        inSessionId = inInSessionId;
    }
    /**
     * Get the isLoggedOn value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsLoggedOn()
    {
        return isLoggedOn;
    }
    /**
     * Sets the isLoggedOn value.
     *
     * @param inIsLoggedOn a <code>boolean</code> value
     */
    public void setIsLoggedOn(boolean inIsLoggedOn)
    {
        isLoggedOn = inIsLoggedOn;
    }
    /**
     * Get the isInitiator value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsInitiator()
    {
        return isInitiator;
    }
    /**
     * Sets the isInitiator value.
     *
     * @param inIsInitiator a <code>boolean</code> value
     */
    public void setIsInitiator(boolean inIsInitiator)
    {
        isInitiator = inIsInitiator;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SessionNotification [").append(isInitiator?"initiator ":"acceptor ").append(inSessionId).append(' ').append(isLoggedOn).append(']');
        return builder.toString();
    }
    /**
     * session ID value
     */
    private SessionID inSessionId;
    /**
     * is logged on value
     */
    private boolean isLoggedOn;
    /**
     * indicates if the status is an initiator or not
     */
    private boolean isInitiator;
}

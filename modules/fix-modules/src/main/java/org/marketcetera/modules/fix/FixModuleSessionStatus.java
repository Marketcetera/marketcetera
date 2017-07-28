package org.marketcetera.modules.fix;

import quickfix.SessionID;

/* $License$ */

/**
 * Indicates the status of a given FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixModuleSessionStatus
{
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * Get the isCreated value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsCreated()
    {
        return isCreated;
    }
    /**
     * Get the isAvailable value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsAvailable()
    {
        return isAvailable;
    }
    /**
     * Create a new FixModuleSessionStatus instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inIsCreated a <code>boolean</code> value
     * @param inIsAvailable a <code>boolean</code> value
     */
    public FixModuleSessionStatus(SessionID inSessionId,
                                  boolean inIsCreated,
                                  boolean inIsAvailable)
    {
        sessionId = inSessionId;
        isCreated = inIsCreated;
        isAvailable = inIsAvailable;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixModuleSessionStatus [sessionId=").append(sessionId).append(", isCreated=").append(isCreated)
                .append(", isAvailable=").append(isAvailable).append("]");
        return builder.toString();
    }
    /**
     * session id value
     */
    private final SessionID sessionId;
    /**
     * is created value
     */
    private final boolean isCreated;
    /**
     * is available value
     */
    private final boolean isAvailable;
}

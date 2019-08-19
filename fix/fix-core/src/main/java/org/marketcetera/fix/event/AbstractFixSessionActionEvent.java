package org.marketcetera.fix.event;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides common behavior for {@link FixSessionActionEvent} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractFixSessionActionEvent
        implements FixSessionActionEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasSessionId#getSessionId()
     */
    @Override
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * Get the type value.
     *
     * @return a <code>String</code> value
     */
    public String getType()
    {
        return type;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append("[").append(getSessionId()).append("]");
        return builder.toString();
    }
    /**
     * Create a new AbstractFixSessionActionEvent instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    protected AbstractFixSessionActionEvent(SessionID inSessionId)
    {
        sessionId = inSessionId;
        type = getClass().getSimpleName();
    }
    /**
     * indicates the type of action
     */
    private final String type;
    /**
     * session id value
     */
    private final SessionID sessionId;
}

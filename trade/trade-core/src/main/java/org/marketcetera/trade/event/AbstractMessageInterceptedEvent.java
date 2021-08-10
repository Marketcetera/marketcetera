package org.marketcetera.trade.event;

/* $License$ */

/**
 * Provides common behavior for {@link MessageInterceptedEvent} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractMessageInterceptedEvent
        implements MessageInterceptedEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasSessionId#getSessionId()
     */
    @Override
    public quickfix.SessionID getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [sessionId=").append(sessionId).append(", message=")
                .append(message).append("]");
        return builder.toString();
    }
    /**
     * Create a new AbstractMessageInterceptedEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    protected AbstractMessageInterceptedEvent(quickfix.SessionID inSessionId,
                                              quickfix.Message inMessage)
    {
        sessionId = inSessionId;
        message = inMessage;
    }
    /**
     * message value
     */
    private final quickfix.Message message;
    /**
     * session id value
     */
    private final quickfix.SessionID sessionId;
}

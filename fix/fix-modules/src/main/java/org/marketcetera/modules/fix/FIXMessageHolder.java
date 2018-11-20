package org.marketcetera.modules.fix;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Holds a FIX message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FIXMessageHolder
        implements HasFIXMessage,HasSessionId
{
    /**
     * Create a new FIXMessageHolder instance.
     */
    public FIXMessageHolder() {}
    /**
     * Create a new FIXMessageHolder instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     */
    public FIXMessageHolder(SessionID inSessionId,
                            Message inMessage)
    {
        sessionId = inSessionId;
        message = inMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public Message getMessage()
    {
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasSessionId#getSessionId()
     */
    @Override
    public SessionID getSessionId()
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
        builder.append("FIXMessageHolder [sessionId=").append(sessionId).append(", message=").append(message)
                .append("]");
        return builder.toString();
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>Message</code> value
     */
    public void setMessage(Message inMessage)
    {
        message = inMessage;
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public void setSessionId(SessionID inSessionId)
    {
        sessionId = inSessionId;
    }
    /**
     * message value
     */
    private Message message;
    /**
     * session ID value
     */
    private SessionID sessionId;
}

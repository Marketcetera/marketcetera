package org.marketcetera.trade.modules;

import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.HasSessionId;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.HasBrokerID;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Contains a FIX message and its system owner.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OwnedMessage
        implements HasFIXMessage,HasUser,HasBrokerID,HasSessionId
{
    /**
     * Create a new OwnedMessage instance.
     */
    public OwnedMessage() {}
    /**
     * Create a new OwnedMessage instance.
     *
     * @param inUser a <code>User</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     */
    public OwnedMessage(User inUser,
                        BrokerID inBrokerId,
                        SessionID inSessionId,
                        Message inMessage)
    {
        user = inUser;
        brokerId = inBrokerId;
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
     * @see org.marketcetera.admin.HasUser#getUser()
     */
    @Override
    public User getUser()
    {
        return user;
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
     * @see org.marketcetera.trade.HasBrokerID#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OwnedMessage [user=").append(user).append(", brokerId=").append(brokerId).append(", sessionId=")
                .append(sessionId).append(", message=").append(message).append("]");
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
     * Sets the user value.
     *
     * @param inUser a <code>User</code> value
     */
    public void setUser(User inUser)
    {
        user = inUser;
    }
    /**
     * Get the brokerId value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /**
     * Sets the brokerId value.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     */
    public void setBrokerId(BrokerID inBrokerId)
    {
        brokerId = inBrokerId;
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
     * FIX message value
     */
    private Message message;
    /**
     * owner value
     */
    private User user;
    /**
     * broker id value
     */
    private BrokerID brokerId;
    /**
     * session id value
     */
    private SessionID sessionId;
}

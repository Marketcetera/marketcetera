package org.marketcetera.ors.ws;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.jms.core.JmsOperations;

/**
 * The session information maintained for each client.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ClientSession
{

    // INSTANCE DATA.

    private final SessionId mSessionId;
    private final SimpleUser mUser;
    private final JmsOperations mReplyTopic;


    // CONSTRUCTOR.

    /**
     * Creates a new session which retains the given session ID, the
     * given user associated with the session, and the given topic for
     * reply delivery.
     *
     * @param sessionId The session ID.
     * @param user The user.
     * @param replyTopic The topic.
     */
    
    public ClientSession
        (SessionId sessionId,
         SimpleUser user,
         JmsOperations replyTopic)
    {
        mSessionId=sessionId;
        mUser=user;
        mReplyTopic=replyTopic;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's session ID.
     *
     * @return The session ID.
     */
    
    public SessionId getSessionId()
    {
        return mSessionId;
    }

    /**
     * Returns the receiver's user.
     *
     * @return The user.
     */
    
    public SimpleUser getUser()
    {
        return mUser;
    }

    /**
     * Returns the receiver's reply topic.
     *
     * @return The topic.
     */
    
    public JmsOperations getReplyTopic()
    {
        return mReplyTopic;
    }

    // Object.

    @Override
    public String toString()
    {
        return Messages.CLIENT_SESSION_STRING.getText
            (getSessionId(),getUser().getUserID());
    }
}

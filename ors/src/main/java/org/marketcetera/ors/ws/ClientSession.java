package org.marketcetera.ors.ws;

import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.info.SessionInfoImpl;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.ors.security.User;
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
    private final User mUser;
    private final JmsOperations mReplyTopic;
    private final SessionInfo mSessionInfo;


    // CONSTRUCTOR.

    /**
     * Creates a new session which uses the given system information
     * to create its session information, and retains the given
     * session ID, the given user associated with the session, and the
     * given topic for reply delivery.
     *
     * @param systemInfo The system information.
     * @param sessionId The session ID.
     * @param user The user.
     * @param replyTopic The topic.
     */
    
    public ClientSession
        (SystemInfo systemInfo,
         SessionId sessionId,
         User user,
         JmsOperations replyTopic)
    {
        mSessionId=sessionId;
        mUser=user;
        mReplyTopic=replyTopic;
        mSessionInfo=new SessionInfoImpl(systemInfo);
        getSessionInfo().setValue
            (SessionInfo.SESSION_ID,getSessionId());
        getSessionInfo().setValue
            (SessionInfo.ACTOR,getUser());
        getSessionInfo().setValue
            (SessionInfo.ACTOR_ID,getUser().getUserID());
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
    
    public User getUser()
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

    /**
     * Returns the receiver's session information.
     *
     * @return The information.
     */

    public SessionInfo getSessionInfo()
    {
        return mSessionInfo;
    }


    // Object.

    @Override
    public String toString()
    {
        return Messages.CLIENT_SESSION_STRING.getText
            (getSessionId(),getUser().getUserID());
    }
}

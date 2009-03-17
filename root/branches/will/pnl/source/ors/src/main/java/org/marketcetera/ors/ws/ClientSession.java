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

@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientSession
{

    // INSTANCE DATA.

    private final SessionId mSessionId;
    private final SimpleUser mUser;
    private final JmsOperations mReplyTopic;


    // CONSTRUCTOR.

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

    public SessionId getSessionId()
    {
        return mSessionId;
    }

    public SimpleUser getUser()
    {
        return mUser;
    }

    public JmsOperations getReplyTopic()
    {
        return mReplyTopic;
    }
}

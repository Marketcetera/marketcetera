package org.marketcetera.server.session;

import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ServerSession
{
    /**
     * Create a new ServerSession instance.
     */
    ServerSession() {}
    /**
     * Create a new ServerSession instance.
     *
     * @param inContext
     * @param inUserId
     * @param inSessionId
     */
    ServerSession(StatelessClientContext inContext,
                  String inUserId,
                  SessionId inSessionId)
    {
        context = inContext;
        userId = inUserId;
        sessionId = inSessionId;
    }
    /**
     * Get the context value.
     *
     * @return a <code>StatelessClientContext</code> value
     */
    public StatelessClientContext getContext()
    {
        return context;
    }
    /**
     * Sets the context value.
     *
     * @param inContext a <code>StatelessClientContext</code> value
     */
    public void setContext(StatelessClientContext inContext)
    {
        context = inContext;
    }
    /**
     * Get the userId value.
     *
     * @return a <code>String</code> value
     */
    public String getUserId()
    {
        return userId;
    }
    /**
     * Sets the userId value.
     *
     * @param inUserId a <code>String</code> value
     */
    public void setUserId(String inUserId)
    {
        userId = inUserId;
    }
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
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>SessionId</code> value
     */
    public void setSessionId(SessionId inSessionId)
    {
        sessionId = inSessionId;
    }
    /**
     * 
     */
    private StatelessClientContext context;
    /**
     * 
     */
    private String userId;
    /**
     * 
     */
    private SessionId sessionId;
}

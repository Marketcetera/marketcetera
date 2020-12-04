package org.marketcetera.util.ws.stateful;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * The client context which the client must supply as an argument to
 * every stateful remote call. {@link Client#getContext()} is the
 * preferred way for service clients to obtain a ready-to-use
 * context. It conveys key (but optional) information about the client
 * to the server.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ClientContext
        extends StatelessClientContext
{
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
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    @Override
    public String toString()
    {
        return Messages.CLIENT_CONTEXT.getText(super.toString(),getSessionId());
    }
    @Override
    public int hashCode()
    {
        return (super.hashCode()+ObjectUtils.hashCode(getSessionId()));
    }
    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        ClientContext o=(ClientContext)other;
        return (super.equals(other) && ObjectUtils.equals(getSessionId(),o.getSessionId()));
    }
    /**
     * session ID value
     */
    private SessionId sessionId;
    /**
     * username that owns the context
     */
    private String username;
}

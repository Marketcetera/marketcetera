package org.marketcetera.util.ws.stateful;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

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

/* $License$ */

@ClassVersion("$Id$")
public class ClientContext
    extends StatelessClientContext
{

    // INSTANCE DATA.

    private SessionId mSessionId;


    // INSTANCE METHODS.

    /**
     * Sets the receiver's session ID to the given one.
     *
     * @param sessionId The session ID, which may be null.
     */

    public void setSessionId
        (SessionId sessionId)
    {
        mSessionId=sessionId;
    }
 
    /**
     * Returns the receiver's session ID.
     *
     * @return The session ID, which may be null.
     */

    public SessionId getSessionId()
    {
        return mSessionId;
    }   


    // StatelessClientContext.

    @Override
    public String toString()
    {
        return Messages.CLIENT_CONTEXT.getText
            (super.toString(),getSessionId());
    }

    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ObjectUtils.hashCode(getSessionId()));
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
        return (super.equals(other) &&
                ObjectUtils.equals(getSessionId(),o.getSessionId()));                
    }
}

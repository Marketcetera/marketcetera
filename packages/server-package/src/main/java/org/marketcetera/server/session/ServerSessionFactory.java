package org.marketcetera.server.session;

import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Creates server sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ServerSessionFactory
        implements SessionFactory<ServerSession>
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#createSession(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, org.marketcetera.util.ws.tags.SessionId)
     */
    @Override
    public ServerSession createSession(StatelessClientContext inContext,
                                       String inUser,
                                       SessionId inId)
    {
        ServerSession session = new ServerSession(inContext,
                                                  inUser,
                                                  inId);
        sessionsBySessionId.put(session.getSessionId(),
                                session);
        return session;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#removedSession(java.lang.Object)
     */
    @Override
    public void removedSession(ServerSession inSession)
    {
        sessionsBySessionId.invalidate(inSession.getSessionId());
    }
    // TODO add expiration after write configuration
    /**
     * caches sessions
     */
    private Cache<SessionId,ServerSession> sessionsBySessionId = CacheBuilder.newBuilder().build();
}

package org.marketcetera.dare;

import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Creates DARE client sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DareClientSessionFactory
        implements SessionFactory<DareClientSession>
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#createSession(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, org.marketcetera.util.ws.tags.SessionId)
     */
    @Override
    public DareClientSession createSession(StatelessClientContext inContext,
                                           String inUser,
                                           SessionId inId)
    {
        DareClientSession session = new DareClientSession(inId,
                                                          inUser);
        sessions.put(session.getSessionId(),
                     session);
        return session;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#removedSession(java.lang.Object)
     */
    @Override
    public void removedSession(DareClientSession inSession)
    {
        sessions.invalidate(inSession.getSessionId());
    }
    /**
     * stores session values
     */
    private final Cache<SessionId,DareClientSession> sessions = CacheBuilder.newBuilder().build();
}

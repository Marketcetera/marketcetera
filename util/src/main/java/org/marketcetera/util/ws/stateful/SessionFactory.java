package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A session (data object) factory.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SessionFactory<T>
{

    /**
     * Creates a new session (data object), for a new session
     * initiated with the given creation context and on behalf of the
     * user with the given name, and assigned the given ID.
     *
     * @param context The context.
     * @param user The user name.
     * @param id The session ID.
     *
     * @return The session (data object). It may be null.
     */

    T createSession(StatelessClientContext context,
                    String user,
                    SessionId id);

    /**
     * Notifies the receiver that the session associated with the
     * given session (data object) has been terminated. Note that,
     * since the session (data object) associated with a {@link
     * SessionHolder} can be modified during a session, the argument
     * supplied to this method may not be the same object as returned
     * from {@link
     * #createSession(StatelessClientContext,String,SessionId)} for
     * the same session.
     *
     * @param session The session (data object).
     */

    void removedSession(T session);
}

package org.marketcetera.server.session;

import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Manages sessions for the server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ServerSessionManager
        extends SessionManager<ServerSession>
{
    /**
     * Create a new ServerSessionManager instance.
     *
     * @param inSessionFactory
     */
    @Autowired
    public ServerSessionManager(SessionFactory<ServerSession> inSessionFactory)
    {
        super(inSessionFactory);
    }
}

package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessServiceBaseImpl;

/**
 * The base class for all stateful web services, which retains an
 * optional session manager.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ServiceBaseImpl<T>
    extends StatelessServiceBaseImpl
    implements ServiceBase
{

    // INSTANCE DATA.

    private final SessionManager<T> mSessionManager;


    // CONSTRUCTORS.

    /**
     * Creates a new service implementation with the given session
     * manager.
     *
     * @param sessionManager The session manager, which may be null.
     */    

    public ServiceBaseImpl
        (SessionManager<T> sessionManager)
    {
        mSessionManager=sessionManager;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's session manager.
     *
     * @return The session manager, which may be null.
     */

    public SessionManager<T> getSessionManager()
    {
        return mSessionManager;
    }
}

package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessServer;

/**
 * A server node for stateful communication. Its (optional)
 * authenticator checks credentials and its (optional) session manager
 * maintains active sessions.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Server<T>
    extends StatelessServer
{

    // INSTANCE DATA.

    private final Authenticator mAuthenticator;
    private final SessionManager<T> mSessionManager;
    private final ServiceInterface mAuthService;


    // CONSTRUCTORS.

    /**
     * Creates a new server node with the given server host name,
     * port, authenticator, and session manager.
     *
     * @param host The host name.
     * @param port The port.
     * @param authenticator The authenticator, which may be null.
     * @param sessionManager The session manager, which may be null.
     */    

    public Server
        (String host,
         int port,
         Authenticator authenticator,
         SessionManager<T> sessionManager)
    {
        super(host,port);
        mAuthenticator=authenticator;
        mSessionManager=sessionManager;
        if (getSessionManager()!=null) {
            getSessionManager().setServerId(getId());
        }
        if (getAuthenticator()==null) {
            mAuthService=null;
        } else {
            mAuthService=publish
                (new AuthServiceImpl<T>(getAuthenticator(),getSessionManager()),
                 AuthService.class);
        }
    }

    /**
     * Creates a new server node with the default server host name and
     * port, and the given authenticator and session manager.
     *
     * @param authenticator The authenticator, which may be null.
     * @param sessionManager The session manager, which may be null.
     */    

    public Server
        (Authenticator authenticator,
         SessionManager<T> sessionManager)
    {
        this(DEFAULT_HOST,DEFAULT_PORT,authenticator,sessionManager);
    }

    /**
     * Creates a new server node with the default server host name and
     * port, and no authenticator or session manager.
     */    

    public Server()
    {
        this(null,null);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's authenticator.
     *
     * @return The authenticator, which may be null.
     */

    public Authenticator getAuthenticator()
    {
        return mAuthenticator;
    }   

    /**
     * Returns the receiver's session manager.
     *
     * @return The session manager, which may be null.
     */

    public SessionManager<T> getSessionManager()
    {
        return mSessionManager;
    }   

    /**
     * Returns the receiver's authentication service interface.
     *
     * @return The interface, which may be null.
     */

    private ServiceInterface getAuthService()
    {
        return mAuthService;
    }   


    // StatelessServer.

    @Override
    public void stop()
    {
        super.stop();
        if (getAuthService()!=null) {
            getAuthService().stop();
        }
    }
}

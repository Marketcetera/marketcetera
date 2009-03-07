package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A client node for stateful communication. Its (optional)
 * application ID is that of the application which hosts the client. A
 * session starts with {@link #login(String,char[])} and ends with
 * {@link #logout()}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Client
    extends StatelessClient
{

    // INSTANCE DATA.

    private SessionId mSessionId;


    // CONSTRUCTORS.

    /**
     * Creates a new client node with the given server host name,
     * port, and client application ID.
     *
     * @param host The host name.
     * @param port The port.
     * @param appId The application ID, which may be null.
     */    

    public Client
        (String host,
         int port,
         AppId appId)
    {
        super(host,port,appId);
    }

    /**
     * Creates a new client node with the default server host name and
     * port, and the given client application ID.
     *
     * @param appId The application ID, which may be null.
     */    

    public Client
        (AppId appId)
    {
        this(DEFAULT_HOST,DEFAULT_PORT,appId);
    }

    /**
     * Creates a new client node with the default server host name and
     * port, and no client application ID.
     */    

    public Client()
    {
        this(null);
    }


    // INSTANCE METHODS.

    /**
     * Sets the receiver's session ID to the given one.
     *
     * @param sessionId The session ID, which may be null.
     */

    private void setSessionId
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

    /**
     * Asserts that an active session is in progress.
     *
     * @throws I18NException Thrown if an active session is not in
     * progress.
     */
    
    public void assertValidSession()
        throws I18NException
    {
        if (getSessionId()==null) {
            throw new I18NException(Messages.NOT_LOGGED_IN);
        }
    }

    /**
     * Initiates a new session using the given credentials.
     *
     * @param user The user name.
     * @param password The password.
     *
     * @throws I18NException Thrown if a session is already in
     * progress.
     * @throws RemoteException Thrown if the server is unable to
     * complete the login operation.
     */

    public void login
        (String user,
         char[] password)
        throws I18NException,
               RemoteException
    {
        if (getSessionId()!=null) {
            throw new I18NException(Messages.ALREADY_LOGGED_IN);
        }
        AuthService i=getService(AuthService.class);
        setSessionId(i.login(super.getContext(),user,password));
    }

    /**
     * Ends an ongoing session.
     *
     * @throws RemoteException Thrown if the server is unable to
     * complete the logout operation.
     */

    public void logout() 
        throws RemoteException
    {
        AuthService i=getService(AuthService.class);
        i.logout(getContext());
        setSessionId(null);
    }


    // StatelessClient.

    @Override
    public ClientContext getContext()
    {
        ClientContext context=new ClientContext();
        fillContext(context);
        context.setSessionId(getSessionId());
        return context;
    }
}

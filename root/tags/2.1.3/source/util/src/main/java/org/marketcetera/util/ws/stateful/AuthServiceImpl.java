package org.marketcetera.util.ws.stateful;

import java.util.Arrays;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessRemoteCaller;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * An authentication service implementation.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AuthServiceImpl<T>
    extends ServiceBaseImpl<T>
    implements AuthService
{

    // INSTANCE DATA.

    private final Authenticator mAuthenticator;


    // CONSTRUCTORS.

    /**
     * Creates a new authentication service implementation with the
     * given authenticator and session manager.
     *
     * @param authenticator The authenticator.
     * @param sessionManager The session manager.
     */    

    public AuthServiceImpl
        (Authenticator authenticator,
         SessionManager<T> sessionManager)
    {
        super(sessionManager);
        mAuthenticator=authenticator;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's authenticator.
     *
     * @return The authenticator.
     */

    public Authenticator getAuthenticator()
    {
        return mAuthenticator;
    }   

    /**
     * Logs in the client with the given context, provided the given
     * credentials are acceptable.
     *
     * @param context The context.
     * @param user The user name.
     * @param password The password.
     *
     * @return The ID of the new session.
     *
     * @throws I18NException Thrown if the authenticator rejects the
     * credentials.
     */

    private SessionId loginImpl
        (StatelessClientContext context,
         String user,
         char[] password)
        throws I18NException
    {
        if (!getAuthenticator().shouldAllow(context,user,password)) {
            throw new I18NException(Messages.BAD_CREDENTIALS);
        }
        SessionId sessionId=SessionId.generate();
        getSessionManager().put(sessionId,new SessionHolder<T>(user,context));
        return sessionId;
    }

    /**
     * Logs out the client with the given context and associated
     * session holder. This method is a no-op if there is no active
     * session for that client.
     *
     * @param sessionHolder The holder, which may be null.
     */

    private void logout
        (ClientContext context,
         SessionHolder<T> sessionHolder)
    {
        if (sessionHolder!=null) {
            getSessionManager().remove(context.getSessionId());
        }
    }


    // AuthServiceInterface.

    @Override
    public SessionId login
        (StatelessClientContext context,
         final String user,
         final char[] password)
        throws RemoteException
    {
        try {
            return (new StatelessRemoteCaller<SessionId>() {
                @Override
                protected SessionId call
                    (StatelessClientContext context)
                    throws I18NException
                {
                    return loginImpl(context,user,password);
                }}).execute(context);
        } finally {
            Arrays.fill(password,'\0');
        }
    }

    @Override
    public void logout
        (ClientContext context)
        throws RemoteException
    {
        (new RemoteRunner<T>(RemoteRunner.DEFAULT_VERSION_FILTER,
                             null,null,getSessionManager(),null) {
            @Override
            protected void run
                (ClientContext context,
                 SessionHolder<T> sessionHolder)
            {
                logout(context,sessionHolder);
            }}).execute(context);
    }
}

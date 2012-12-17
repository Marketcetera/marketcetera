package org.marketcetera.util.ws.stateful;

import java.util.Locale;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.TagFilter;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * An implementation wrapper for stateful services. The wrapped call
 * is implemented by overriding {@link
 * #run(ClientContext,SessionHolder)}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class RemoteRunner<S>
    extends RemoteCall<S>
{

    // CONSTRUCTORS.

    /**
     * Creates a new wrapper which uses the given (optional) session
     * manager for session ID mappings, and which applies the given
     * filters to the client context.
     *
     * @param versionIdFilter The version ID filter, which may be null.
     * @param appIdFilter The application ID filter, which may be null.
     * @param clientIdFilter The client ID filter, which may be null.
     * @param sessionManager The session manager, which may be null.
     * @param sessionIdFilter The session ID filter, which may be null.
     */    

    public RemoteRunner
        (TagFilter versionIdFilter,
         TagFilter appIdFilter,
         TagFilter clientIdFilter,
         SessionManager<S> sessionManager,
         TagFilter sessionIdFilter)
    {
        super(versionIdFilter,appIdFilter,clientIdFilter,
              sessionManager,sessionIdFilter);
    }

    /**
     * Creates a new wrapper which uses the given (optional) session
     * manager for session ID mappings, and which applies two filters
     * to the client context. The first filter ensures that the
     * client's version ID is equal to the server's version ID; the
     * second ensures that the session ID maps to an active session.
     *
     * @param sessionManager The session manager, which may be null.
     */    

    public RemoteRunner
        (SessionManager<S> sessionManager)
    {
        super(sessionManager);
    }


    // INSTANCE METHODS.

    /**
     * Invokes the service implementation on behalf of the client with
     * the given context.
     *
     * @param context The context.
     *
     * @throws RemoteException Thrown if the implementation fails;
     * it wraps the throwable thrown by the actual implementation.
     */

    public void execute
        (ClientContext context)
        throws RemoteException
    {
        Locale locale=null;
        if (context.getLocale()!=null) {
            locale=context.getLocale().getRaw();
        }
        try {
            startCall(context);
            ActiveLocale.pushLocale(locale);
            try {
                run(context,getSessionHolder(context));
            } finally {
                ActiveLocale.popLocale();
            }
            handleSuccess(context);
        } catch (Throwable t) {
            throw wrapFailure(context,t);
        }
    }

    /**
     * The service implementation, executed on behalf of the client
     * with the given context and associated session holder.
     *
     * @param context The context.
     * @param sessionHolder The holder, which may be null.
     *
     * @throws Exception Thrown if the implementation fails.
     */

    protected abstract void run
        (ClientContext context,
         SessionHolder<S> sessionHolder)
        throws Exception;
}

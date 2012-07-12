package org.marketcetera.util.ws.stateless;

import java.util.Locale;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.TagFilter;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * An implementation wrapper for stateless services. The wrapped call
 * is implemented by overriding {@link #call(StatelessClientContext)}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class StatelessRemoteCaller<T>
    extends StatelessRemoteCall
{

    // CONSTRUCTORS.

    /**
     * Creates a new wrapper which applies the given filters to the
     * client context.
     *
     * @param versionIdFilter The version ID filter, which may be null.
     * @param appIdFilter The application ID filter, which may be null.
     * @param clientIdFilter The client ID filter, which may be null.
     */    

    public StatelessRemoteCaller
        (TagFilter versionIdFilter,
         TagFilter appIdFilter,
         TagFilter clientIdFilter)
    {
        super(versionIdFilter,appIdFilter,clientIdFilter);
    }

    /**
     * Creates a new wrapper which applies a single filter to the
     * client context. That filter ensures that the client's version
     * ID is equal to the server's version ID.
     */    

    public StatelessRemoteCaller() {}


    // INSTANCE METHODS.

    /**
     * Invokes the service implementation on behalf of the client with
     * the given context.
     *
     * @param context The context.
     *
     * @return The result returned by the implementation.
     *
     * @throws RemoteException Thrown if the implementation fails;
     * it wraps the throwable thrown by the actual implementation.
     */

    public T execute
        (StatelessClientContext context)
        throws RemoteException
    {
        Locale locale=null;
        if (context.getLocale()!=null) {
            locale=context.getLocale().getRaw();
        }
        try {
            startCall(context);
            T result;
            ActiveLocale.pushLocale(locale);
            try {
                assertFilterMatch(context);
                result=call(context);
            } finally {
                ActiveLocale.popLocale();
            }
            handleSuccess(context);
            return result;
        } catch (Throwable t) {
            throw wrapFailure(context,t);
        }
    }

    /**
     * The service implementation, executed on behalf of the client
     * with the given context.
     *
     * @param context The context.
     *
     * @return The result returned by the implementation.
     *
     * @throws Exception Thrown if the implementation fails.
     */

    protected abstract T call
        (StatelessClientContext context)
        throws Exception;
}

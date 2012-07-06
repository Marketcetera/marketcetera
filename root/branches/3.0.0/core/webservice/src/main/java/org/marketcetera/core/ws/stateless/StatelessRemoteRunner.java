package org.marketcetera.core.ws.stateless;

import java.util.Locale;
import org.marketcetera.core.util.log.ActiveLocale;
import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.ws.tags.TagFilter;
import org.marketcetera.core.ws.wrappers.RemoteException;

/**
 * An implementation wrapper for stateless services. The wrapped call
 * is implemented by overriding {@link #run(StatelessClientContext)}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: StatelessRemoteRunner.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: StatelessRemoteRunner.java 82324 2012-04-09 20:56:08Z colin $")
public abstract class StatelessRemoteRunner
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

    public StatelessRemoteRunner
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

    public StatelessRemoteRunner() {}


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
        (StatelessClientContext context)
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
                assertFilterMatch(context);
                run(context);
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
     * with the given context.
     *
     * @param context The context.
     *
     * @throws Exception Thrown if the implementation fails.
     */

    protected abstract void run
        (StatelessClientContext context)
        throws Exception;
}

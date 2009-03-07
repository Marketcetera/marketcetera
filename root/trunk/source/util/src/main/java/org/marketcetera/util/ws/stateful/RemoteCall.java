package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessRemoteCall;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.TagFilter;
import org.marketcetera.util.ws.tags.ValidSessionTagFilter;

/**
 * An implementation wrapper for stateful services. It runs on the
 * server-side and applies the (optional) client version, application,
 * client, and session IDs through its filters; it also handles
 * logging, exception wrapping, and (optionally) maps session IDs to
 * session holders.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RemoteCall<T>
    extends StatelessRemoteCall
{

    // INSTANCE DATA.

    private final SessionManager<T> mSessionManager;
    private final TagFilter mSessionIdFilter;


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

    public RemoteCall
        (TagFilter versionIdFilter,
         TagFilter appIdFilter,
         TagFilter clientIdFilter,
         SessionManager<T> sessionManager,
         TagFilter sessionIdFilter)
    {
        super(versionIdFilter,appIdFilter,clientIdFilter);
        mSessionManager=sessionManager;
        mSessionIdFilter=sessionIdFilter;
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

    public RemoteCall
        (SessionManager<T> sessionManager)
    {
        mSessionManager=sessionManager;
        if (getSessionManager()!=null) {
            mSessionIdFilter=getDefaultSessionIdFilter(getSessionManager());
        } else {
            mSessionIdFilter=null;
        }
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

    /**
     * Returns the default session ID filter for the given session
     * manager.
     *
     * @param sessionManager The session manager.
     *
     * @return The filter.
     */

    public static <T> ValidSessionTagFilter<T> getDefaultSessionIdFilter
        (SessionManager<T> sessionManager)
    {
        return new ValidSessionTagFilter<T>(sessionManager);
    }

    /**
     * Returns the receiver's session ID filter.
     *
     * @return The filter, which may be null.
     */

    public TagFilter getSessionIdFilter()
    {
        return mSessionIdFilter;
    }

    /**
     * Asserts that the given client context matches all of the
     * receiver's filters. It also returns the session holder for the
     * active session of that client, if one exists.
     *
     * @param context The context.
     *
     * @return The session holder, which may be null.
     *
     * @throws I18NException Thrown if there is a filter mismatch.
     */

    protected SessionHolder<T> getSessionHolder
        (ClientContext context)
        throws I18NException
    {
        assertFilterMatch(context);
        SessionId sessionId=context.getSessionId();
        assertFilterMatch(getSessionIdFilter(),sessionId);
        if ((sessionId==null) || (getSessionManager()==null)) {
            return null;
        }
        SessionHolder<T> holder=getSessionManager().get(sessionId);
        return holder;
    }
}

package org.marketcetera.util.ws.stateless;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.EqualsTagFilter;
import org.marketcetera.util.ws.tags.Tag;
import org.marketcetera.util.ws.tags.TagFilter;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * An implementation wrapper for stateless services. It runs on the
 * server-side and applies the (optional) client version, application,
 * and client IDs through its filters; it also handles logging and
 * exception wrapping.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class StatelessRemoteCall
{

    // CLASS DATA.

    /**
     * The default version ID filter.
     */

    public static final EqualsTagFilter DEFAULT_VERSION_FILTER=
        new EqualsTagFilter(VersionId.SELF,Messages.VERSION_MISMATCH);


    // INSTANCE DATA.

    private final TagFilter mVersionIdFilter;
    private final TagFilter mAppIdFilter;
    private final TagFilter mClientIdFilter;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper which applies the given filters to the
     * client context.
     *
     * @param versionIdFilter The version ID filter, which may be null.
     * @param appIdFilter The application ID filter, which may be null.
     * @param clientIdFilter The client ID filter, which may be null.
     */    

    public StatelessRemoteCall
        (TagFilter versionIdFilter,
         TagFilter appIdFilter,
         TagFilter clientIdFilter)
    {
        mVersionIdFilter=versionIdFilter;
        mAppIdFilter=appIdFilter;
        mClientIdFilter=clientIdFilter;
    }

    /**
     * Creates a new wrapper which applies a single filter to the
     * client context. That filter ensures that the client's version
     * ID is equal to the server's version ID.
     */    

    public StatelessRemoteCall()
    {
        mVersionIdFilter=DEFAULT_VERSION_FILTER;
        mAppIdFilter=null;
        mClientIdFilter=null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's version ID filter.
     *
     * @return The filter, which may be null.
     */

    public TagFilter getVersionIdFilter()
    {
        return mVersionIdFilter;
    }

    /**
     * Returns the receiver's application ID filter.
     *
     * @return The filter, which may be null.
     */

    public TagFilter getAppIdFilter()
    {
        return mAppIdFilter;
    }

    /**
     * Returns the receiver's client ID filter.
     *
     * @return The filter, which may be null.
     */

    public TagFilter getClientIdFilter()
    {
        return mClientIdFilter;
    }

    /**
     * Asserts that the given filter matches the given tag.
     *
     * @param filter The filter, which may be null.
     * @param tag The tag, which may be null.
     *
     * @throws I18NException Thrown if there is a mismatch.
     */

    protected static void assertFilterMatch
        (TagFilter filter,
         Tag tag)
        throws I18NException
    {
        if (filter!=null) {
            filter.assertMatch(tag);
        }
    }

    /**
     * Asserts that the given client context matches all of the
     * receiver's filters.
     *
     * @param context The context.
     *
     * @throws I18NException Thrown if there is a mismatch.
     */

    protected void assertFilterMatch
        (StatelessClientContext context)
        throws I18NException
    {
        assertFilterMatch(getVersionIdFilter(),context.getVersionId());
        assertFilterMatch(getAppIdFilter(),context.getAppId());
        assertFilterMatch(getClientIdFilter(),context.getClientId());
    }

    /**
     * Called by subclasses before starting custom processing of a
     * service call on behalf of the client with the given context.
     *
     * @param context The context.
     */

    protected void startCall
        (StatelessClientContext context)
    {
        Messages.CALL_START.debug(this,context);
    }

    /**
     * Called by subclasses after custom processing of a service call
     * on behalf of the client with the given context completes
     * successfully.
     *
     * @param context The context.
     */

    protected void handleSuccess
        (StatelessClientContext context)
    {
        Messages.CALL_SUCCESS.debug(this,context);
    }

    /**
     * Called by subclasses after custom processing of a service call
     * on behalf of the client with the given context completes
     * unsuccessfully. The failure is described by the given throwable.
     *
     * @param context The context.
     * @param t The throwable.
     */

    protected RemoteException wrapFailure
        (StatelessClientContext context,
         Throwable t)
    {
        Messages.CALL_FAILURE.debug(this,t,context);
        return new RemoteException(t);
    }
}

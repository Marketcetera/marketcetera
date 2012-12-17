package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionManager;

/**
 * A session ID filter that accepts any ID which maps to an active
 * session. If the session is active, the very filter check renews the
 * session's expiration counter.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ValidSessionTagFilter<T>
    implements TagFilter
{

    // INSTANCE DATA.

    private final SessionManager<T> mSessionManager;


    // CONSTRUCTORS.

    /**
     * Creates a new filter which uses the given (optional) session
     * manager for session ID mappings.
     *
     * @param sessionManager The session manager. It may be null, in
     * which case the filter accepts all tags.
     */

    public ValidSessionTagFilter
        (SessionManager<T> sessionManager)
    { 
        mSessionManager=sessionManager;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's session manager.
     *
     * @return The session manager, which may be null
     */

    public SessionManager<T> getSessionManager()
    {
        return mSessionManager;
    }


    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag)
        throws I18NException
    {
        if (getSessionManager()==null) {
            return;
        }
        if (tag==null) {
            throw new I18NException(Messages.SESSION_REQUIRED);
        }
        if ((tag instanceof SessionId) &&
            (getSessionManager().get((SessionId)tag)!=null)) {
            return;
        }
        throw new I18NException
            (new I18NBoundMessage1P(Messages.SESSION_EXPIRED,tag));
    }
}

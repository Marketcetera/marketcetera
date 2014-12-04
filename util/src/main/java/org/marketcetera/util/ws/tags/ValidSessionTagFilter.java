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
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.tags.TagFilter#assertMatch(org.marketcetera.util.ws.tags.Tag)
     */
    @Override
    public void assertMatch(Tag inTag)
            throws I18NException
    {
        if(getSessionManager() == null) {
            return;
        }
        if(inTag == null) {
            throw new SessionRequiredException(Messages.SESSION_REQUIRED);
        }
        if((inTag instanceof SessionId) && (getSessionManager().get((SessionId)inTag)!=null)) {
            return;
        }
        throw new SessionExpiredException(new I18NBoundMessage1P(Messages.SESSION_EXPIRED,
                                                                 inTag));
    }
}

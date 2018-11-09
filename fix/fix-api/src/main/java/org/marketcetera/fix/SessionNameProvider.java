package org.marketcetera.fix;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides a human-readable session name.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionNameProvider
{
    /**
     * Get the name of the given session.
     *
     * <b>This method is intended to be light-weight to be called frequently with minimal trips to the database.
     * As such, it is possible that the value returned may, under certain circumstances, be inaccurate due to caching.
     * This is unlikely to happen, but is possible due to the emphasis on performance.
     * 
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>String</code> value, never <code>null</code>
     */
    String getSessionName(SessionID inSessionId);
}

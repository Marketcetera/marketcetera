package org.marketcetera.fix;

import quickfix.SessionID;

/* $License$ */

/**
 * Decides whether a FIX session should be restored on start/logon or just allowed to start.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionRestoreArbiter
{
    /**
     * Indicates if the given session should be restored or allowed to start as-is.
     *
     * @param inSessionId a <code>SessionID</cod> value
     * @return a <code>boolean</code>
     */
    boolean shouldRestore(SessionID inSessionId);
}

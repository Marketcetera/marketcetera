package org.marketcetera.fix;

import quickfix.SessionID;

/* $License$ */

/**
 * Indicates that the implementer has a {@link SessionID}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSessionId
{
    /**
     * Get the session ID value.
     *
     * @return a <code>SessionID</code> value
     */
    SessionID getSessionId();
}

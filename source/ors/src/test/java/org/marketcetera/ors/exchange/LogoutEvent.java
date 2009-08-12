package org.marketcetera.ors.exchange;

import quickfix.Application;
import quickfix.SessionID;

/**
 * An event representing a call to {@link
 * Application#onLogout(SessionID)}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class LogoutEvent
    extends Event
{
    public LogoutEvent
        (SessionID sessionID)
    {
        super(sessionID);
    }
}

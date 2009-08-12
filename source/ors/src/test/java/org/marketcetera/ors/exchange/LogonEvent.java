package org.marketcetera.ors.exchange;

import quickfix.Application;
import quickfix.SessionID;

/**
 * An event representing a call to {@link
 * Application#onLogon(SessionID)}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class LogonEvent
    extends Event
{
    public LogonEvent
        (SessionID sessionID)
    {
        super(sessionID);
    }
}

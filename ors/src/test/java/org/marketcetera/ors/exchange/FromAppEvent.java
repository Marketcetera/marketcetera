package org.marketcetera.ors.exchange;

import quickfix.Application;
import quickfix.Message;
import quickfix.SessionID;

/**
 * An event representing a call to {@link
 * Application#fromApp(Message,SessionID)}.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class FromAppEvent
    extends MessageEvent
{
    public FromAppEvent
        (SessionID sessionID,
         Message message)
    {
        super(sessionID,message);
    }
}

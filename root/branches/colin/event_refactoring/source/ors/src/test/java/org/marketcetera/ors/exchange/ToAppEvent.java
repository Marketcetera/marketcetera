package org.marketcetera.ors.exchange;

import quickfix.Application;
import quickfix.Message;
import quickfix.SessionID;

/**
 * An event representing a call to {@link
 * Application#toApp(Message,SessionID)}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class ToAppEvent
    extends MessageEvent
{
    public ToAppEvent
        (SessionID sessionID,
         Message message)
    {
        super(sessionID,message);
    }
}

package org.marketcetera.ors.exchange;

import quickfix.Application;
import quickfix.Message;
import quickfix.SessionID;

/**
 * An event representing a call to {@link
 * Application#toAdmin(Message,SessionID)}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class ToAdminEvent
    extends MessageEvent
{
    public ToAdminEvent
        (SessionID sessionID,
         Message message)
    {
        super(sessionID,message);
    }
}

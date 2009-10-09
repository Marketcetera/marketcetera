package org.marketcetera.ors.exchange;

import quickfix.Message;
import quickfix.SessionID;

/**
 * A generic message event, containing a {@link SessionID} and a
 * {@link Message}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class MessageEvent
    extends Event
{
    private Message mMessage;

    public MessageEvent
        (SessionID sessionID,
         Message message)
    {
        super(sessionID);
        mMessage=message;
    }

    public Message getMessage()
    {
        return mMessage;
    }

    @Override
    public String toString()
    {
        return super.toString()+'['+getMessage()+']';
    }
}

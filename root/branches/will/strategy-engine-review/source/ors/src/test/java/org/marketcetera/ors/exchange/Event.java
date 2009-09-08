package org.marketcetera.ors.exchange;

import quickfix.SessionID;

/**
 * A generic event, containing only a {@link SessionID}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class Event
{
    private SessionID mSessionID;
    
    public Event
        (SessionID sessionID)
    {
        mSessionID=sessionID;
    }

    public SessionID getSessionID()
    {
        return mSessionID;
    }

    @Override
    public String toString()
    {
        return getClass().toString()+':'+getSessionID();
    }
}

package com.marketcetera.ors.filters;

import java.util.Set;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: MessageTypeFilter.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: MessageTypeFilter.java 16468 2014-05-12 00:36:56Z colin $")
public class MessageTypeFilter
    implements MessageFilter
{
    private Set<String> mAccepted;
    private boolean mAcceptUnknown;


    public void setAcceptedMessages
        (Set<String> accepted)
    {
		mAccepted=accepted;
    }

    public Set<String> getAcceptedMessages()
    {
		return mAccepted;
    }


    public void setAcceptUnknown
        (boolean acceptUnknown)
    {
        mAcceptUnknown=acceptUnknown;
    }

    public boolean getAcceptUnknown()
    {
		return mAcceptUnknown;
    }


    @Override
    public boolean isAccepted
        (Message message)
    {
        try {
            return getAcceptedMessages().contains
                (message.getHeader().getString(MsgType.FIELD));
        } catch (FieldNotFound ex) {
            return getAcceptUnknown();
        }
    }
}

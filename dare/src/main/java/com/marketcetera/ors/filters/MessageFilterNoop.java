package com.marketcetera.ors.filters;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: MessageFilterNoop.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: MessageFilterNoop.java 16468 2014-05-12 00:36:56Z colin $")
public class MessageFilterNoop
    implements MessageFilter
{
    @Override
    public boolean isAccepted
        (Message message)
    {
        return true;
    }
}

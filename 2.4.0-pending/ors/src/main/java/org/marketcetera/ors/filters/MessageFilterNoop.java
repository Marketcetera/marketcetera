package org.marketcetera.ors.filters;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
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

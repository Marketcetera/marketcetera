package org.marketcetera.ors.filters;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: MessageFilter.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: MessageFilter.java 16468 2014-05-12 00:36:56Z colin $")
public interface MessageFilter
{
    boolean isAccepted
        (Message message);
}

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
public class OrderFilterNoop
    implements OrderFilter
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.OrderFilter#isAccepted(org.marketcetera.ors.filters.OrderFilter.MessageInfo, quickfix.Message)
     */
    @Override
    public boolean isAccepted(MessageInfo inMessageInfo,
                              Message inMessage)
    {
        return true;
    }
}

package com.marketcetera.ors.filters;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: OrderFilterNoop.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: OrderFilterNoop.java 16468 2014-05-12 00:36:56Z colin $")
public class OrderFilterNoop
    implements OrderFilter
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.filters.OrderFilter#isAccepted(com.marketcetera.ors.filters.OrderFilter.MessageInfo, quickfix.Message)
     */
    @Override
    public boolean isAccepted(MessageInfo inMessageInfo,
                              Message inMessage)
    {
        return true;
    }
}

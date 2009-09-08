package org.marketcetera.ors.filters;

import org.marketcetera.ors.info.RequestInfo;
import quickfix.Message;
import quickfix.field.ClearingFirm;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class SimpleMessageModifierManager
    extends MessageModifierManager
{
    public static final String FIRM=
        "FIRM";

    public void modifyMessage
        (RequestInfo info)
    {
        Message msg=(Message)info.getValue(RequestInfo.CURRENT_MESSAGE);
        msg.setField(new ClearingFirm(FIRM));
    }
}

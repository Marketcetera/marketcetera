package org.marketcetera.ors.filters;

import org.marketcetera.ors.info.RequestInfo;
import quickfix.Message;
import quickfix.field.SecurityDesc;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class SimpleMessageModifierManager
    extends MessageModifierManager
{
    public static final String SECURITY_DESC=
        "SECURITY_DESC";

    public void modifyMessage
        (RequestInfo info)
    {
        Message msg=(Message)info.getValue(RequestInfo.CURRENT_MESSAGE);
        msg.setField(new SecurityDesc(SECURITY_DESC));
    }
}

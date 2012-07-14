package org.marketcetera.trade;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang.StringEscapeUtils;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.InvalidMessage;
import quickfix.Message;

/**
 * A JAXB adapter for QuickFIX/J message properties. It uses escaped
 * strings for marshaling to prevent the introduction of invalid
 * characters into XML.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageAdapter
    extends XmlAdapter<String,Message>
{
    @Override
    public String marshal
        (Message msg)
    {
        if (msg==null) {
            return null;
        }
        return StringEscapeUtils.escapeJava(msg.toString());
    }

    @Override
    public Message unmarshal
        (String msg)
        throws InvalidMessage
    {
        if (msg==null) {
            return null;
        }
        return new Message(StringEscapeUtils.unescapeJava(msg));
    }
}

package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * General-purpose utilities.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class LogUtils
{

    // CLASS METHODS.

    /**
     * Returns a textual representation of the given object list. A
     * null list is treated as a list containing a single null
     * element.
     *
     * @param list The list.
     *
     * @return The textual representation.
     */

    public static String getListText
        (Object... list)
    {
        if (list==null) {
            list=new Object[] {null};
        }
        StringBuffer buffer=new StringBuffer();
        buffer.append('(');
        boolean first=true;
        for (Object e:list) {
            if (!first) {
                buffer.append(',');
            }
            if (e==null) {
                buffer.append("[null]");
            } else {
                buffer.append('\'');
                buffer.append(e.toString());
                buffer.append('\'');
            }
            first=false;
        }
        buffer.append(')');
        return buffer.toString();
    }

    /**
     * Returns a simplified textual representation of the given
     * provider and parameterized message.
     *
     * @param provider The message provider.
     * @param message The message.
     * @param params The message parameters.
     *
     * @return The textual representation.
     */

    public static String getSimpleMessage
        (I18NMessageProvider provider,
         I18NMessage message,
         Object... params)
    {
        return "provider '"+provider.getProviderId()+
            "'; id '"+message.getMessageId()+
            "'; entry '"+message.getEntryId()+
            "'; parameters "+getListText(params);
    }

    /**
     * Returns a simplified textual representation of the
     * parameterized message.
     *
     * @param message The message.
     * @param params The message parameters.
     *
     * @return The textual representation.
     */

    public static String getSimpleMessage
        (I18NMessage message,
         Object... params)
    {
        return getSimpleMessage(message.getMessageProvider(),message,params);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private LogUtils() {}
}

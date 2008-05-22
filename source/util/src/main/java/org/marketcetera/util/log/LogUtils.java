package org.marketcetera.util.log;

/**
 * General-purpose utilities.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.marketcetera.core.ClassVersion;

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
        StringBuilder builder=new StringBuilder();
        builder.append('(');
        boolean first=true;
        for (Object e:list) {
            if (!first) {
                builder.append(',');
            }
            if (e==null) {
                builder.append("[null]");
            } else {
                builder.append('\'');
                builder.append(e.toString());
                builder.append('\'');
            }
            first=false;
        }
        builder.append(')');
        return builder.toString();
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

    /**
     * Returns a simplified textual representation of the
     * parameterized message.
     *
     * @param message The message.
     *
     * @return The textual representation.
     */

    public static String getSimpleMessage
        (I18NBoundMessage message)
    {
        return getSimpleMessage(message.getMessage(),message.getParams());
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private LogUtils() {}
}

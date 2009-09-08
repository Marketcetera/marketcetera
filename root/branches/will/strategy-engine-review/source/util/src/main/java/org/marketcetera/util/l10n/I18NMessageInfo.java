package org.marketcetera.util.l10n;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Holder of meta-information about an internationalized message.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessageInfo
    extends MessageInfo
{

    // CLASS DATA.

    /**
     * An empty array of class instances.
     */

    public static final I18NMessageInfo[] EMPTY_ARRAY=
        new I18NMessageInfo[0];


    // INSTANCE DATA.

    private I18NMessage mMessage;


    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder with the given message
     * key, parameter count, and internationalized message.
     *
     * @param key The key.
     * @param paramCount The parameter count.
     * @param message The message.
     */

    public I18NMessageInfo
        (String key,
         int paramCount,
         I18NMessage message)
    {
        super(key,paramCount);
        mMessage=message;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's internationalized message.
     *
     * @return The message.
     */

    public I18NMessage getMessage()
    {
        return mMessage;
    }


    // MessageInfo.

    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ObjectUtils.hashCode(getMessage()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        I18NMessageInfo o=(I18NMessageInfo)other;
        return (super.equals(o) &&
                ObjectUtils.equals(getMessage(),o.getMessage()));
    }
}

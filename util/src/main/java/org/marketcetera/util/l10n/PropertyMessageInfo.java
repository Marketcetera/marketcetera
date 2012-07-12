package org.marketcetera.util.l10n;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Holder of meta-information about a message stored in a properties
 * file.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class PropertyMessageInfo
    extends MessageInfo
{

    // CLASS DATA.

    /**
     * An empty array of class instances.
     */

    public static final PropertyMessageInfo[] EMPTY_ARRAY=
        new PropertyMessageInfo[0];


    // INSTANCE DATA.

    private String mMessageText;

    
    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder with the given message
     * key, parameter count, and message text.
     *
     * @param key The key.
     * @param paramCount The parameter count.
     * @param messageText The text.
     */

    public PropertyMessageInfo
        (String key,
         int paramCount,
         String messageText)
    {
        super(key,paramCount);
        mMessageText=messageText;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's message text.
     *
     * @return The text.
     */

    public String getMessageText()
    {
        return mMessageText;
    }


    // MessageInfo.

    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ObjectUtils.hashCode(getMessageText()));
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
        PropertyMessageInfo o=(PropertyMessageInfo)other;
        return (super.equals(o) &&
                ObjectUtils.equals(getMessageText(),o.getMessageText()));
    }
}

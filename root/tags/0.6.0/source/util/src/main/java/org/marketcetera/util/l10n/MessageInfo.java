package org.marketcetera.util.l10n;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Holder of meta-information about a generic message.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MessageInfo
{

    // CLASS DATA.

    /**
     * An empty array of class instances.
     */

    public static final MessageInfo[] EMPTY_ARRAY=
        new MessageInfo[0];


    // INSTANCE DATA.

    private String mKey;
    private int mParamCount;


    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder with the given message
     * key and parameter count.
     *
     * @param key The key.
     * @param paramCount The parameter count; -1 signifies an unknown
     * count.
     */

    public MessageInfo
        (String key,
         int paramCount)
    {
        mKey=key;
        mParamCount=paramCount;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's message key.
     *
     * @return The key.
     */

    public String getKey()
    {
        return mKey;
    }

    /**
     * Returns the receiver's parameter count.
     *
     * @return The count; -1 signifies an unknown count.
     */
    
    public int getParamCount()
    {
        return mParamCount;
    }


    // Object.

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getKey())+
                getParamCount());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if (!(other instanceof MessageInfo)) {
            return false;
        }
        MessageInfo o=(MessageInfo)other;
        return (ObjectUtils.equals(getKey(),o.getKey()) &&
                (getParamCount()==o.getParamCount()));
    }
}

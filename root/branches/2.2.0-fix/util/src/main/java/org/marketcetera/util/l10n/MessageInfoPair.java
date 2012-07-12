package org.marketcetera.util.l10n;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Holder of dual meta-information about a message. Both
 * meta-information components must have the same message key, hence
 * containing meta-information for two different representations of
 * the same message.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class MessageInfoPair
{

    // CLASS DATA.

    /**
     * An empty array of class instances.
     */

    public static final MessageInfoPair[] EMPTY_ARRAY=
        new MessageInfoPair[0];


    // INSTANCE DATA.

    private MessageInfo mSrcInfo;
    private MessageInfo mDstInfo;


    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder from the given component
     * holders. These holders both contain meta-information for two
     * different representations of the same message.
     *
     * @param srcInfo The source meta-information.
     * @param dstInfo The destination meta-information.
     */

    public MessageInfoPair
        (MessageInfo srcInfo,
         MessageInfo dstInfo)
    {
        mSrcInfo=srcInfo;
        mDstInfo=dstInfo;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's source meta-information.
     *
     * @return The meta-information.
     */

    public MessageInfo getSrcInfo()
    {
        return mSrcInfo;
    }
    
    /**
     * Returns the receiver's destination meta-information.
     *
     * @return The meta-information.
     */

    public MessageInfo getDstInfo()
    {
        return mDstInfo;
    }


    // Object.

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getSrcInfo())+
                ObjectUtils.hashCode(getDstInfo()));
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
        MessageInfoPair o=(MessageInfoPair)other;
        return (ObjectUtils.equals(getSrcInfo(),o.getSrcInfo()) &&
                ObjectUtils.equals(getDstInfo(),o.getDstInfo()));
    }
}

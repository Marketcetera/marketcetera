package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter that accepts any non-null tag.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class NonNullTagFilter
    implements TagFilter
{

    // INSTANCE DATA.

    private final I18NBoundMessage mMessage;


    // CONSTRUCTORS.

    /**
     * Creates a new filter with the given mismatch message.
     *
     * @param message The message for the exception thrown when the
     * filter rejects a tag.
     */

    public NonNullTagFilter
        (I18NBoundMessage message)
    { 
        mMessage=message;
    }


    // INSTANCE METHODS.

    /**
     * Returns the message for the exception thrown when the receiver
     * filter rejects a tag.
     *
     * @return The message.
     */

    public I18NBoundMessage getMessage()
    {
        return mMessage;
    }


    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag)
        throws I18NException
    {
        if (tag!=null) {
            return;
        }
        throw new I18NException(getMessage());
    }
}

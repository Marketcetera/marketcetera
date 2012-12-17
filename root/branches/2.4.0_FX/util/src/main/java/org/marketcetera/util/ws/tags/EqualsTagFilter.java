package org.marketcetera.util.ws.tags;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter that accepts any tag that is equal to a specific
 * target tag (which may be the null tag). The message for the
 * exception thrown when the filter rejects a candidate tag is
 * configurable, and receives as parameters the target and (failed)
 * candidate tag.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class EqualsTagFilter
    implements TagFilter
{

    // INSTANCE DATA.

    private final Tag mTarget;
    private final I18NMessage2P mMessage;


    // CONSTRUCTORS.

    /**
     * Creates a new filter with the given target tag and mismatch
     * message.
     *
     * @param target The target tag. It may be null.
     * @param message The message for the exception thrown when the
     * filter rejects a tag.
     */

    public EqualsTagFilter
        (Tag target,
         I18NMessage2P message)
    { 
        mTarget=target;
        mMessage=message;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's target tag.
     *
     * @return The tag, which may be null.
     */

    public Tag getTarget()
    {
        return mTarget;
    }

    /**
     * Returns the message for the exception thrown when the receiver
     * filter rejects a tag.
     *
     * @return The message.
     */

    public I18NMessage2P getMessage()
    {
        return mMessage;
    }


    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag)
        throws I18NException
    {
        if (ObjectUtils.equals(tag,getTarget())) {
            return;
        }
        throw new I18NException
            (new I18NBoundMessage2P(getMessage(),getTarget(),tag));
    }
}

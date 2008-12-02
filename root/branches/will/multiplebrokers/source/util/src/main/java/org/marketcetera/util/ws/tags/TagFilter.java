package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface TagFilter
{

    /**
     * Asserts that the given tag is acceptable to the receiver.
     *
     * @throws I18NException Thrown if the tag is not acceptable.
     */

    void assertMatch
        (Tag tag)
        throws I18NException;
}

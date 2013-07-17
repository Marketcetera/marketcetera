package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
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

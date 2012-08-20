package org.marketcetera.core.ws.tags;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A tag filter.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: TagFilter.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: TagFilter.java 82324 2012-04-09 20:56:08Z colin $")
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

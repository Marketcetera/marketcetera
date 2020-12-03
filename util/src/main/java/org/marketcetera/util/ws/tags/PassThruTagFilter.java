package org.marketcetera.util.ws.tags;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter that accepts all tags.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: PassThruTagFilter.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@ClassVersion("$Id: PassThruTagFilter.java 16154 2012-07-14 16:34:05Z colin $")
public class PassThruTagFilter
    implements TagFilter
{

    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag) {}
}

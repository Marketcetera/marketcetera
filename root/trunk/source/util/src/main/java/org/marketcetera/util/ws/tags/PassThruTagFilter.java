package org.marketcetera.util.ws.tags;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter that accepts all tags.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class PassThruTagFilter
    implements TagFilter
{

    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag) {}
}

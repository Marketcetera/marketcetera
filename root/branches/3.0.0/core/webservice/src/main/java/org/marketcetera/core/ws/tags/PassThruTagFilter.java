package org.marketcetera.core.ws.tags;

/**
 * A tag filter that accepts all tags.
 * 
 * @since 1.0.0
 * @version $Id: PassThruTagFilter.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class PassThruTagFilter
    implements TagFilter
{

    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag) {}
}

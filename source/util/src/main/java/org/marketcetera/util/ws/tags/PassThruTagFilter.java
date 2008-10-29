package org.marketcetera.util.ws.tags;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A tag filter that accepts all tags.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class PassThruTagFilter
    implements TagFilter
{

    // TagFilter.

    @Override
    public void assertMatch
        (Tag tag) {}
}

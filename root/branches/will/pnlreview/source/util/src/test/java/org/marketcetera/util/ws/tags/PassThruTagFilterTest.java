package org.marketcetera.util.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class PassThruTagFilterTest
    extends TagFilterTestBase
{
    @Test
    public void all()
        throws Exception
    {
        PassThruTagFilter filter=new PassThruTagFilter();
        singlePass(filter,TEST_TAG);
        singlePass(filter,null);
    }
}

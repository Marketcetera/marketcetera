package org.marketcetera.util.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: PassThruTagFilterTest.java 16154 2012-07-14 16:34:05Z colin $
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

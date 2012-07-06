package org.marketcetera.core.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: PassThruTagFilterTest.java 82324 2012-04-09 20:56:08Z colin $
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

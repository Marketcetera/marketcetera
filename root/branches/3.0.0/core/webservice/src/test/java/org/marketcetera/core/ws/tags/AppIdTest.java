package org.marketcetera.core.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: AppIdTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class AppIdTest
    extends TagTestBase
{
    @Test
    public void all()
    {
        single(new AppId(TEST_VALUE),new AppId(TEST_VALUE),new AppId());
    }
}

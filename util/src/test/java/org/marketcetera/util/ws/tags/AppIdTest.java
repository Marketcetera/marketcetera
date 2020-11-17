package org.marketcetera.util.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: AppIdTest.java 16154 2012-07-14 16:34:05Z colin $
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

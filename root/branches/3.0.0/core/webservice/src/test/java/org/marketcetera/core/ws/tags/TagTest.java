package org.marketcetera.core.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: TagTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class TagTest
    extends TagTestBase
{
    @Test
    public void all()
    {
        single(new Tag(TEST_VALUE),new Tag(TEST_VALUE),new Tag());
    }
}

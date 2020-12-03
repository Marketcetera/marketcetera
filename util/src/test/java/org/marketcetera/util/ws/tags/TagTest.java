package org.marketcetera.util.ws.tags;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: TagTest.java 16154 2012-07-14 16:34:05Z colin $
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

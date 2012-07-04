package org.marketcetera.security;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: HolderGenericTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class HolderGenericTest
    extends HolderTestBase
{
    private static final String TEST_VALUE=
        "x";


    @Test
    public void basics()
    {
        simpleNoMessage
            (new Holder<String>(),TEST_VALUE);
        simpleWithMessage
            (new Holder<String>(TestMessages.TEST_MESSAGE),TEST_VALUE);
    }
}

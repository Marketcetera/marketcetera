package org.marketcetera.util.auth;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
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

package org.marketcetera.util.log;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.junit.Test;

public class I18NMessageTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "Base msg (expected) en "+TEST_P1;
    private static final String TEST_TTL_EN=
        "Base ttl (expected) en "+TEST_P1+" "+TEST_P2;
    private static final String TEST_MSG_FR=
        "Base msg (expected) fr "+TEST_P1;
    private static final String TEST_TTL_FR=
        "Base ttl (expected) fr "+TEST_P1+" "+TEST_P2;

    private static final String TEST_MSG_EN_NULL=
        "Base msg (expected) en null";
    private static final String TEST_TTL_EN_NULL=
        "Base ttl (expected) en null null";
    private static final String TEST_MSG_FR_NULL=
        "Base msg (expected) fr null";
    private static final String TEST_TTL_FR_NULL=
        "Base ttl (expected) fr null null";

    private static final String TEST_MSG_EN_NOSUB=
        "Base msg (expected) en {0}";
    private static final String TEST_TTL_EN_NOSUB=
        "Base ttl (expected) en {0} {1}";
    private static final String TEST_MSG_FR_NOSUB=
        "Base msg (expected) fr {0}";
    private static final String TEST_TTL_FR_NOSUB=
        "Base ttl (expected) fr {0} {1}";

    @Test
    public void basic()
    {
        unboundTests
            (new I18NMessage(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void bound()
    {
        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_MSG,TEST_P1),
                   new Object[] {TEST_P1},
                   TestMessages.BASE_MSG,TEST_MSG_EN,TEST_MSG_FR);
        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_TTL,TEST_P1,TEST_P2),
                   new Object[] {TEST_P1,TEST_P2},
                   TestMessages.BASE_TTL,TEST_TTL_EN,TEST_TTL_FR);

        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_MSG,(Object)null),
                   new Object[] {null},
                   TestMessages.BASE_MSG,TEST_MSG_EN_NULL,TEST_MSG_FR_NULL);
        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_TTL,(Object)null,(Object)null),
                   new Object[] {null,null},
                   TestMessages.BASE_TTL,TEST_TTL_EN_NULL,TEST_TTL_FR_NULL);

        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_MSG,(Object[])null),
                   null,
                   TestMessages.BASE_MSG,TEST_MSG_EN_NOSUB,TEST_MSG_FR_NOSUB);
        boundTests(new I18NBoundMessageBase
                   (TestMessages.BASE_TTL,(Object[])null),
                   null,
                   TestMessages.BASE_TTL,TEST_TTL_EN_NOSUB,TEST_TTL_FR_NOSUB);
    }
}

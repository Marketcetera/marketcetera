package org.marketcetera.util.log;

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
    }
}

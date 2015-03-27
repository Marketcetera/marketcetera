package org.marketcetera.util.log;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Locale;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TI18NMessage2PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P2 msg (expected) en "+TEST_P1+" "+TEST_P2;
    private static final String TEST_TTL_EN=
        "P2 ttl (expected) en "+TEST_P1+" "+TEST_P2;
    private static final String TEST_MSG_FR=
        "P2 msg (expected) fr "+TEST_P1+" "+TEST_P2;
    private static final String TEST_TTL_FR=
        "P2 ttl (expected) fr "+TEST_P1+" "+TEST_P2;


    private static void castOverride
        (I18NMessage2P m) {}


    @Test
    public void basic()
    {
        unboundTests
            (2,
             new I18NMessage2P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage2P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessage2P
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessage2P
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessage2P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessage2P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P2_MSG.getText
             (TEST_P1,TEST_P2));
        assertEquals
            (TEST_TTL_EN,TestMessages.P2_TTL.getText
             (TEST_P1,TEST_P2));
        assertEquals
            (TEST_MSG_FR,TestMessages.P2_MSG.getText
             (Locale.FRENCH,TEST_P1,TEST_P2));
        assertEquals
            (TEST_TTL_FR,TestMessages.P2_TTL.getText
             (Locale.FRENCH,TEST_P1,TEST_P2));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P2_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_TTL.error
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_MSG.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_TTL.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_MSG.info
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_TTL.info
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_MSG.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_TTL.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_MSG.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2);

        TestMessages.P2_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2);
        TestMessages.P2_TTL.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2);
    }

    @Test
    public void bound()
    {
        Serializable[] params=new Serializable[]
            {TEST_P1,TEST_P2};
        I18NBoundMessage2P m=new I18NBoundMessage2P
            (TestMessages.P2_MSG,TEST_P1,TEST_P2);
        boundTests(m,new I18NBoundMessage2P
                   (TestMessages.P2_MSG,TEST_P1,TEST_P2),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage2P
                       (TestMessages.P2_MSG,TEST_P1,TEST_P1),
                       new I18NBoundMessage2P
                       (TestMessages.P2_TTL,TEST_P1,TEST_P2),
                       TestMessages.P0_MSG
                   },params,TestMessages.P2_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessage2P
                   (TestMessages.P2_TTL,TEST_P1,TEST_P2),
                   new I18NBoundMessage2P
                   (TestMessages.P2_TTL,TEST_P1,TEST_P2),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage2P
                       (TestMessages.P2_TTL,TEST_P1,TEST_P1),
                       new I18NBoundMessage2P
                       (TestMessages.P2_MSG,TEST_P1,TEST_P2),
                       TestMessages.P0_TTL
                   },params,TestMessages.P2_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);
        assertEquals(TEST_P1,m.getParam1());
        assertEquals(TEST_P2,m.getParam2());
    }
}

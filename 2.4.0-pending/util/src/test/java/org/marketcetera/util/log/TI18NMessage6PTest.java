package org.marketcetera.util.log;

import java.io.Serializable;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TI18NMessage6PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P6 msg (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6;
    private static final String TEST_TTL_EN=
        "P6 ttl (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6;
    private static final String TEST_MSG_FR=
        "P6 msg (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6;
    private static final String TEST_TTL_FR=
        "P6 ttl (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6;
    private static final String TEST_LOCATION=
        TI18NMessage6PTest.class.getName();


    private static void castOverride
        (I18NMessage6P m) {}


    @Test
    public void basic()
    {
        unboundTests
            (6,
             new I18NMessage6P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage6P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessage6P
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessage6P
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessage6P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessage6P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P6_MSG.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6));
        assertEquals
            (TEST_TTL_EN,TestMessages.P6_TTL.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6));
        assertEquals
            (TEST_MSG_FR,TestMessages.P6_MSG.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6));
        assertEquals
            (TEST_TTL_FR,TestMessages.P6_TTL.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P6_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P6_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P6_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P6_TTL.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P6_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P6_MSG.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P6_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P6_TTL.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P6_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P6_MSG.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P6_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P6_TTL.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P6_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P6_MSG.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P6_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P6_TTL.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P6_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P6_MSG.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P6_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P6_TTL.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
    }

    @Test
    public void bound()
    {
        Serializable[] params=new Serializable[]
            {TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6};
        I18NBoundMessage6P m=new I18NBoundMessage6P
            (TestMessages.P6_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,
             TEST_P6);
        boundTests(m,new I18NBoundMessage6P
                   (TestMessages.P6_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5,TEST_P6),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage6P
                       (TestMessages.P6_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P1),
                       new I18NBoundMessage6P
                       (TestMessages.P6_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6),
                       TestMessages.P0_MSG
                   },params,TestMessages.P6_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessage6P
                   (TestMessages.P6_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5,TEST_P6),new I18NBoundMessage6P
                   (TestMessages.P6_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5,TEST_P6),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage6P
                       (TestMessages.P6_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P1),
                       new I18NBoundMessage6P
                       (TestMessages.P6_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6),
                       TestMessages.P0_TTL
                   },params,TestMessages.P6_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);
        assertEquals(TEST_P1,m.getParam1());
        assertEquals(TEST_P2,m.getParam2());
        assertEquals(TEST_P3,m.getParam3());
        assertEquals(TEST_P4,m.getParam4());
        assertEquals(TEST_P5,m.getParam5());
        assertEquals(TEST_P6,m.getParam6());
    }
}

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

public class TI18NMessage4PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P4 msg (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4;
    private static final String TEST_TTL_EN=
        "P4 ttl (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4;
    private static final String TEST_MSG_FR=
        "P4 msg (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4;
    private static final String TEST_TTL_FR=
        "P4 ttl (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4;
    private static final String TEST_LOCATION=
        TI18NMessage4PTest.class.getName();


    private static void castOverride
        (I18NMessage4P m) {}


    @Test
    public void basic()
    {
        unboundTests
            (4,
             new I18NMessage4P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage4P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessage4P
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessage4P
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessage4P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessage4P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P4_MSG.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4));
        assertEquals
            (TEST_TTL_EN,TestMessages.P4_TTL.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4));
        assertEquals
            (TEST_MSG_FR,TestMessages.P4_MSG.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4));
        assertEquals
            (TEST_TTL_FR,TestMessages.P4_TTL.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P4_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P4_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P4_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P4_TTL.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P4_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P4_MSG.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P4_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P4_TTL.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P4_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P4_MSG.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P4_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P4_TTL.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P4_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P4_MSG.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P4_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P4_TTL.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P4_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P4_MSG.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P4_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P4_TTL.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
    }

    @Test
    public void bound()
    {
        Serializable[] params=new Serializable[]
            {TEST_P1,TEST_P2,TEST_P3,TEST_P4};
        I18NBoundMessage4P m=new I18NBoundMessage4P
            (TestMessages.P4_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4);
        boundTests(m,new I18NBoundMessage4P
                   (TestMessages.P4_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage4P
                       (TestMessages.P4_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P1),
                       new I18NBoundMessage4P
                       (TestMessages.P4_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4),
                       TestMessages.P0_MSG
                   },params,TestMessages.P4_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessage4P
                   (TestMessages.P4_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4),
                   new I18NBoundMessage4P
                   (TestMessages.P4_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage4P
                       (TestMessages.P4_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P1),
                       new I18NBoundMessage4P
                       (TestMessages.P4_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4),
                       TestMessages.P0_TTL
                   },params,TestMessages.P4_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);
        assertEquals(TEST_P1,m.getParam1());
        assertEquals(TEST_P2,m.getParam2());
        assertEquals(TEST_P3,m.getParam3());
        assertEquals(TEST_P4,m.getParam4());
    }
}
